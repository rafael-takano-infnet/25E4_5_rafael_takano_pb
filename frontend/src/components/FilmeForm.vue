<template>
  <form @submit.prevent="handleSubmit" data-testid="filme-form" class="bg-white p-6 rounded-lg shadow mb-6">
    <h2 class="text-xl font-semibold text-gray-800 mb-4">
      {{ isEditing ? 'Editar Filme' : 'Novo Filme' }}
    </h2>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">Título</label>
        <input
          v-model="form.titulo"
          data-testid="input-titulo"
          type="text"
          maxlength="255"
          class="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          :class="{ 'border-red-500': errors.titulo }"
        />
        <p v-if="errors.titulo" class="text-red-500 text-sm mt-1" data-testid="error-titulo">{{ errors.titulo }}</p>
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">Diretor</label>
        <input
          v-model="form.diretor"
          data-testid="input-diretor"
          type="text"
          maxlength="255"
          class="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          :class="{ 'border-red-500': errors.diretor }"
        />
        <p v-if="errors.diretor" class="text-red-500 text-sm mt-1" data-testid="error-diretor">{{ errors.diretor }}</p>
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">Ano</label>
        <input
          v-model.number="form.ano"
          data-testid="input-ano"
          type="number"
          min="1888"
          class="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          :class="{ 'border-red-500': errors.ano }"
        />
        <p v-if="errors.ano" class="text-red-500 text-sm mt-1" data-testid="error-ano">{{ errors.ano }}</p>
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">Gênero</label>
        <input
          v-model="form.genero"
          data-testid="input-genero"
          type="text"
          maxlength="100"
          class="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          :class="{ 'border-red-500': errors.genero }"
        />
        <p v-if="errors.genero" class="text-red-500 text-sm mt-1" data-testid="error-genero">{{ errors.genero }}</p>
      </div>
    </div>

    <div class="mt-4 flex items-center gap-2">
      <input
        v-model="form.disponivel"
        data-testid="input-disponivel"
        type="checkbox"
        class="w-4 h-4 text-blue-600 rounded"
      />
      <label class="text-sm text-gray-700">Disponível</label>
    </div>

    <div class="mt-6 flex gap-3">
      <button
        type="submit"
        data-testid="btn-submit"
        class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition"
      >
        {{ isEditing ? 'Atualizar' : 'Cadastrar' }}
      </button>
      <button
        v-if="isEditing"
        type="button"
        @click="$emit('cancel')"
        data-testid="btn-cancel"
        class="px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300 transition"
      >
        Cancelar
      </button>
    </div>
  </form>
</template>

<script>
import { createFilmeFormState, validateFilmeForm } from '../utils/filmeForm.js'

export default {
  name: 'FilmeForm',
  props: {
    filme: { type: Object, default: null },
  },
  emits: ['submit', 'cancel'],
  data() {
    return {
      form: createFilmeFormState(this.filme),
      errors: {},
    }
  },
  computed: {
    isEditing() {
      return this.filme !== null && this.filme.id !== undefined
    },
  },
  watch: {
    filme: {
      handler(val) {
        this.form = createFilmeFormState(val)
        this.errors = {}
      },
      immediate: true,
    },
  },
  methods: {
    validate() {
      this.errors = validateFilmeForm(this.form)
      return Object.keys(this.errors).length === 0
    },
    handleSubmit() {
      if (this.validate()) {
        this.$emit('submit', { ...this.form })
      }
    },
  },
}
</script>
