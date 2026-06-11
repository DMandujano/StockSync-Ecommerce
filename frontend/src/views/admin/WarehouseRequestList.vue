<template>
  <v-card elevation="2">
    <v-card-title class="d-flex flex-column flex-sm-row justify-space-between align-sm-center ga-3">
      <span>Solicitudes a Bodega</span>
      <v-dialog v-model="createDialog" max-width="500" width="95%">
        <template v-slot:activator="{ props }">
          <v-btn color="primary" v-bind="props" prepend-icon="mdi-plus" class="create-btn">
            Nueva Solicitud
          </v-btn>
        </template>
        <v-card>
          <v-card-title class="text-wrap">Crear Solicitud</v-card-title>
          <v-card-text>
            <v-form @submit.prevent="handleCreate" ref="form">
              <v-select
                v-model="newRequest.productId"
                :items="products"
                item-title="name"
                item-value="id"
                label="Producto"
                required
              />
              <v-select
                v-model="newRequest.sourceWarehouseId"
                :items="warehouses"
                item-title="name"
                item-value="id"
                label="Bodega Origen (la que provee)"
                required
              />
              <v-select
                v-if="auth.isAdmin"
                v-model="newRequest.destinationWarehouseId"
                :items="allWarehouses"
                item-title="name"
                item-value="id"
                label="Local Destino"
                required
              />
              <v-text-field
                v-model.number="newRequest.quantity"
                type="number"
                label="Cantidad"
                required
                min="1"
              />
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-spacer />
            <v-btn variant="text" @click="createDialog = false">Cancelar</v-btn>
            <v-btn color="primary" @click="handleCreate" :loading="creating">
              Solicitar
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
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
            v-model="filterStatus"
            :items="statusOptions"
            label="Estado"
            clearable
            variant="outlined"
            density="compact"
            hide-details
          />
        </v-col>
        <v-col cols="12" sm="4">
          <v-select
            v-model="filterWarehouse"
            :items="allWarehouses"
            item-title="name"
            item-value="id"
            label="Bodega"
            clearable
            variant="outlined"
            density="compact"
            hide-details
          />
        </v-col>
      </v-row>

      <ResponsiveTable :loading="loading" :empty="!filteredRequests.length" empty-text="No hay solicitudes registradas." :colspan="6">
        <template #headers>
          <tr>
            <th>Producto (SKU)</th>
            <th>De Bodega</th>
            <th>Hacia Local</th>
            <th>Cantidad</th>
            <th>Estado</th>
            <th>Fecha</th>
          </tr>
        </template>
        <template #body>
          <tr v-for="req in filteredRequests" :key="req.id">
            <td>{{ req.productName }} <span class="text-caption text-medium-emphasis">({{ req.sku }})</span></td>
            <td>{{ req.sourceWarehouseName }}</td>
            <td>{{ req.destinationWarehouseName }}</td>
            <td>{{ req.quantity }}</td>
            <td>
              <v-chip :color="getStatusColor(req.status)" size="small">
                {{ req.status }}
              </v-chip>
            </td>
            <td>{{ new Date(req.createdAt).toLocaleDateString() }}</td>
          </tr>
        </template>
        <template #cards>
          <v-card v-for="req in filteredRequests" :key="req.id" variant="outlined" class="mb-3">
            <v-card-title>{{ req.productName }}</v-card-title>
            <v-card-text>
              <div><strong>SKU:</strong> {{ req.sku }}</div>
              <div><strong>De Bodega:</strong> {{ req.sourceWarehouseName }}</div>
              <div><strong>Hacia Local:</strong> {{ req.destinationWarehouseName }}</div>
              <div><strong>Cantidad:</strong> {{ req.quantity }}</div>
              <div><strong>Estado:</strong> <v-chip :color="getStatusColor(req.status)" size="small">{{ req.status }}</v-chip></div>
              <div><strong>Fecha:</strong> {{ new Date(req.createdAt).toLocaleDateString() }}</div>
            </v-card-text>
          </v-card>
        </template>
      </ResponsiveTable>
    </v-card-text>
  </v-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getRequestsByDestination, createStockRequest, getAllRequests } from '../../api/stockRequests'
