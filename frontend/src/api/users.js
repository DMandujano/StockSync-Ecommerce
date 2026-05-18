import client from './client'

export function getInvitedUsers() {
  return client.get('/users/invited')
}

export function inviteUser(email, role) {
  return client.post('/users/invite', { email, role })
}
