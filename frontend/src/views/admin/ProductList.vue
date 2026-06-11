<template>
  <v-card elevation="2">
    <v-card-title class="d-flex justify-space-between align-center">
      <span>Productos</span>
      <v-btn color="primary" to="/admin/productos/nuevo" prepend-icon="mdi-plus">
        Nuevo Producto
      </v-btn>
    </v-card-title>

    <v-card-text class="pa-2 pa-sm-4">
      <v-row dense class="mb-4" align="center">
        <v-col cols="12" sm="4">
          <v-select
            v-model="filterCategory"
            :items="categories"
            item-title="name"
            item-value="name"
            label="Categoría"
            clearable
            variant="outlined"
            density="compact"
          />
        </v-col>
        <v-col cols="12" sm="4">
          <v-select
            v-model="filterStock"
            :items="stockOptions"
            label="Stock"
            clearable
            variant="outlined"
            density="compact"
          />
        </v-col>
        <v-col cols="12" sm="4">
          <v-select
            v-model="filterWarehouse"
            :items="warehouses"
            item-title="name"
            item-value="id"
            label="Bodega"
            clearable
            variant="outlined"
            density="compact"
          />
        </v-col>
      </v-row>

      <ResponsiveTable
        :loading="loading"
        :empty="!filteredProducts.length"
        empty-text="No hay productos registrados"
        :colspan="9"
      >
        <template #headers>
          <tr>
            <th>Nombre</th>
            <th>SKU</th>
            <th>Categoría</th>
            <th>Precio</th>
            <th>Stock Total</th>
            <th>Stock Mínimo</th>
            <th>Bodegas</th>
            <th>Activo</th>
            <th>Acciones</th>
          </tr>
        </template>

        <template #body>
          <tr v-for="product in filteredProducts" :key="product.id">
            <td class="font-weight-medium">{{ product.name }}</td>
            <td><code>{{ product.sku }}</code></td>
            <td>{{ product.categoryName }}</td>
            <td>${{ formatPrice(product.price) }}</td>
            <td>
              <v-chip :color="product.stock <= product.minStockLevel ? 'warning' : 'success'" size="x-small">
                {{ product.stock }}
              </v-chip>
            </td>
            <td>{{ product.minStockLevel ?? 5 }}</td>
            <td>
              <template v-if="product.warehouseStocks && product.warehouseStocks.length">
                <v-chip
                  v-for="ws in product.warehouseStocks"
                  :key="ws.warehouseId"
                  size="x-small"
                  class="ma-1"
                  :color="ws.quantity > 0 ? 'success' : 'error'"
                >
                  {{ ws.warehouseName }}: {{ ws.quantity }}
                </v-chip>
              </template>
              <span v-else class="text-medium-emphasis">—</span>
            </td>
            <td>
              <v-icon :color="product.active ? 'success' : 'grey'">
                {{ product.active ? 'mdi-check-circle' : 'mdi-cancel' }}
              </v-icon>
            </td>
            <td>
              <v-btn
                icon="mdi-pencil"
                variant="text"
                color="primary"
                size="small"
                :to="`/admin/productos/editar/${product.id}`"
              />
              <v-btn
                icon="mdi-delete"
                variant="text"
                color="error"
                size="small"
                @click="confirmDelete(product)"
              />
            </td>
          </tr>
        </template>

        <template #cards>
          <v-card
            v-for="product in filteredProducts"
            :key="product.id"
            variant="outlined"
            class="mb-3"
          >
            <v-card-item>
              <template #title>
                <div class="d-flex align-center ga-2">
                  <span class="text-body-1 font-weight-medium">{{ product.name }}</span>
                  <v-icon :color="product.active ? 'success' : 'grey'" size="small">
                    {{ product.active ? 'mdi-check-circle' : 'mdi-cancel' }}
                  </v-icon>
                </div>
              </template>
            </v-card-item>

            <v-card-text class="pt-0">
              <div class="text-caption text-medium-emphasis mb-1">
                <strong>SKU:</strong> {{ product.sku }} &middot;
                <strong>Categoría:</strong> {{ product.categoryName }}
              </div>
              <div class="text-caption text-medium-emphasis mb-1">
                <strong>Precio:</strong> ${{ formatPrice(product.price) }} &middot;
                <strong>Stock:</strong>
                <v-chip
                  :color="product.stock <= product.minStockLevel ? 'warning' : 'success'"
                  size="x-small"
                  class="ml-1"
                >
                  {{ product.stock }}
                </v-chip>
              </div>
              <div class="text-caption text-medium-emphasis" v-if="product.minStockLevel">
                <strong>Stock Mínimo:</strong> {{ product.minStockLevel }}
              </div>
              <div class="mt-2" v-if="product.warehouseStocks && product.warehouseStocks.length">
                <div class="text-caption text-medium-emphasis mb-1"><strong>Bodegas:</strong></div>
                <v-chip
                  v-for="ws in product.warehouseStocks"
                  :key="ws.warehouseId"
                  size="x-small"
                  class="ma-1"
                  :color="ws.quantity > 0 ? 'success' : 'error'"
                >
                  {{ ws.warehouseName }}: {{ ws.quantity }}
                </v-chip>
              </div>
            </v-card-text>

            <v-card-actions>
              <v-btn
                variant="text"
                color="primary"
                size="small"
                :to="`/admin/productos/editar/${product.id}`"
              >
                Editar
              </v-btn>
              <v-btn
                variant="text"
                color="error"
                size="small"
                @click="confirmDelete(product)"
              >
                Eliminar
              </v-btn>
            </v-card-actions>
          </v-card>
        </template>
      </ResponsiveTable>
    </v-card-text>

    <v-dialog v-model="deleteDialog" max-width="400">
      <v-card>
        <v-card-title class="text-wrap">Eliminar Producto</v-card-title>
        <v-card-text>
          ¿Estás seguro de eliminar "{{ productToDelete?.name }}"?
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="deleteDialog = false">Cancelar</v-btn>
          <v-btn color="error" variant="tonal" @click="handleDelete" :loading="deleting">
            Eliminar
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useProductsStore } from '../../stores/products'
import { useCategoriesStore } from '../../stores/categories'
import { useWarehousesStore } from '../../stores/warehouses'
import ResponsiveTable from '../../components/ResponsiveTable.vue'

