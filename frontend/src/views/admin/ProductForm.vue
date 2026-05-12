<template>
  <v-card elevation="2">
    <v-card-title>{{ isEdit ? 'Editar Producto' : 'Nuevo Producto' }}</v-card-title>

    <v-card-text>
      <v-alert v-if="error" type="error" variant="tonal" class="mb-4" closable @click:close="error = ''">
        {{ error }}
      </v-alert>

      <v-form @submit.prevent="handleSave">
        <v-row>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="form.name"
              label="Nombre"
              :rules="[rules.required]"
              required
            />
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="form.sku"
              label="SKU"
              :rules="[rules.required]"
              required
            />
          </v-col>
          <v-col cols="12">
            <v-textarea
              v-model="form.description"
              label="Descripción"
              rows="3"
            />
          </v-col>
          <v-col cols="12" md="4">
            <v-text-field
              v-model="form.price"
              label="Precio"
              type="number"
              step="0.01"
              :rules="[rules.required, rules.positive]"
              required
              prefix="$"
            />
          </v-col>
          <v-col cols="12" md="4">
            <v-text-field
              v-model="form.stock"
              label="Stock"
              type="number"
              :rules="[rules.required, rules.minZero]"
              required
            />
          </v-col>
          <v-col cols="12" md="4">
            <v-select
              v-model="form.categoryId"
              :items="categories"
              item-title="name"
              item-value="id"
              label="Categoría"
              :rules="[rules.required]"
              required
            />
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="form.imageUrl"
              label="URL de Imagen"
            />
          </v-col>
          <v-col cols="12" md="6" class="d-flex align-center">
            <v-switch
              v-model="form.active"
              label="Producto activo"
              color="primary"
              inset
            />
          </v-col>
        </v-row>

        <v-row class="mt-4">
          <v-col cols="12" class="d-flex ga-4">
            <v-btn
              type="submit"
              color="primary"
              :loading="saving"
            >
              {{ isEdit ? 'Actualizar' : 'Crear' }}
            </v-btn>
            <v-btn variant="outlined" to="/admin/productos">
              Cancelar
            </v-btn>
          </v-col>
        </v-row>
      </v-form>
    </v-card-text>
  </v-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useProductsStore } from '../../stores/products'
import { useCategoriesStore } from '../../stores/categories'

const route = useRoute()
const router = useRouter()
const store = useProductsStore()
const categoriesStore = useCategoriesStore()

const isEdit = computed(() => !!route.params.id)
const categories = computed(() => categoriesStore.categories)

const saving = ref(false)
const error = ref('')

const form = ref({
  name: '',
  description: '',
  price: 0,
  stock: 0,
  sku: '',
  imageUrl: '',
  active: true,
  categoryId: null,
})

const rules = {
  required: (v) => !!v || 'Campo requerido',
  positive: (v) => !v || Number(v) >= 0 || 'Debe ser mayor o igual a 0',
  minZero: (v) => !v || Number(v) >= 0 || 'Debe ser mayor o igual a 0',
}

async function handleSave() {
  saving.value = true
  error.value = ''
  try {
    const payload = {
      name: form.value.name,
      description: form.value.description,
      price: Number(form.value.price),
      stock: Number(form.value.stock),
      sku: form.value.sku,
      imageUrl: form.value.imageUrl,
      active: form.value.active,
      categoryId: Number(form.value.categoryId),
    }
    await store.save(payload, isEdit.value ? Number(route.params.id) : null)
    router.push('/admin/productos')
  } catch (e) {
    const err = e.response?.data
    if (err?.errors) {
      error.value = Object.values(err.errors).flat().join(', ')
    } else {
      error.value = err?.message || 'Error al guardar producto'
    }
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  await categoriesStore.fetchAll()
  if (isEdit.value) {
    try {
      const data = await store.fetchOne(Number(route.params.id))
      form.value = {
        name: data.name,
        description: data.description || '',
        price: data.price,
        stock: data.stock,
        sku: data.sku,
        imageUrl: data.imageUrl || '',
        active: data.active,
        categoryId: data.categoryId || null,
      }
    } catch (e) {
      console.error('Error loading product:', e)
    }
  }
})
</script>
