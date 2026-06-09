<template>
  <v-container fluid class="pa-6">

    <div class="mb-6">
      <h1 class="text-h4 font-weight-bold">
        Solicitudes de Reposición
      </h1>

      <p class="text-medium-emphasis">
        Revisa y aprueba las solicitudes enviadas por los locales.
      </p>
    </div>

    <v-row>

      <v-col
          v-for="solicitud in solicitudes"
          :key="solicitud.id"
          cols="12"
          md="6"
          lg="4"
      >
        <v-card
            class="solicitud-card"
            elevation="4"
        >

          <v-card-title class="d-flex align-center">

            <v-icon
                color="primary"
                class="mr-2"
            >
              mdi-clipboard-list
            </v-icon>

            Solicitud #{{ solicitud.id }}

            <v-spacer />

            <v-chip
                color="warning"
                variant="tonal"
            >
              Pendiente
            </v-chip>

          </v-card-title>

          <v-divider />

          <v-card-text>

            <div class="info-item">
              <v-icon size="20" class="mr-2">
                mdi-store
              </v-icon>

              <strong>Local:</strong>
              {{ solicitud.local }}
            </div>

            <div class="info-item">
              <v-icon size="20" class="mr-2">
                mdi-package-variant
              </v-icon>

              <strong>Producto:</strong>
              {{ solicitud.producto }}
            </div>

            <div class="info-item">
              <v-icon size="20" class="mr-2">
                mdi-counter
              </v-icon>

              <strong>Cantidad:</strong>
              {{ solicitud.cantidad }}
            </div>

            <div class="info-item">
              <v-icon size="20" class="mr-2">
                mdi-calendar
              </v-icon>

              <strong>Fecha:</strong>
              {{ solicitud.fecha }}
            </div>

          </v-card-text>

          <v-card-actions class="pa-4">

            <v-btn
                color="success"
                prepend-icon="mdi-check"
                @click="aprobar(solicitud)"
            >
              Aprobar
            </v-btn>

            <v-btn
                color="error"
                variant="outlined"
                prepend-icon="mdi-close"
            >
              Rechazar
            </v-btn>

          </v-card-actions>

        </v-card>
      </v-col>

    </v-row>

  </v-container>
</template>

<script setup>

const solicitudes = [
  {
    id: 1,
    local: 'Local Providencia',
    producto: 'Coca Cola',
    cantidad: 50,
    fecha: '09/06/2026'
  },
  {
    id: 2,
    local: 'Local Maipú',
    producto: 'Papas Fritas',
    cantidad: 30,
    fecha: '09/06/2026'
  },
  {
    id: 3,
    local: 'Local Santiago Centro',
    producto: 'Agua Mineral',
    cantidad: 40,
    fecha: '08/06/2026'
  }
]

function aprobar(solicitud) {
  alert(`Solicitud ${solicitud.id} aprobada`)
}

</script>

<style scoped>

.solicitud-card {
  transition: all .25s ease;
  border-radius: 18px;
}

.solicitud-card:hover {
  transform: translateY(-4px);
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

@media (max-width: 600px) {

  .v-card-actions {
    flex-direction: column;
    gap: 10px;
  }

  .v-card-actions .v-btn {
    width: 100%;
  }

}

</style>