<template>
  <v-row class="mx-0">
    <v-col cols="12" sm="4">
      <v-card class="pa-6 text-center" elevation="2">
        <v-icon size="48" color="primary" class="mb-3">mdi-package-variant-closed</v-icon>
        <div class="text-h3 font-weight-bold">{{ totalProducts }}</div>
        <div class="text-body-1 text-medium-emphasis">Productos</div>
      </v-card>
    </v-col>
    <v-col cols="12" sm="4">
      <v-card class="pa-6 text-center" elevation="2">
        <v-icon size="48" color="primary" class="mb-3">mdi-warehouse</v-icon>
        <div class="text-h3 font-weight-bold">{{ totalWarehouses }}</div>
        <div class="text-body-1 text-medium-emphasis">Bodegas</div>
      </v-card>
    </v-col>
    <v-col cols="12" sm="4">
      <v-card class="pa-6 text-center" elevation="2">
        <v-icon size="48" color="primary" class="mb-3">mdi-chart-box</v-icon>
        <div class="text-h3 font-weight-bold">{{ totalStocks }}</div>
        <div class="text-body-1 text-medium-emphasis">Registros de Stock</div>
      </v-card>
    </v-col>
  </v-row>

  <v-row class="mx-0 mt-4" v-if="lowStockProducts.length">
    <v-col cols="12">
      <v-card elevation="2" color="error" variant="tonal">
        <v-card-title class="text-error font-weight-bold">
          <v-icon class="mr-2">mdi-alert-circle</v-icon>
          Alertas de Bajo Stock
        </v-card-title>
        <v-card-text>
          <ResponsiveTable :empty="!lowStockProducts.length" empty-text="No hay productos con stock bajo" :colspan="4">
            <template #headers>
              <tr>
                <th>Nombre</th>
                <th>Categoría</th>
                <th>Stock Mínimo</th>
                <th>Stock Actual</th>
              </tr>
            </template>
            <template #body>
              <tr v-for="p in lowStockProducts" :key="p.id">
                <td>{{ p.name }}</td>
                <td>{{ p.categoryName }}</td>
                <td>{{ p.minStockLevel || 5 }}</td>
                <td>
                  <v-chip color="error" size="small" class="font-weight-bold">
                    {{ p.stock }}
                  </v-chip>
                </td>
              </tr>
            </template>
            <template #cards>
              <v-card v-for="p in lowStockProducts" :key="p.id" variant="outlined" class="mb-3">
                <v-card-title>{{ p.name }}</v-card-title>
                <v-card-text>
                  <div><strong>Categoría:</strong> {{ p.categoryName }}</div>
                  <div><strong>Stock Mínimo:</strong> {{ p.minStockLevel || 5 }}</div>
                  <div><strong>Stock Actual:</strong> <v-chip color="error" size="small" class="font-weight-bold">{{ p.stock }}</v-chip></div>
                </v-card-text>
              </v-card>
            </template>
          </ResponsiveTable>
        </v-card-text>
      </v-card>
    </v-col>
  </v-row>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getProducts, getLowStockProducts } from '../../api/products'
import { getWarehouses } from '../../api/warehouses'
import { getStocks } from '../../api/stock'
import ResponsiveTable from '../../components/ResponsiveTable.vue'

const totalProducts = ref(0)
const totalWarehouses = ref(0)
const totalStocks = ref(0)
const lowStockProducts = ref([])

onMounted(async () => {
  try {
    const [prods, whs, stks, lowStock] = await Promise.all([
      getProducts(),
      getWarehouses(),
      getStocks(),
      getLowStockProducts(),
    ])
    totalProducts.value = prods.data.length
    totalWarehouses.value = whs.data.length
    totalStocks.value = stks.data.length
    
    lowStockProducts.value = lowStock.data
  } catch (e) {
    console.error('Error loading dashboard:', e)
  }
})
</script>
