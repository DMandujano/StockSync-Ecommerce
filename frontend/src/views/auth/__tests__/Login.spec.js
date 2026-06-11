import { mount } from '@vue/test-utils'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import { createPinia, setActivePinia } from 'pinia'
import Login from '../Login.vue'
import { useAuthStore } from '../../../stores/auth'

// Mock de ResizeObserver para Vuetify en jsdom
global.ResizeObserver = class ResizeObserver {
  observe() {}
  unobserve() {}
  disconnect() {}
}

// Configuración de Vuetify
const vuetify = createVuetify({ components, directives })

// Mock del Router
const mockPush = vi.fn()
vi.mock('vue-router', async (importOriginal) => {
  const actual = await importOriginal()
  return {
    ...actual,
    useRouter: () => ({
      push: mockPush
    })
  }
})

// Mock de componentes hijos para aislar la prueba del componente padre
vi.mock('../../../components/PublicHeader.vue', () => ({ default: { template: '<div class="mock-header"></div>' } }))
vi.mock('../../../components/PublicFooter.vue', () => ({ default: { template: '<div class="mock-footer"></div>' } }))

describe('Login.vue', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('renderiza el formulario de login correctamente', () => {
    const wrapper = mount({ template: '<v-app><Login/></v-app>', components: { Login } }, {
      global: {
        plugins: [vuetify]
      }
    })
    
    expect(wrapper.text()).toContain('Iniciar Sesión')
    expect(wrapper.find('input[type="email"]').exists()).toBe(true)
    expect(wrapper.find('input[type="password"]').exists()).toBe(true)
    expect(wrapper.find('button[type="submit"]').exists()).toBe(true)
  })

  it('muestra un mensaje de error si las credenciales son incorrectas', async () => {
    const wrapper = mount({ template: '<v-app><Login/></v-app>', components: { Login } }, {
      global: {
        plugins: [vuetify]
      }
    })
    
    const store = useAuthStore()
    store.login = vi.fn().mockRejectedValue({
      response: { data: { message: 'Contraseña incorrecta' } }
    })

    await wrapper.find('input[type="email"]').setValue('admin@test.com')
    await wrapper.find('input[type="password"]').setValue('wrong')
    await wrapper.find('form').trigger('submit.prevent')
    
    // Esperar a que las promesas y el DOM se actualicen
    await new Promise(resolve => setTimeout(resolve, 10))
    await wrapper.vm.$nextTick()

    expect(store.login).toHaveBeenCalledWith('admin@test.com', 'wrong')
    expect(wrapper.text()).toContain('Contraseña incorrecta')
  })

  it('redirecciona dependiendo del rol del usuario al iniciar sesión correctamente', async () => {
    const wrapper = mount({ template: '<v-app><Login/></v-app>', components: { Login } }, {
      global: {
        plugins: [vuetify]
      }
    })
    
    const store = useAuthStore()
    // Simulamos un login exitoso como ADMIN
    store.login = vi.fn().mockResolvedValue({ forcePasswordChange: false })
    Object.defineProperty(store, 'isAdmin', { get: () => true })

    await wrapper.find('input[type="email"]').setValue('admin@test.com')
    await wrapper.find('input[type="password"]').setValue('correct')
    await wrapper.find('form').trigger('submit.prevent')
    
    await new Promise(resolve => setTimeout(resolve, 10))

    expect(mockPush).toHaveBeenCalledWith('/admin')
  })
})
