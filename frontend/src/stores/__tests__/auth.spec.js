import { setActivePinia, createPinia } from 'pinia'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { useAuthStore } from '../auth'
import * as authApi from '../../api/auth'

// Mock de las llamadas a la API
vi.mock('../../api/auth', () => ({
  login: vi.fn(),
  register: vi.fn(),
  changePassword: vi.fn()
}))

describe('Auth Store', () => {
  beforeEach(() => {
    // Configurar Pinia antes de cada test
    setActivePinia(createPinia())
    // Limpiar localStorage
    localStorage.clear()
    // Limpiar mocks
    vi.clearAllMocks()
  })

  it('inicializa con los valores por defecto', () => {
    const store = useAuthStore()
    expect(store.token).toBeNull()
    expect(store.user).toBeNull()
    expect(store.isAuthenticated).toBe(false)
    expect(store.isAdmin).toBe(false)
  })

  it('login actualiza el estado y localStorage', async () => {
    const store = useAuthStore()
    const mockData = {
      token: 'mock-jwt-token',
      email: 'test@test.com',
      nombre: 'Test User',
      role: 'ADMIN',
      forcePasswordChange: false
    }
    
    authApi.login.mockResolvedValue({ data: mockData })

    await store.login('test@test.com', 'password123')

    expect(authApi.login).toHaveBeenCalledWith('test@test.com', 'password123')
    expect(store.token).toBe('mock-jwt-token')
    expect(store.isAuthenticated).toBe(true)
    expect(store.isAdmin).toBe(true)
    
    expect(localStorage.getItem('jwt')).toBe('mock-jwt-token')
    expect(localStorage.getItem('mustChangePassword')).toBe('false')
  })

  it('logout limpia el estado y localStorage', () => {
    const store = useAuthStore()
    // Estado inicial simulado
    store.token = 'some-token'
    store.user = { email: 'test@test.com', role: 'ADMIN' }
    localStorage.setItem('jwt', 'some-token')
    localStorage.setItem('user', JSON.stringify(store.user))
    
    store.logout()

    expect(store.token).toBeNull()
    expect(store.user).toBeNull()
    expect(store.isAuthenticated).toBe(false)
    expect(localStorage.getItem('jwt')).toBeNull()
    expect(localStorage.getItem('user')).toBeNull()
  })

  it('setAdminViewWarehouseId actualiza el localStorage', () => {
    const store = useAuthStore()
    
    store.setAdminViewWarehouseId(5)
    expect(store.adminViewWarehouseId).toBe(5)
    expect(localStorage.getItem('adminViewWarehouseId')).toBe('5')

    store.setAdminViewWarehouseId(null)
    expect(store.adminViewWarehouseId).toBeNull()
    expect(localStorage.getItem('adminViewWarehouseId')).toBeNull()
  })
})