const store = useProductsStore()
const categoriesStore = useCategoriesStore()
const warehousesStore = useWarehousesStore()

const products = ref([])
const categories = ref([])
const warehouses = ref([])
const loading = ref(false)
const deleteDialog = ref(false)
const deleting = ref(false)
const productToDelete = ref(null)

const filterCategory = ref(null)
const filterStock = ref(null)
const filterWarehouse = ref(null)

const stockOptions = [
  { title: 'Stock Bajo', value: 'low' },
  { title: 'Sin Stock', value: 'none' },
  { title: 'Con Stock', value: 'available' },
]

const filteredProducts = computed(() => {
  let result = products.value

  if (filterCategory.value) {
    result = result.filter(p => p.categoryName === filterCategory.value)
  }

  if (filterStock.value) {
    result = result.filter(p => {
      if (filterStock.value === 'low') return p.stock <= p.minStockLevel
      if (filterStock.value === 'none') return p.stock === 0
      if (filterStock.value === 'available') return p.stock > 0
      return true
    })
  }

  if (filterWarehouse.value) {
    result = result.filter(p =>
      p.warehouseStocks?.some(ws => ws.warehouseId === filterWarehouse.value)
    )
  }

  return result
})

function formatPrice(price) {
  return Number(price).toLocaleString('es-CL')
}

function confirmDelete(product) {
  productToDelete.value = product
  deleteDialog.value = true
}

async function handleDelete() {
  deleting.value = true
  try {
    await store.remove(productToDelete.value.id)
    products.value = [...store.products]
  } catch (e) {
    console.error('Error deleting product:', e)
  } finally {
    deleting.value = false
    deleteDialog.value = false
    productToDelete.value = null
  }
}

onMounted(async () => {
  loading.value = true
  try {
    await Promise.all([
      store.fetchAll(),
      categoriesStore.fetchAll(),
      warehousesStore.fetchAll(),
    ])
    products.value = [...store.products]
    categories.value = [...categoriesStore.categories]
    warehouses.value = [...warehousesStore.warehouses]
  } finally {
    loading.value = false
  }
})
</script>
