<template>
  <v-card elevation="2">
    <v-card-title class="d-flex justify-space-between align-center">
      <span>{{ isLocalView ? 'Visor de Locales' : 'Visor de Bodegas' }}</span>
    </v-card-title>

    <v-card-text>
      <v-alert v-if="!warehouses.length && !loading" type="info" variant="tonal" class="mb-4">
        No hay almacenes registrados en el sistema.
      </v-alert>

      <v-progress-linear v-if="loading" indeterminate color="primary" class="mb-4" />

      <v-row v-if="warehouses.length > 0">
        <v-col v-for="wh in warehouses" :key="wh.id" cols="12" sm="6" md="4">
          <v-card variant="outlined" class="h-100 d-flex flex-column">
            <v-card-item>
              <template v-slot:prepend>
                <v-icon :icon="isLocalView ? 'mdi-store' : 'mdi-warehouse'" size="large" color="primary" class="mr-3" />
              </template>
              <v-card-title>{{ wh.name }}</v-card-title>
              <v-card-subtitle>Cód: {{ wh.code }}</v-card-subtitle>
            </v-card-item>

            <v-card-text class="flex-grow-1">
              <div class="d-flex align-center mb-1">
                <v-icon icon="mdi-map-marker" size="small" class="mr-2 opacity-70" />
                <span class="text-body-2">{{ wh.address || 'Sin dirección' }}</span>
              </div>
              <div class="d-flex align-center">
                <v-icon icon="mdi-city" size="small" class="mr-2 opacity-70" />
                <span class="text-body-2">{{ wh.city || 'Sin ciudad' }}</span>
              </div>
            </v-card-text>

            <v-divider />

            <v-card-actions class="pa-3">
              <v-btn
                color="primary"
                variant="elevated"
                block
                prepend-icon="mdi-login-variant"
                @click="entrarComo(wh.id)"
              >
                Entrar como {{ isLocalView ? 'Local' : 'Bodega' }}
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
    </v-card-text>
  </v-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWarehousesStore } from '../../stores/warehouses'
import { useAuthStore } from '../../stores/auth'

const route = useRoute()
const router = useRouter()
const store = useWarehousesStore()
const authStore = useAuthStore()

const loading = ref(false)
const warehouses = ref([])

// Check if current route is for local or bodega view
const isLocalView = computed(() => route.path.includes('/visores/local'))

onMounted(async () => {
  loading.value = true
  try {
    await store.fetchAll()
    // For now we show all warehouses since there is no strict database division by type.
    // If needed, they could be filtered by finding Users with assigned roles. 
    // Here we list all so the admin can choose any warehouse.
    warehouses.value = store.warehouses
  } finally {
    loading.value = false
  }
})

function entrarComo(warehouseId) {
  authStore.setAdminViewWarehouseId(warehouseId)
  if (isLocalView.value) {
    router.push('/local')
  } else {
    router.push('/bodega')
  }
}
</script>
