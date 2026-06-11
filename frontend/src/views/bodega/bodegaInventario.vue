<template>
  <v-container fluid class="pa-6">

    <!-- Header -->
    <div class="mb-6">
      <h1 class="text-h5 text-md-h4 font-weight-bold">
        Inventario de Bodega
      </h1>

      <p class="text-medium-emphasis">
        Consulta el stock disponible para abastecer los locales.
      </p>
    </div>

    <!-- Resumen -->
    <v-row class="mx-0 mb-4">

      <v-col cols="12" sm="6" md="3">
        <v-card class="text-center pa-4">
          <v-icon
              size="50"
              color="primary"
          >
            mdi-package-variant
          </v-icon>

          <div class="text-subtitle-1 mt-2">
            Productos
          </div>

          <div class="text-h5 text-md-h4 font-weight-bold">
            {{ productos.length }}
          </div>
        </v-card>
      </v-col>

      <v-col cols="12" sm="6" md="3">
        <v-card class="text-center pa-4">
          <v-icon
              size="50"
              color="warning"
          >
            mdi-alert
          </v-icon>

          <div class="text-subtitle-1 mt-2">
            Stock Bajo
          </div>

          <div class="text-h5 text-md-h4 font-weight-bold">
            {{ stockBajo }}
          </div>
        </v-card>
      </v-col>

    </v-row>

    <v-row dense class="mb-4" align="center">
      <v-col cols="12" sm="6">
        <v-text-field
            v-model="busqueda"
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
            label="Estado de Stock"
            clearable
            variant="outlined"
            density="compact"
            hide-details
        />
      </v-col>
    </v-row>

    <!-- Tabla -->
    <v-card>

      <v-card-title class="text-wrap">
        Inventario General
      </v-card-title>

      <v-divider />

      <ResponsiveTable
          :loading="loading"
          :empty="productosFiltrados.length === 0"
          empty-text="No hay productos registrados"
          colspan="5"
      >
        <template #headers>
          <tr>
            <th>Producto</th>
            <th>SKU</th>
            <th>Ubicación</th>
            <th>Stock</th>
            <th>Estado</th>
          </tr>
        </template>

        <template #body>
          <tr
              v-for="producto in productosFiltrados"
              :key="producto.id"
          >
            <td>{{ producto.productName }}</td>
            <td>{{ producto.sku }}</td>
            <td>{{ producto.warehouseName }}</td>
            <td>{{ producto.quantity }}</td>
            <td>
              <v-chip
                  :color="estadoColor(producto.quantity)"
                  size="small"
                  variant="tonal"
              >
                {{ estadoTexto(producto.quantity) }}
              </v-chip>
            </td>
          </tr>
        </template>

        <template #cards>
          <v-card
              v-for="producto in productosFiltrados"
              :key="producto.id"
              variant="outlined"
              class="mb-3"
          >
            <v-card-title class="font-weight-bold">
              {{ producto.productName }}
            </v-card-title>
            <v-card-text>
              <div><strong>SKU:</strong> {{ producto.sku }}</div>
              <div><strong>Ubicación:</strong> {{ producto.warehouseName }}</div>
              <div>
                <strong>Stock:</strong>
                <v-chip
                    :color="estadoColor(producto.quantity)"
                    size="small"
                    variant="tonal"
                >
                  {{ producto.quantity }}
                </v-chip>
              </div>
              <div class="mt-1">
                <strong>Estado:</strong>
                <v-chip
                    :color="estadoColor(producto.quantity)"
                    size="small"
                    variant="tonal"
                >
                  {{ estadoTexto(producto.quantity) }}
                </v-chip>
              </div>
            </v-card-text>
          </v-card>
        </template>
      </ResponsiveTable>

    </v-card>

  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useStockStore } from '../../stores/stock'
import { useAuthStore } from '../../stores/auth'
import ResponsiveTable from '../../components/ResponsiveTable.vue'

const stockStore = useStockStore()
const authStore = useAuthStore()

const busqueda = ref('')
const filterStatus = ref(null)
const productos = ref([])
const loading = ref(false)

const statusOptions = [
  { title: 'Crítico', value: 'critical' },
  { title: 'Bajo', value: 'low' },
  { title: 'Disponible', value: 'available' },
]

onMounted(async () => {
  loading.value = true
  try {
    if (authStore.assignedWarehouseId) {
      productos.value = await stockStore.fetchByWarehouse(authStore.assignedWarehouseId)
    }
  } catch (error) {
    console.error('Error fetching warehouse stock:', error)
  } finally {
    loading.value = false
  }
})

const productosFiltrados = computed(() => {
  let result = productos.value

  const search = busqueda.value || ''
  if (search) {
    result = result.filter(p =>
        p.productName?.toLowerCase().includes(search.toLowerCase()) ||
        p.sku?.toLowerCase().includes(search.toLowerCase())
    )
  }

  if (filterStatus.value) {
    result = result.filter(p => {
      if (filterStatus.value === 'critical') return p.quantity <= 10
      if (filterStatus.value === 'low') return p.quantity > 10 && p.quantity <= 20
      if (filterStatus.value === 'available') return p.quantity > 20
      return true
    })
  }

  return result
})

const stockBajo = computed(() => {
  return productos.value.filter(p => p.quantity <= 20).length
})

function estadoColor(stock) {
  if (stock <= 10) return 'error'
  if (stock <= 20) return 'warning'
  return 'success'
}

function estadoTexto(stock) {
  if (stock <= 10) return 'Crítico'
  if (stock <= 20) return 'Bajo'
  return 'Disponible'
}
</script>

<style scoped>

.v-card {
  border-radius: 16px;
}

.v-table tbody tr:hover {
  background-color: rgba(255,255,255,0.04);
}

@media (max-width: 600px) {

  .text-h4 {
    font-size: 1.8rem;
  }

}

</style>