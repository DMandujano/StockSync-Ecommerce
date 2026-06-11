<template>
  <v-card elevation="2">
    <v-card-title class="d-flex flex-column flex-sm-row justify-space-between align-sm-center ga-3">
      <span class="text-h6">Stock</span>
      <div class="d-flex flex-column flex-sm-row ga-2 w-100 w-sm-auto">
        <v-btn color="primary" to="/admin/stock/nuevo" prepend-icon="mdi-plus">
          Nuevo Stock
        </v-btn>
        <v-btn color="info" variant="tonal" prepend-icon="mdi-swap-horizontal-bold" @click="showTransfer = true">
          Transferir
        </v-btn>
      </div>
    </v-card-title>

    <v-card-text>
      <v-progress-linear v-if="loading" indeterminate color="primary" />

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
            v-model="filterWarehouse"
            :items="warehouses"
            item-title="name"
            item-value="id"
            label="Bodega"
            clearable
            variant="outlined"
            density="compact"
            hide-details
          />
        </v-col>
        <v-col cols="12" sm="4">
          <v-select
            v-model="filterStockStatus"
            :items="stockStatusOptions"
            label="Estado de Stock"
            clearable
            variant="outlined"
            density="compact"
            hide-details
          />
        </v-col>
      </v-row>

      <ResponsiveTable :loading="loading" :empty="!filteredStocks.length" empty-text="No hay registros de stock" :colspan="5">
        <template #headers>
          <tr>
            <th>Producto</th>
            <th>Bodega</th>
            <th>Cantidad</th>
            <th>Última actualización</th>
            <th>Acciones</th>
          </tr>
        </template>
        <template #body>
          <tr v-for="s in filteredStocks" :key="s.id">
            <td class="font-weight-medium">{{ s.productName }}</td>
            <td>{{ s.warehouseName }}</td>
            <td>
              <v-chip :color="chipColor(s)" size="small">
                {{ s.quantity }}
              </v-chip>
            </td>
            <td>{{ formatDate(s.lastUpdate) }}</td>
            <td>
              <v-btn
                icon="mdi-delete"
                variant="text"
                color="error"
                size="small"
                @click="confirmDelete(s)"
              />
            </td>
          </tr>
        </template>
        <template #cards>
          <v-card v-for="s in filteredStocks" :key="s.id" variant="outlined" class="mb-3">
            <v-card-title>{{ s.productName }}</v-card-title>
            <v-card-text>
              <div><strong>Bodega:</strong> {{ s.warehouseName }}</div>
              <div><strong>Cantidad:</strong> <v-chip :color="chipColor(s)" size="small">{{ s.quantity }}</v-chip></div>
              <div><strong>Última actualización:</strong> {{ formatDate(s.lastUpdate) }}</div>
            </v-card-text>
            <v-card-actions>
              <v-btn icon="mdi-delete" variant="text" color="error" size="small" @click="confirmDelete(s)" />
            </v-card-actions>
          </v-card>
        </template>
      </ResponsiveTable>
    </v-card-text>

    <v-dialog v-model="deleteDialog" max-width="400" width="95%">
      <v-card>
        <v-card-title class="text-wrap">Eliminar Stock</v-card-title>
        <v-card-text>
          ¿Estás seguro de eliminar este registro de stock?
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

    <v-dialog v-model="showTransfer" max-width="500" width="95%">
      <v-card>
        <v-card-title class="text-wrap">Transferir Stock</v-card-title>
        <v-card-text>
          <v-alert v-if="transferError" type="error" variant="tonal" class="mb-4" closable @click:close="transferError = ''">
            {{ transferError }}
          </v-alert>
          <v-select
            v-model="transferForm.productId"
            :items="products"
            item-title="name"
            item-value="id"
            label="Producto"
            class="mb-3"
          />
          <v-select
            v-model="transferForm.sourcewarehouseId"
            :items="warehouses"
            item-title="name"
            item-value="id"
            label="Bodega origen"
            class="mb-3"
          />
          <v-select
            v-model="transferForm.destinationWarehouseId"
            :items="warehouses"
            item-title="name"
            item-value="id"
            label="Bodega destino"
            class="mb-3"
          />
          <v-text-field
            v-model="transferForm.quantity"
            label="Cantidad"
            type="number"
            min="1"
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="showTransfer = false">Cancelar</v-btn>
          <v-btn color="primary" variant="tonal" @click="handleTransfer" :loading="transferring">
            Transferir
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useStockStore } from '../../stores/stock'
import { getProducts, getLowStockProducts } from '../../api/products'
import { getWarehouses } from '../../api/warehouses'
import ResponsiveTable from '../../components/ResponsiveTable.vue'

