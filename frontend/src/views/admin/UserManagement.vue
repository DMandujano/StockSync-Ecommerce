<template>
  <v-row>
    <v-col cols="12" md="5">
      <v-card elevation="2">
        <v-card-title>Invitar Usuario</v-card-title>
        <v-card-text>
          <v-alert v-if="inviteError" type="error" variant="tonal" class="mb-4" closable @click:close="inviteError = ''">
            {{ inviteError }}
          </v-alert>

          <v-alert v-if="inviteSuccess" type="success" variant="tonal" class="mb-4" closable @click:close="inviteSuccess = ''">
            Usuario invitado exitosamente. Contraseña temporal: <strong>{{ tempPassword }}</strong>
          </v-alert>

          <v-form @submit.prevent="handleInvite">
            <v-text-field
              v-model="form.email"
              label="Email"
              type="email"
              prepend-inner-icon="mdi-email"
              :rules="[rules.required, rules.email]"
              required
            />

            <v-select
              v-model="form.role"
              :items="roles"
              item-title="label"
              item-value="value"
              label="Rol"
              prepend-inner-icon="mdi-shield-account"
              :rules="[rules.required]"
              required
            />

            <v-btn
              type="submit"
              color="primary"
              block
              :loading="inviting"
            >
              Invitar
            </v-btn>
          </v-form>
        </v-card-text>
      </v-card>
    </v-col>

    <v-col cols="12" md="7">
      <v-card elevation="2">
        <v-card-title>Usuarios Invitados</v-card-title>
        <v-card-text>
          <v-progress-linear v-if="loading" indeterminate color="primary" />

          <v-table density="comfortable">
            <thead>
              <tr>
                <th>Email</th>
                <th>Nombre</th>
                <th>Rol</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="u in users" :key="u.id">
                <td>{{ u.email }}</td>
                <td>{{ u.nombre }}</td>
                <td>
                  <v-chip :color="chipColor(u.role)" size="small" variant="tonal">
                    {{ u.role }}
                  </v-chip>
                </td>
              </tr>
              <tr v-if="!users.length && !loading">
                <td colspan="3" class="text-center text-medium-emphasis py-6">
                  No has invitado ningún usuario
                </td>
              </tr>
            </tbody>
          </v-table>
        </v-card-text>
      </v-card>
    </v-col>
  </v-row>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUsersStore } from '../../stores/users'

const store = useUsersStore()
const users = ref([])
const loading = ref(false)
const inviting = ref(false)
const inviteError = ref('')
const inviteSuccess = ref(false)
const tempPassword = ref('')

const form = ref({
  email: '',
  role: null,
})

const roles = [
  { label: 'Local', value: 'LOCAL' },
  { label: 'Bodega', value: 'BODEGA' },
]

const rules = {
  required: (v) => !!v || 'Campo requerido',
  email: (v) => /.+@.+/.test(v) || 'Email inválido',
}

function chipColor(role) {
  const map = { ADMIN: 'warning', LOCAL: 'primary', BODEGA: 'success' }
  return map[role] || 'grey'
}

async function handleInvite() {
  inviting.value = true
  inviteError.value = ''
  inviteSuccess.value = false
  try {
    const result = await store.invite(form.value.email, form.value.role)
    tempPassword.value = result.temporaryPassword
    inviteSuccess.value = true
    form.value = { email: '', role: null }
    users.value = [...store.users]
  } catch (e) {
    inviteError.value = e.response?.data?.message || 'Error al invitar usuario'
  } finally {
    inviting.value = false
  }
}

onMounted(async () => {
  loading.value = true
  try {
    await store.fetchInvited()
    users.value = [...store.users]
  } finally {
    loading.value = false
  }
})
</script>
