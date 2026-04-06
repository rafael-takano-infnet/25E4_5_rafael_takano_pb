<template>
  <div
    v-if="visible"
    data-testid="alert-message"
    :class="alertClasses"
    class="p-4 mb-4 rounded-lg flex justify-between items-center"
    role="alert"
  >
    <span data-testid="alert-text">{{ message }}</span>
    <button
      @click="$emit('close')"
      class="ml-4 font-bold text-lg leading-none hover:opacity-70"
      data-testid="alert-close"
    >
      &times;
    </button>
  </div>
</template>

<script>
export default {
  name: 'AlertMessage',
  props: {
    message: { type: String, default: '' },
    type: { type: String, default: 'info' },
    visible: { type: Boolean, default: false },
  },
  emits: ['close'],
  computed: {
    alertClasses() {
      const map = {
        success: 'bg-green-100 text-green-800 border border-green-300',
        error: 'bg-red-100 text-red-800 border border-red-300',
        info: 'bg-blue-100 text-blue-800 border border-blue-300',
      }
      return map[this.type] || map.info
    },
  },
  watch: {
    visible(val) {
      if (val && this.type === 'success') {
        setTimeout(() => this.$emit('close'), 3000)
      }
    },
  },
}
</script>
