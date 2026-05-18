import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as api from '../api/users'

export const useUsersStore = defineStore('users', () => {
  const users = ref([])
  const loading = ref(false)

  async function fetchInvited() {
    loading.value = true
    try {
      const { data } = await api.getInvitedUsers()
      users.value = data
    } finally {
      loading.value = false
    }
  }

  async function invite(email, role) {
    const { data } = await api.inviteUser(email, role)
    await fetchInvited()
    return data
  }

  return { users, loading, fetchInvited, invite }
})
