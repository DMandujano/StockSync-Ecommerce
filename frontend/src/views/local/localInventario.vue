<template>

  <v-card>
    <h1 class="text-h5 text-md-h4 mb-6">Inventario del Local</h1>
    <v-card-text>
      <v-alert
          type="warning"
          variant="tonal"
          class="mb-4"
      >
        Productos con bajo stock: {{ bajoStock.length }}
      </v-alert>

      <!-- Bajo stock -->
      <v-card
          variant="outlined"
          class="mb-6"
      >
        <v-card-title class="text-wrap">
          Productos que requieren reposición
        </v-card-title>

        <ResponsiveTable :empty="!bajoStock.length" empty-text="No hay productos críticos" :colspan="2">
          <template #headers>
            <tr>
              <th>Producto</th>
              <th>Stock</th>
            </tr>
          </template>

          <template #body>
            <tr
                v-for="producto in bajoStock"
                :key="producto.id"
            >
              <td>{{ producto.productName }}</td>
              <td>
                <v-chip color="error">
                  {{ producto.quantity }}
                </v-chip>
              </td>
            </tr>
          </template>

          <template #cards>
            <v-card
                v-for="producto in bajoStock"
                :key="producto.id"
                variant="outlined"
                class="mb-3"
            >
              <v-card-title>{{ producto.productName }}</v-card-title>
              <v-card-text>
                <v-chip color="error">{{ producto.quantity }}</v-chip>
              </v-card-text>
            </v-card>
          </template>
        </ResponsiveTable>
      </v-card>

      <!-- Filtros -->
      <v-row dense class="mb-4" align="center">
        <v-col cols="12" sm="4">
          <v-text-field
            v-model="filterProduct"
            label="Buscar producto"
            prepend-inner-icon="mdi-magnify"
            clearable
            variant="outlined"
            density="compact"
            hide-details
          />
        </v-col>
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
            hide-details
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
            hide-details
          />
        </v-col>
      </v-row>

      <!-- Inventario completo -->
      <v-card-title class="text-wrap">
        Inventario
      </v-card-title>
      <ResponsiveTable :empty="!filteredProducts.length" empty-text="No hay productos en inventario" :colspan="5">
        <template #headers>
          <tr>
            <th>Producto</th>
            <th>SKU</th>
            <th>Categoría</th>
            <th>Stock</th>
            <th>Estado</th>
          </tr>
        </template>

        <template #body>
          <tr
              v-for="producto in filteredProducts"
              :key="producto.id"
          >
            <td>{{ producto.productName }}</td>
            <td>{{ producto.sku }}</td>
            <td>{{ producto.categoryName || 'N/A' }}</td>

            <td>
              {{ producto.quantity }}
            </td>

            <td>
              <v-chip
                  :color="producto.quantity <= 10 ? 'error' : 'success'"
                  size="small"
              >
                {{ producto.quantity <= 10 ? 'Bajo Stock' : 'Disponible' }}
              </v-chip>
            </td>
          </tr>
        </template>

        <template #cards>
          <v-card
              v-for="producto in filteredProducts"
              :key="producto.id"
              variant="outlined"
              class="mb-3"
          >
            <v-card-title>{{ producto.productName }}</v-card-title>
            <v-card-text>
              <div>SKU: {{ producto.sku }}</div>
              <div>Categoría: {{ producto.categoryName || 'N/A' }}</div>
              <div>Stock: {{ producto.quantity }}</div>
              <v-chip
                  :color="producto.quantity <= 10 ? 'error' : 'success'"
                  size="small"
              >
                {{ producto.quantity <= 10 ? 'Bajo Stock' : 'Disponible' }}
              </v-chip>
            </v-card-text>
          </v-card>
        </template>
      </ResponsiveTable>

    </v-card-text>
  </v-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useStockStore } from '../../stores/stock'
import { useAuthStore } from '../../stores/auth'
import { useCategoriesStore } from '../../stores/categories'
import ResponsiveTable from '../../components/ResponsiveTable.vue'

const store = useStockStore()
const authStore = useAuthStore()
const categoriesStore = useCategoriesStore()

const products = ref([])
const categories = ref([])
const loading = ref(false)
const filterProduct = ref('')
const filterCategory = ref(null)
const filterStock = ref(null)

const stockOptions = [
  { title: 'Bajo Stock', value: 'low' },
  { title: 'Disponible', value: 'available' },
]

const filteredProducts = computed(() => {
  let result = products.value

  if (filterProduct.value) {
    const q = filterProduct.value.toLowerCase()
    result = result.filter(p =>
      p.productName?.toLowerCase().includes(q) ||
      p.sku?.toLowerCase().includes(q)
    )
  }

  if (filterCategory.value) {
    result = result.filter(p => p.categoryName === filterCategory.value)
  }

  if (filterStock.value) {
    result = result.filter(p => {
      if (filterStock.value === 'low') return p.quantity <= 10
      if (filterStock.value === 'available') return p.quantity > 10
      return true
    })
  }

  return result
})

const bajoStock = computed(() =>
    products.value.filter(product => product.quantity <= 10)
)

onMounted(async () => {
  loading.value = true
  try {
    if (authStore.assignedWarehouseId) {
      await Promise.all([
        store.fetchByWarehouse(authStore.assignedWarehouseId).then(d => products.value = d),
        categoriesStore.fetchAll().then(() => categories.value = [...categoriesStore.categories]),
      ])
    }
  } finally {
    loading.value = false
  }
})
</script>