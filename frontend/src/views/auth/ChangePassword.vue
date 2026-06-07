<template>
  <v-main>
    <v-container class="py-12" max-width="480">
      <v-card class="pa-6" elevation="4">
        <v-card-title class="text-h4 font-weight-bold text-center mb-2">
          Cambiar Contraseña
        </v-card-title>

        <v-card-subtitle class="text-center mb-4">
          Por seguridad, debes cambiar tu contraseña temporal.
        </v-card-subtitle>

        <v-card-text>
          <v-alert v-if="error" type="error" variant="tonal" class="mb-4" closable @click:close="error = ''">
            {{ error }}
          </v-alert>

          <v-alert v-if="success" type="success" variant="tonal" class="mb-4">
            Contraseña cambiada exitosamente. Redirigiendo...
          </v-alert>

          <v-form @submit.prevent="handleChangePassword">
            <v-text-field
              v-model="oldPassword"
              label="Contraseña actual"
              type="password"
              prepend-inner-icon="mdi-lock"
              :rules="[rules.required]"
              required
            />

            <v-text-field
              v-model="newPassword"
              label="Nueva contraseña"
              type="password"
              prepend-inner-icon="mdi-lock-plus"
              :rules="[rules.required, rules.min6]"
              required
            />

            <v-text-field
              v-model="confirmPassword"
              label="Confirmar nueva contraseña"
              type="password"
              prepend-inner-icon="mdi-lock-check"
              :rules="[rules.required, rules.match]"
              required
            />

            <v-btn
              type="submit"
              color="primary"
              block
              size="large"
              class="mt-4"
              :loading="loading"
            >
              Cambiar Contraseña
            </v-btn>
          </v-form>
        </v-card-text>
      </v-card>
    </v-container>
  </v-main>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const oldPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const error = ref('')
const success = ref(false)
const loading = ref(false)

const rules = {
  required: (v) => !!v || 'Campo requerido',
  min6: (v) => !v || v.length >= 6 || 'Mínimo 6 caracteres',
  match: (v) => v === newPassword.value || 'Las contraseñas no coinciden',
}

async function handleChangePassword() {
  if (newPassword.value !== confirmPassword.value) {
    error.value = 'Las contraseñas no coinciden'
    return
  }
  loading.value = true
  error.value = ''
  try {
    await auth.changePassword(oldPassword.value, newPassword.value)
    success.value = true
    setTimeout(() => router.push('/admin'), 1500)
  } catch (e) {
    const msg = e.response?.data?.message || e.response?.data?.error || 'Error al cambiar la contraseña'
    error.value = msg
  } finally {
    loading.value = false
  }
}
</script>
