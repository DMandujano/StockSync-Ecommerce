import { mount } from '@vue/test-utils'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import { createPinia, setActivePinia } from 'pinia'
import LocalVentas from '../localVentas.vue'

// Mock ResizeObserver para Vuetify
global.ResizeObserver = class ResizeObserver {
  observe() {}
  unobserve() {}
  disconnect() {}
}

const vuetify = createVuetify({ components, directives })

vi.mock('vue-router', async (importOriginal) => {
  const actual = await importOriginal()
  return {
    ...actual,
    useRouter: () => ({
      push: vi.fn()
    })
  }
})

// Mock de jsPDF
vi.mock('jspdf', () => ({
  default: class jsPDF {
    text() {}
    save() {}
  }
}))

vi.mock('jspdf-autotable', () => ({
  default: vi.fn()
}))

describe('localVentas.vue', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('renderiza la vista principal del Punto de Venta correctamente', () => {
    const wrapper = mount({ template: '<v-app><LocalVentas/></v-app>', components: { LocalVentas } }, {
      global: {
        plugins: [vuetify]
      }
    })
    
    // Verifica que el título o los elementos base existan
    expect(wrapper.text()).toContain('Ventas del Local')
    expect(wrapper.find('button').exists()).toBe(true)
  })
})
