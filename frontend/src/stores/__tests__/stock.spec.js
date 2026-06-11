import { setActivePinia, createPinia } from 'pinia'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { useStockStore } from '../stock'
import * as api from '../../api/stock'

// Mocks para la API real
vi.mock('../../api/stock', () => ({
  getStocks: vi.fn(),
  getStocksByWarehouse: vi.fn(),
  addStock: vi.fn(),
  updateStock: vi.fn(),
  transferStock: vi.fn(),
  deleteStock: vi.fn(),
  processSale: vi.fn(),
  getMovements: vi.fn()
}))

describe('Stock Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('fetchAll actualiza el estado de stocks y loading', async () => {
    const store = useStockStore()
    const mockStocks = [{ id: 1, quantity: 10, product: { name: 'Prod A' } }]
    
    api.getStocks.mockResolvedValue({ data: mockStocks })

    expect(store.loading).toBe(false)
    const fetchPromise = store.fetchAll()
    expect(store.loading).toBe(true) // Verifica el estado intermedio

    await fetchPromise

    expect(api.getStocks).toHaveBeenCalled()
    expect(store.stocks).toEqual(mockStocks)
    expect(store.loading).toBe(false)
  })

  it('add() llama a la API y retorna el nuevo stock', async () => {
    const store = useStockStore()
    const newStock = { id: 2, quantity: 5 }
    api.addStock.mockResolvedValue({ data: newStock })

    const result = await store.add({ productId: 1, warehouseId: 1, quantity: 5 })

    expect(api.addStock).toHaveBeenCalledWith({ productId: 1, warehouseId: 1, quantity: 5 })
    expect(result).toEqual(newStock)
  })

  it('remove() elimina el elemento del estado local tras llamar a la API', async () => {
    const store = useStockStore()
    store.stocks = [{ id: 1 }, { id: 2 }, { id: 3 }]
    
    api.deleteStock.mockResolvedValue({})

    await store.remove(2)

    expect(api.deleteStock).toHaveBeenCalledWith(2)
    expect(store.stocks).toHaveLength(2)
    expect(store.stocks.find(s => s.id === 2)).toBeUndefined()
  })

  it('fetchMovements actualiza el estado de movements', async () => {
    const store = useStockStore()
    const mockMovements = [{ id: 1, type: 'INGRESO' }]
    
    api.getMovements.mockResolvedValue({ data: mockMovements })

    await store.fetchMovements('INGRESO', 1)

    expect(api.getMovements).toHaveBeenCalledWith('INGRESO', 1)
    expect(store.movements).toEqual(mockMovements)
  })
})
