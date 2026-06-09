<template>
  <v-container fluid class="pa-6">

    <div class="mb-6">
      <h1 class="text-h4 font-weight-bold">
        Recepciones y Observaciones
      </h1>

      <p class="text-medium-emphasis">
        Revisa las confirmaciones realizadas por los locales y las observaciones reportadas.
      </p>
    </div>

    <!-- Resumen -->
    <v-row class="mb-6">

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

          <div class="text-h4 font-weight-bold">
            12
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

          <div class="text-h4 font-weight-bold">
            3
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

          <div class="text-h4 font-weight-bold">
            15
          </div>
        </v-card>
      </v-col>

    </v-row>

    <!-- Tabla -->
    <v-card elevation="4">

      <v-card-title>
        Historial de Recepciones
      </v-card-title>

      <v-divider />

      <div class="table-wrapper">

        <v-table>

          <thead>
          <tr>
            <th>Despacho</th>
            <th>Local</th>
            <th>Fecha</th>
            <th>Estado</th>
            <th>Observación</th>
          </tr>
          </thead>

          <tbody>

          <tr
              v-for="item in recepciones"
              :key="item.id"
          >
            <td>{{ item.id }}</td>

            <td>{{ item.local }}</td>

            <td>{{ item.fecha }}</td>

            <td>
              <v-chip
                  :color="item.observacion ? 'warning' : 'success'"
                  variant="tonal"
              >
                {{
                  item.observacion
                      ? 'Observado'
                      : 'Recibido'
                }}
              </v-chip>
            </td>

            <td>

              <span v-if="item.observacion">
                {{ item.observacion }}
              </span>

              <span
                  v-else
                  class="text-success"
              >
                Sin observaciones
              </span>

            </td>

          </tr>

          </tbody>

        </v-table>

      </div>

    </v-card>

  </v-container>
</template>

<script setup>

const recepciones = [
  {
    id: 'DESP-001',
    local: 'Providencia',
    fecha: '10/06/2026',
    observacion: null
  },
  {
    id: 'DESP-002',
    local: 'Maipú',
    fecha: '10/06/2026',
    observacion: 'Faltaron 5 Coca Cola'
  },
  {
    id: 'DESP-003',
    local: 'Santiago Centro',
    fecha: '09/06/2026',
    observacion: null
  },
  {
    id: 'DESP-004',
    local: 'La Florida',
    fecha: '09/06/2026',
    observacion: 'Producto dañado'
  }
]

</script>

<style scoped>

.table-wrapper {
  overflow-x: auto;
}

.v-card {
  border-radius: 16px;
}

</style>