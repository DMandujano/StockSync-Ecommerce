import client from './client'

export function getInvitedUsers() {
  return client.get('/api/users/invited')
}

export function inviteUser(email, role) {
  return client.post('/api/users/invite', { email, role })
}
