<template>
  <v-container fluid class="pa-6">

    <div class="mb-6">
      <h1 class="text-h5 text-md-h4 font-weight-bold">
        Recepciones y Observaciones
      </h1>

      <p class="text-medium-emphasis">
        Revisa las confirmaciones realizadas por los locales y las observaciones reportadas.
      </p>
    </div>

    <!-- Resumen -->
    <v-row class="mx-0 mb-6">

      <v-col cols="12" sm="4">
        <v-card class="text-center pa-4">
          <v-icon
              size="50"
              color="success"
          >
            mdi-check-circle
          </v-icon>

          <div class="text-subtitle-1 mt-2">
            Recepciones Correctas
          </div>

          <div class="text-h5 text-md-h4 font-weight-bold">
            {{ correctasCount }}
          </div>
        </v-card>
      </v-col>

      <v-col cols="12" sm="4">
        <v-card class="text-center pa-4">
          <v-icon
              size="50"
              color="warning"
          >
            mdi-alert-circle
          </v-icon>

          <div class="text-subtitle-1 mt-2">
            Con Observaciones
          </div>

          <div class="text-h5 text-md-h4 font-weight-bold">
            {{ observacionesCount }}
          </div>
        </v-card>
      </v-col>

      <v-col cols="12" sm="4">
        <v-card class="text-center pa-4">
          <v-icon
              size="50"
              color="info"
          >
            mdi-truck-delivery
          </v-icon>

          <div class="text-subtitle-1 mt-2">
            Total Entregas
          </div>

          <div class="text-h5 text-md-h4 font-weight-bold">
            {{ totalEntregasCount }}
          </div>
        </v-card>
      </v-col>

    </v-row>

    <!-- Tabla -->
    <v-card elevation="4">

      <v-card-title class="text-wrap">
        Historial de Recepciones
      </v-card-title>

      <v-divider />

      <v-row dense class="mb-4 mt-2" align="center">
        <v-col cols="12" sm="6">
          <v-text-field
            v-model="filterLocal"
            label="Buscar local"
            prepend-inner-icon="mdi-magnify"
            clearable
            variant="outlined"
            density="compact"
            hide-details
          />
        </v-col>
        <v-col cols="12" sm="3">
          <v-text-field
            v-model="filterDateFrom"
            label="Desde"
            type="date"
            variant="outlined"
            density="compact"
            hide-details
            clearable
          />
        </v-col>
        <v-col cols="12" sm="3">
          <v-text-field
            v-model="filterDateTo"
            label="Hasta"
            type="date"
            variant="outlined"
            density="compact"
            hide-details
            clearable
          />
        </v-col>
      </v-row>

      <ResponsiveTable
          :loading="loading"
          :empty="recepciones.length === 0"
          empty-text="No hay recepciones registradas"
          colspan="5"
      >
        <template #headers>
          <tr>
            <th>Despacho</th>
            <th>Local</th>
            <th>Fecha</th>
            <th>Estado</th>
            <th>Observación</th>
          </tr>
        </template>

        <template #body>
          <tr
              v-for="item in recepciones"
              :key="item.id"
          >
            <td>{{ item.id }}</td>
            <td>{{ item.destinationWarehouseName }}</td>
            <td>{{ new Date(item.updatedAt || item.createdAt).toLocaleDateString() }}</td>
            <td>
              <v-chip
                  color="success"
                  variant="tonal"
              >
                Recibido
              </v-chip>
            </td>
            <td>
              <span class="text-success">
                Sin observaciones
              </span>
            </td>
          </tr>
        </template>

        <template #cards>
          <v-card
              v-for="item in recepciones"
              :key="item.id"
              variant="outlined"
              class="mb-3"
          >
            <v-card-title class="font-weight-bold">
              Despacho #{{ item.id }}
            </v-card-title>
            <v-card-text>
              <div><strong>Local:</strong> {{ item.destinationWarehouseName }}</div>
              <div><strong>Fecha:</strong> {{ new Date(item.updatedAt || item.createdAt).toLocaleDateString() }}</div>
              <div>
                <strong>Estado:</strong>
                <v-chip
                    color="success"
                    variant="tonal"
                    size="small"
                    class="ml-1"
                >
                  Recibido
                </v-chip>
              </div>
              <div class="mt-1"><strong>Observación:</strong> Sin observaciones</div>
            </v-card-text>
          </v-card>
        </template>
      </ResponsiveTable>

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
const filterLocal = ref('')
const filterDateFrom = ref('')
const filterDateTo = ref('')

onMounted(async () => {
  loading.value = true
  try {
    if (authStore.assignedWarehouseId) {
      await store.fetchIncoming(authStore.assignedWarehouseId)
    }
  } finally {
    loading.value = false
  }
})

const recepciones = computed(() => {
  let result = store.incomingRequests.filter(r => r.status === 'RECIBIDO')

  if (filterLocal.value) {
    const q = filterLocal.value.toLowerCase()
    result = result.filter(r => r.destinationWarehouseName?.toLowerCase().includes(q))
  }

  if (filterDateFrom.value) {
    const from = new Date(filterDateFrom.value)
    from.setHours(0, 0, 0, 0)
    result = result.filter(r => new Date(r.updatedAt || r.createdAt) >= from)
  }

  if (filterDateTo.value) {
    const to = new Date(filterDateTo.value)
    to.setHours(23, 59, 59, 999)
    result = result.filter(r => new Date(r.updatedAt || r.createdAt) <= to)
  }

  return result
})

const correctasCount = computed(() => recepciones.value.length)
const observacionesCount = computed(() => 0) // El backend actual no soporta observaciones en StockRequest
const totalEntregasCount = computed(() => correctasCount.value + observacionesCount.value)

</script>

<style scoped>

.v-card {
  border-radius: 16px;
}

</style>