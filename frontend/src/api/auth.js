import client from './client'

export function login(email, password) {
  return client.post('/api/login', { email, password })
}

export function register(nombre, email, password) {
  return client.post('/api/register', { nombre, email, password })
}

export function changePassword(oldPassword, newPassword) {
  return client.post('/api/users/change-password', { oldPassword, newPassword })
}

export function forgotPassword(email) {
  return client.post('/api/forgot-password', { email })
}

export function resetPassword(token, newPassword) {
  return client.post('/api/reset-password', { token, newPassword })
}

export const authService = {
  login,
  register,
  changePassword,
  forgotPassword,
  resetPassword
}