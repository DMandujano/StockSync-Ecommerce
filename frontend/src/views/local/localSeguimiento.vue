<template>
  <v-container>
    <h1 class="text-h5 text-md-h4 font-weight-bold mb-6">
      Seguimiento de Solicitudes
    </h1>

    <v-card>
      <v-card-text>
        <v-row dense class="mb-4" align="center">
          <v-col cols="12" sm="6">
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
          <v-col cols="12" sm="6">
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
        </v-row>

        <ResponsiveTable :loading="loading" :empty="!loading && filteredSolicitudes.length === 0" empty-text="No hay solicitudes" :colspan="5">
          <template #headers>
            <tr>
              <th>ID</th>
              <th>Producto</th>
              <th>Cantidad</th>
              <th>Fecha</th>
              <th>Estado</th>
            </tr>
          </template>

          <template #body>
            <tr
                v-for="solicitud in filteredSolicitudes"
                :key="solicitud.id"
            >
              <td>#{{ solicitud.id }}</td>
              <td>{{ solicitud.productName }}</td>
              <td>{{ solicitud.quantity }}</td>
              <td>{{ new Date(solicitud.createdAt).toLocaleDateString() }}</td>

              <td>
                <v-chip
                    :color="estadoColor(solicitud.status)"
                >
                  {{ solicitud.status }}
                </v-chip>
              </td>
            </tr>
          </template>

          <template #cards>
            <v-card
                v-for="solicitud in filteredSolicitudes"
                :key="solicitud.id"
                variant="outlined"
                class="mb-3"
            >
              <v-card-title>#{{ solicitud.id }}</v-card-title>
              <v-card-text>
                <div>Producto: {{ solicitud.productName }}</div>
                <div>Cantidad: {{ solicitud.quantity }}</div>
                <div>Fecha: {{ new Date(solicitud.createdAt).toLocaleDateString() }}</div>
                <v-chip
                    :color="estadoColor(solicitud.status)"
                >
                  {{ solicitud.status }}
                </v-chip>
              </v-card-text>
            </v-card>
          </template>
        </ResponsiveTable>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useStockRequestsStore } from '../../stores/stockRequests'
import { useAuthStore } from '../../stores/auth'
import ResponsiveTable from '../../components/ResponsiveTable.vue'

const store = useStockRequestsStore()
const authStore = useAuthStore()
const loading = ref(false)
const filterProduct = ref('')
const filterStatus = ref(null)

const statusOptions = [
  { title: 'Pendiente', value: 'PENDIENTE' },
  { title: 'Aprobado', value: 'APROBADO' },
  { title: 'Enviado', value: 'ENVIADO' },
  { title: 'Recibido', value: 'RECIBIDO' },
  { title: 'Rechazado', value: 'RECHAZADO' },
]

onMounted(async () => {
  loading.value = true
  try {
    if (authStore.assignedWarehouseId) {
      await store.fetchOutgoing(authStore.assignedWarehouseId)
    }
  } finally {
    loading.value = false
  }
})

const solicitudes = computed(() => {
  return [...store.outgoingRequests]
    .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
})

const filteredSolicitudes = computed(() => {
  let result = solicitudes.value

  if (filterProduct.value) {
    const q = filterProduct.value.toLowerCase()
    result = result.filter(s => s.productName?.toLowerCase().includes(q))
  }

  if (filterStatus.value) {
    result = result.filter(s => s.status === filterStatus.value)
  }

  return result
})

function estadoColor(estado) {
  switch (estado) {
    case 'PENDIENTE':
      return 'warning'
    case 'APROBADO':
      return 'info'
    case 'ENVIADO':
      return 'primary'
    case 'RECIBIDO':
      return 'success'
    case 'RECHAZADO':
      return 'error'
    default:
      return 'grey'
  }
}
</script>