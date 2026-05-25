<template>
  <v-container fluid class="fill-height bg-grey-lighten-4">
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="4" lg="3">
        <v-card class="elevation-12 rounded-xl" :loading="loading">
          <v-card-title class="text-center bg-primary text-white py-4 text-h5 font-weight-bold">
            Restablecer Contraseña
          </v-card-title>
          <v-card-text class="pa-6">
            <v-alert v-if="successMessage" type="success" variant="tonal" class="mb-4">
              {{ successMessage }}
            </v-alert>
            <v-alert v-if="error" type="error" variant="tonal" class="mb-4">
              {{ error }}
            </v-alert>

            <v-form @submit.prevent="submit" v-if="!successMessage">
              <v-text-field
                v-model="password"
                label="Nueva Contraseña"
                prepend-inner-icon="mdi-lock"
                variant="outlined"
                type="password"
                required
                :rules="[v => !!v || 'La contraseña es requerida']"
              ></v-text-field>

              <v-text-field
                v-model="passwordConfirm"
                label="Confirmar Nueva Contraseña"
                prepend-inner-icon="mdi-lock-check"
                variant="outlined"
                type="password"
                required
                :rules="[v => !!v || 'La confirmación es requerida', v => v === password || 'Las contraseñas no coinciden']"
              ></v-text-field>

              <v-btn
                type="submit"
                color="primary"
                block
                size="large"
                class="mt-4 rounded-lg"
                :loading="loading"
              >
                Cambiar Contraseña
              </v-btn>
            </v-form>
          </v-card-text>
          <v-divider></v-divider>
          <v-card-actions class="justify-center py-4 bg-grey-lighten-5">
            <v-btn variant="text" color="primary" to="/login">Volver al login</v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { authService } from '../../api/auth'

const route = useRoute()
const token = ref('')
const password = ref('')
const passwordConfirm = ref('')
const loading = ref(false)
const successMessage = ref('')
const error = ref('')

onMounted(() => {
  token.value = route.query.token || ''
  if (!token.value) {
    error.value = 'Token de restablecimiento no encontrado. Solicita un nuevo enlace.'
  }
})

const submit = async () => {
  if (password.value !== passwordConfirm.value || !password.value) return

  loading.value = true
  error.value = ''
  successMessage.value = ''

  try {
    const response = await authService.resetPassword(token.value, password.value)
    successMessage.value = response.data.message
  } catch (e) {
    error.value = e.response?.data?.message || 'Error al restablecer la contraseña. El token puede ser inválido o haber expirado.'
  } finally {
    loading.value = false
  }
}
</script>