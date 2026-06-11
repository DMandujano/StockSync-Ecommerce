<template>
  <div>
    <v-progress-linear v-if="loading" indeterminate color="primary" />

    <div v-if="!xs">
      <div class="overflow-x-auto">
        <v-table class="text-no-wrap" density="comfortable">
          <thead>
            <slot name="headers" />
          </thead>
          <tbody>
            <slot name="body" />
            <tr v-if="!loading && empty">
              <td :colspan="colspan" class="text-center text-medium-emphasis py-6">
                {{ emptyText }}
              </td>
            </tr>
          </tbody>
        </v-table>
      </div>
    </div>

    <div v-else class="pa-1">
      <slot name="cards" />
      <div v-if="!loading && empty" class="text-center text-medium-emphasis py-6">
        {{ emptyText }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { useDisplay } from 'vuetify'

defineProps({
  loading: { type: Boolean, default: false },
  empty: { type: Boolean, default: false },
  emptyText: { type: String, default: 'No hay datos registrados' },
  colspan: { type: Number, default: 1 },
})

const { xs } = useDisplay()
</script>
