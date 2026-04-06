<template>
  <div class="bg-white rounded-lg shadow overflow-hidden">
    <table class="w-full" data-testid="filme-table">
      <thead class="bg-gray-50">
        <tr>
          <th class="px-4 py-3 text-left text-sm font-semibold text-gray-600">ID</th>
          <th class="px-4 py-3 text-left text-sm font-semibold text-gray-600">Título</th>
          <th class="px-4 py-3 text-left text-sm font-semibold text-gray-600">Diretor</th>
          <th class="px-4 py-3 text-left text-sm font-semibold text-gray-600">Ano</th>
          <th class="px-4 py-3 text-left text-sm font-semibold text-gray-600">Gênero</th>
          <th class="px-4 py-3 text-left text-sm font-semibold text-gray-600">Disponível</th>
          <th class="px-4 py-3 text-left text-sm font-semibold text-gray-600">Ações</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="filme in filmes"
          :key="filme.id"
          class="border-t border-gray-200 hover:bg-gray-50"
          :data-testid="'filme-row-' + filme.id"
        >
          <td class="px-4 py-3 text-sm text-gray-700">{{ filme.id }}</td>
          <td class="px-4 py-3 text-sm text-gray-700" data-testid="filme-titulo">{{ filme.titulo }}</td>
          <td class="px-4 py-3 text-sm text-gray-700">{{ filme.diretor }}</td>
          <td class="px-4 py-3 text-sm text-gray-700">{{ filme.ano }}</td>
          <td class="px-4 py-3 text-sm text-gray-700">{{ filme.genero }}</td>
          <td class="px-4 py-3 text-sm">
            <span
              :class="filme.disponivel ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
              class="px-2 py-1 rounded-full text-xs font-medium"
              data-testid="filme-disponivel"
            >
              {{ filme.disponivel ? 'Sim' : 'Não' }}
            </span>
          </td>
          <td class="px-4 py-3 text-sm">
            <div class="flex gap-2">
              <button
                @click="$emit('edit', filme)"
                data-testid="btn-edit"
                class="px-3 py-1 bg-yellow-500 text-white rounded text-xs hover:bg-yellow-600 transition"
              >
                Editar
              </button>
              <button
                @click="$emit('delete', filme)"
                data-testid="btn-delete"
                class="px-3 py-1 bg-red-500 text-white rounded text-xs hover:bg-red-600 transition"
              >
                Excluir
              </button>
            </div>
          </td>
        </tr>
        <tr v-if="filmes.length === 0">
          <td colspan="7" class="px-4 py-8 text-center text-gray-500" data-testid="empty-message">
            Nenhum filme cadastrado.
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  name: 'FilmeTable',
  props: {
    filmes: { type: Array, default: () => [] },
  },
  emits: ['edit', 'delete'],
}
</script>
