<template>
  <div
    v-if="visible"
    data-testid="confirm-dialog"
    class="fixed inset-0 bg-black/50 flex items-center justify-center z-50"
  >
    <div class="bg-white rounded-lg shadow-xl p-6 max-w-sm w-full mx-4">
      <p class="text-gray-800 text-lg mb-6" data-testid="confirm-message">{{ message }}</p>
      <div class="flex justify-end gap-3">
        <button
          @click="$emit('cancel')"
          data-testid="confirm-cancel"
          class="px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300 transition"
        >
          Cancelar
        </button>
        <button
          @click="$emit('confirm')"
          data-testid="confirm-ok"
          class="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700 transition"
        >
          Confirmar
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ConfirmDialog',
  props: {
    visible: { type: Boolean, default: false },
    message: { type: String, default: 'Tem certeza?' },
  },
  emits: ['confirm', 'cancel'],
}
</script>