const store = useStockStore()
const stocks = ref([])
const products = ref([])
const warehouses = ref([])
const loading = ref(false)
const lowStockProductIds = ref(new Set())
const filterProduct = ref('')
const filterWarehouse = ref(null)
const filterStockStatus = ref(null)

const stockStatusOptions = [
  { title: 'Crítico (sin stock)', value: 'critical' },
  { title: 'Bajo', value: 'low' },
  { title: 'Normal', value: 'normal' },
]

const filteredStocks = computed(() => {
  let result = stocks.value

  if (filterProduct.value) {
    const q = filterProduct.value.toLowerCase()
    result = result.filter(s => s.productName?.toLowerCase().includes(q))
  }

  if (filterWarehouse.value) {
    result = result.filter(s => s.warehouseId === filterWarehouse.value)
  }

  if (filterStockStatus.value) {
    result = result.filter(s => {
      if (filterStockStatus.value === 'critical') return s.quantity === 0
      if (filterStockStatus.value === 'low') return lowStockProductIds.value.has(s.productId) && s.quantity > 0
      if (filterStockStatus.value === 'normal') return !lowStockProductIds.value.has(s.productId) && s.quantity > 0
      return true
    })
  }

  return result
})
const deleteDialog = ref(false)
const deleting = ref(false)
const stockToDelete = ref(null)
const showTransfer = ref(false)
const transferring = ref(false)
const transferError = ref('')
const transferForm = ref({
  productId: null,
  sourcewarehouseId: null,
  destinationWarehouseId: null,
  quantity: 1,
})

function formatDate(date) {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('es-CL', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

function chipColor(s) {
  if (s.quantity === 0) return 'error'
  if (lowStockProductIds.value.has(s.productId)) return 'warning'
  return 'success'
}

function confirmDelete(s) {
  stockToDelete.value = s
  deleteDialog.value = true
}

async function handleDelete() {
  deleting.value = true
  try {
    await store.remove(stockToDelete.value.id)
    stocks.value = [...store.stocks]
  } catch (e) {
    console.error('Error deleting stock:', e)
  } finally {
    deleting.value = false
    deleteDialog.value = false
    stockToDelete.value = null
  }
}

async function handleTransfer() {
  transferring.value = true
  transferError.value = ''
  try {
    await store.transfer({
      productId: Number(transferForm.value.productId),
      sourcewarehouseId: Number(transferForm.value.sourcewarehouseId),
      destinationWarehouseId: Number(transferForm.value.destinationWarehouseId),
      quantity: Number(transferForm.value.quantity),
    })
    showTransfer.value = false
    await store.fetchAll()
    stocks.value = [...store.stocks]
  } catch (e) {
    transferError.value = e.response?.data?.message || 'Error al transferir stock'
  } finally {
    transferring.value = false
  }
}

onMounted(async () => {
  loading.value = true
  try {
    const [prods, whs, lowStock] = await Promise.all([
      getProducts(),
      getWarehouses(),
      getLowStockProducts(),
    ])
    products.value = prods.data
    warehouses.value = whs.data
    lowStockProductIds.value = new Set(lowStock.data.map(p => p.id))
    await store.fetchAll()
    stocks.value = [...store.stocks]
  } finally {
    loading.value = false
  }
})
</script>


