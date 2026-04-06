<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-blue-700 text-white py-4 shadow-md">
      <div class="max-w-5xl mx-auto px-4">
        <h1 class="text-2xl font-bold" data-testid="app-title">Locadora de Filmes</h1>
      </div>
    </header>

    <main class="max-w-5xl mx-auto px-4 py-6">
      <AlertMessage
        :message="alert.message"
        :type="alert.type"
        :visible="alert.visible"
        @close="alert.visible = false"
      />

      <FilmeForm
        :filme="selectedFilme"
        @submit="handleSubmit"
        @cancel="cancelEdit"
      />

      <FilmeTable
        :filmes="filmes"
        @edit="startEdit"
        @delete="confirmDelete"
      />

      <ConfirmDialog
        :visible="confirmDialog.visible"
        :message="confirmDialog.message"
        @confirm="handleDelete"
        @cancel="confirmDialog.visible = false"
      />
    </main>
  </div>
</template>

<script>
import { reactive } from 'vue'
import filmeService from './services/filmeService.js'
import AlertMessage from './components/AlertMessage.vue'
import ConfirmDialog from './components/ConfirmDialog.vue'
import FilmeForm from './components/FilmeForm.vue'
import FilmeTable from './components/FilmeTable.vue'

function createAlertState() {
  return { message: '', type: 'info', visible: false }
}

function createConfirmDialogState() {
  return { visible: false, message: '', filmeId: null }
}

export default {
  name: 'App',
  components: { AlertMessage, ConfirmDialog, FilmeForm, FilmeTable },
  data() {
    return {
      filmes: [],
      selectedFilme: null,
      alert: reactive(createAlertState()),
      confirmDialog: reactive(createConfirmDialogState()),
    }
  },
  async mounted() {
    await this.refreshFilmes()
  },
  methods: {
    async refreshFilmes() {
      try {
        this.filmes = await filmeService.findAll()
      } catch (error) {
        this.showError(error)
      }
    },
    async handleSubmit(form) {
      try {
        if (this.selectedFilme?.id) {
          await filmeService.update(this.selectedFilme.id, form)
          this.showSuccess('Filme atualizado com sucesso!')
        } else {
          await filmeService.create(form)
          this.showSuccess('Filme cadastrado com sucesso!')
        }
        this.selectedFilme = null
        await this.refreshFilmes()
      } catch (error) {
        this.showError(error)
      }
    },
    startEdit(filme) {
      this.selectedFilme = { ...filme }
    },
    cancelEdit() {
      this.selectedFilme = null
    },
    confirmDelete(filme) {
      Object.assign(this.confirmDialog, {
        visible: true,
        message: `Deseja excluir o filme "${filme.titulo}"?`,
        filmeId: filme.id,
      })
    },
    async handleDelete() {
      try {
        await filmeService.remove(this.confirmDialog.filmeId)
        this.resetConfirmDialog()
        this.showSuccess('Filme excluído com sucesso!')
        await this.refreshFilmes()
      } catch (error) {
        this.resetConfirmDialog()
        this.showError(error)
      }
    },
    resetConfirmDialog() {
      Object.assign(this.confirmDialog, createConfirmDialogState())
    },
    showSuccess(message) {
      Object.assign(this.alert, { message, type: 'success', visible: true })
    },
    showError(error) {
      Object.assign(this.alert, {
        message: error?.message || 'Erro de conexão com o servidor',
        type: 'error',
        visible: true,
      })
    },
  },
}
</script>
