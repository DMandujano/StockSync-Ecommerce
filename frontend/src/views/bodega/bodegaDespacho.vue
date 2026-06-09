<template>
  <v-container fluid class="pa-6">

    <!-- Encabezado -->
    <div class="mb-6">
      <h1 class="text-h4 font-weight-bold">
        Gestión de Despachos
      </h1>

      <p class="text-medium-emphasis">
        Administra los pedidos aprobados y realiza seguimiento de los envíos.
      </p>
    </div>

    <!-- Resumen -->
    <v-row class="mb-6">

      <v-col cols="12" sm="6" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="50" color="warning">
            mdi-package-variant-closed
          </v-icon>

          <div class="text-subtitle-1 mt-2">
            Preparando
          </div>

          <div class="text-h4 font-weight-bold">
            3
          </div>
        </v-card>
      </v-col>

      <v-col cols="12" sm="6" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="50" color="info">
            mdi-truck-delivery
          </v-icon>

          <div class="text-subtitle-1 mt-2">
            En Ruta
          </div>

          <div class="text-h4 font-weight-bold">
            5
          </div>
        </v-card>
      </v-col>

      <v-col cols="12" sm="6" md="3">
        <v-card class="text-center pa-4">
          <v-icon size="50" color="success">
            mdi-check-circle
          </v-icon>

          <div class="text-subtitle-1 mt-2">
            Entregados Hoy
          </div>

          <div class="text-h4 font-weight-bold">
            12
          </div>
        </v-card>
      </v-col>

    </v-row>

    <!-- Despachos -->
    <v-row>

      <v-col
          v-for="despacho in despachos"
          :key="despacho.id"
          cols="12"
          lg="6"
      >

        <v-card
            class="despacho-card"
            elevation="4"
        >

          <v-card-title class="d-flex align-center">

            <v-icon
                color="primary"
                class="mr-2"
            >
              mdi-truck-delivery
            </v-icon>

            Despacho #{{ despacho.id }}

            <v-spacer />

            <v-chip
                :color="estadoColor(despacho.estado)"
                variant="tonal"
            >
              {{ despacho.estado }}
            </v-chip>

          </v-card-title>

          <v-divider />

          <v-card-text>

            <div class="mb-2">
              <strong>Local:</strong>
              {{ despacho.local }}
            </div>

            <div class="mb-2">
              <strong>Fecha:</strong>
              {{ despacho.fecha }}
            </div>

            <div class="mb-4">
              <strong>Productos:</strong>
            </div>

            <v-list density="compact">

              <v-list-item
                  v-for="producto in despacho.productos"
                  :key="producto.nombre"
              >
                <template #prepend>
                  <v-icon>
                    mdi-package-variant
                  </v-icon>
                </template>

                <v-list-item-title>
                  {{ producto.nombre }}
                </v-list-item-title>

                <v-list-item-subtitle>
                  Cantidad: {{ producto.cantidad }}
                </v-list-item-subtitle>

              </v-list-item>

            </v-list>

          </v-card-text>

          <v-divider />

          <v-card-actions>

            <v-btn
                color="primary"
                prepend-icon="mdi-package-variant-closed"
            >
              Preparar
            </v-btn>

            <v-btn
                color="info"
                prepend-icon="mdi-truck-fast"
            >
              Despachar
            </v-btn>

          </v-card-actions>

        </v-card>

      </v-col>

    </v-row>

  </v-container>
</template>

<script setup>

const despachos = [
  {
    id: 'DESP-001',
    local: 'Local Providencia',
    fecha: '09/06/2026',
    estado: 'Preparando',
    productos: [
      { nombre: 'Coca Cola', cantidad: 50 },
      { nombre: 'Agua Mineral', cantidad: 30 }
    ]
  },
  {
    id: 'DESP-002',
    local: 'Local Maipú',
    fecha: '09/06/2026',
    estado: 'En Ruta',
    productos: [
      { nombre: 'Papas Fritas', cantidad: 40 }
    ]
  }
]

function estadoColor(estado) {

  switch (estado) {

    case 'Preparando':
      return 'warning'

    case 'En Ruta':
      return 'info'

    case 'Entregado':
      return 'success'

    default:
      return 'grey'
  }

}
</script>

<style scoped>

.despacho-card {
  transition: all .25s ease;
  border-radius: 18px;
}

.despacho-card:hover {
  transform: translateY(-4px);
}

@media (max-width: 960px) {

  .v-card-actions {
    flex-direction: column;
    gap: 10px;
  }

  .v-card-actions .v-btn {
    width: 100%;
  }

}

</style>