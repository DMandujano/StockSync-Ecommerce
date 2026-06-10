<template>
  <v-container fluid>
    <h1 class="text-h4 mb-6">
      Recepción de Productos
    </h1>

    <v-row>
      <v-col
          v-for="item in recepciones"
          :key="item.id"
          cols="12"
      >
        <v-card>
          <v-card-title>
            Solicitud #{{ item.id }}
          </v-card-title>

          <v-card-text>
            <p><strong>Producto:</strong> {{ item.productName }}</p>
            <p><strong>Cantidad:</strong> {{ item.quantity }}</p>
            <p><strong>SKU:</strong> {{ item.sku }}</p>

            <v-chip color="primary" class="mt-2">
              En Camino
            </v-chip>
          </v-card-text>

          <v-card-actions>
            <v-btn
                color="success"
                @click="confirmar(item)"
                :loading="loadingId === item.id"
            >
              Confirmar Recepción
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>

      <v-col v-if="loading">
        <p class="text-center">Cargando...</p>
      </v-col>
      <v-col v-else-if="recepciones.length === 0">
        <p class="text-center text-medium-emphasis">No hay envíos pendientes.</p>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { useStockRequestsStore } from '../../stores/stockRequests'

const authStore = useAuthStore()
const store = useStockRequestsStore()

const loading = ref(false)
const loadingId = ref(null)

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
  return store.incomingRequests.filter(r => r.status === 'ENVIADO')
})

async function confirmar(item) {
  loadingId.value = item.id
  try {
    await store.receiveByScanner(item.sku, authStore.assignedWarehouseId)
    await store.fetchIncoming(authStore.assignedWarehouseId)
  } catch (error) {
    console.error('Error al confirmar recepción', error)
    alert('Error al confirmar recepción')
  } finally {
    loadingId.value = null
  }
}
</script>