import { getProducts } from '../../api/products'
import { getWarehouses } from '../../api/warehouses'
import { useAuthStore } from '../../stores/auth'
import ResponsiveTable from '../../components/ResponsiveTable.vue'

const auth = useAuthStore()
const requests = ref([])
const products = ref([])
const warehouses = ref([])
const allWarehouses = ref([])
const loading = ref(false)
const filterProduct = ref('')
const filterStatus = ref(null)
const filterWarehouse = ref(null)

const statusOptions = [
  { title: 'Pendiente', value: 'PENDIENTE' },
  { title: 'Enviado', value: 'ENVIADO' },
  { title: 'Recibido', value: 'RECIBIDO' },
  { title: 'Rechazado', value: 'RECHAZADO' },
]

const filteredRequests = computed(() => {
  let result = requests.value

  if (filterProduct.value) {
    const q = filterProduct.value.toLowerCase()
    result = result.filter(r =>
      r.productName?.toLowerCase().includes(q) ||
      r.sku?.toLowerCase().includes(q)
    )
  }

  if (filterStatus.value) {
    result = result.filter(r => r.status === filterStatus.value)
  }

  if (filterWarehouse.value) {
    result = result.filter(r =>
      r.sourceWarehouseId === filterWarehouse.value ||
      r.destinationWarehouseId === filterWarehouse.value
    )
  }

  return result
})

const createDialog = ref(false)
const creating = ref(false)
const form = ref(null)

const newRequest = ref({
  productId: null,
  sourceWarehouseId: null,
  destinationWarehouseId: null, // Se asigna automáticamente
  quantity: 1
})

function getStatusColor(status) {
  const map = {
    'PENDIENTE': 'warning',
    'ENVIADO': 'info',
    'RECIBIDO': 'success',
    'RECHAZADO': 'error'
  }
  return map[status] || 'grey'
}

async function fetchRequests() {
  if (!auth.isAdmin && !auth.user?.assignedWarehouse?.id) {
    console.warn("Usuario no tiene bodega asignada")
    return
  }
  loading.value = true
  try {
    const res = auth.isAdmin 
      ? await getAllRequests() 
      : await getRequestsByDestination(auth.user.assignedWarehouse.id)
    requests.value = res.data
  } catch (e) {
    console.error('Error fetching requests', e)
  } finally {
    loading.value = false
  }
}

async function fetchDependencies() {
  try {
    const [pRes, wRes] = await Promise.all([getProducts(), getWarehouses()])
    products.value = pRes.data
    allWarehouses.value = wRes.data
    warehouses.value = auth.isAdmin ? wRes.data : wRes.data.filter(w => w.id !== auth.user?.assignedWarehouse?.id)
  } catch (e) {
    console.error('Error fetching deps', e)
  }
}

async function handleCreate() {
  if (!newRequest.value.productId || !newRequest.value.sourceWarehouseId) return
  if (auth.isAdmin && !newRequest.value.destinationWarehouseId) return
  
  creating.value = true
  if (!auth.isAdmin) {
    newRequest.value.destinationWarehouseId = auth.user.assignedWarehouse.id
  }
  
  try {
    await createStockRequest(newRequest.value)
    createDialog.value = false
    newRequest.value = {
      productId: null,
      sourceWarehouseId: null,
      destinationWarehouseId: null,
      quantity: 1
    }
    await fetchRequests()
  } catch (e) {
    console.error('Error creating request', e)
    alert('Error: ' + (e.response?.data?.message || e.message))
  } finally {
    creating.value = false
  }
}

onMounted(() => {
  fetchRequests()
  fetchDependencies()
})
</script>


