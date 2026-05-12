<template>
  <v-main>
    <PublicHeader />

    <v-container class="py-12">
      <v-row class="text-center mb-12">
        <v-col>
          <h1 class="text-h3 font-weight-bold mb-4">
            StockSync
          </h1>
          <p class="text-h6 text-medium-emphasis mb-8">
            Inventario sin errores, negocio sin límites.
          </p>
        </v-col>
      </v-row>

      <v-row justify="center" class="mb-12">
        <v-col cols="12" sm="4">
          <v-card class="text-center pa-6" elevation="2">
            <v-icon size="48" color="primary" class="mb-4">mdi-package-variant-closed</v-icon>
            <div class="text-h4 font-weight-bold">{{ totalProducts }}</div>
            <div class="text-body-1 text-medium-emphasis">Productos</div>
          </v-card>
        </v-col>
        <v-col cols="12" sm="4">
          <v-card class="text-center pa-6" elevation="2">
            <v-icon size="48" color="primary" class="mb-4">mdi-warehouse</v-icon>
            <div class="text-h4 font-weight-bold">{{ totalWarehouses }}</div>
            <div class="text-body-1 text-medium-emphasis">Bodegas</div>
          </v-card>
        </v-col>
        <v-col cols="12" sm="4">
          <v-card class="text-center pa-6" elevation="2">
            <v-icon size="48" color="primary" class="mb-4">mdi-chart-box</v-icon>
            <div class="text-h4 font-weight-bold">{{ totalStocks }}</div>
            <div class="text-body-1 text-medium-emphasis">Registros de Stock</div>
          </v-card>
        </v-col>
      </v-row>

      <v-row>
        <v-col cols="12">
          <h2 class="text-h5 font-weight-bold mb-4">Productos Destacados</h2>
        </v-col>
        <v-col v-for="product in recentProducts" :key="product.id" cols="12" sm="6" md="4">
          <v-card elevation="2" class="h-100">
            <v-card-title>{{ product.name }}</v-card-title>
            <v-card-subtitle>{{ product.categoryName }}</v-card-subtitle>
            <v-card-text>
              <div class="text-h6 font-weight-bold text-primary mb-2">
                ${{ formatPrice(product.price) }}
              </div>
              <div class="text-body-2 text-medium-emphasis">
                SKU: {{ product.sku }} | Stock: {{ product.stock }}
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <v-row class="mt-8">
        <v-col cols="12" class="text-center">
          <v-btn to="/productos" color="primary" size="large" variant="tonal">
            <v-icon start>mdi-arrow-right</v-icon>
            Ver todos los productos
          </v-btn>
        </v-col>
      </v-row>
    </v-container>

    <PublicFooter />
  </v-main>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import PublicHeader from '../../components/PublicHeader.vue'
import PublicFooter from '../../components/PublicFooter.vue'
import { getProducts } from '../../api/products'
import { getWarehouses } from '../../api/warehouses'
import { getStocks } from '../../api/stock'

const totalProducts = ref(0)
const totalWarehouses = ref(0)
const totalStocks = ref(0)
const recentProducts = ref([])

function formatPrice(price) {
  return Number(price).toLocaleString('es-CL')
}

onMounted(async () => {
  try {
    const [prods, whs, stks] = await Promise.all([
      getProducts(),
      getWarehouses(),
      getStocks(),
    ])
    totalProducts.value = prods.data.length
    totalWarehouses.value = whs.data.length
    totalStocks.value = stks.data.length
    recentProducts.value = prods.data.slice(0, 6)
  } catch (e) {
    console.error('Error loading landing data:', e)
  }
})
</script>
