import { requestJson } from './apiClient.js'

const API_URL = '/api/filmes'

export default {
  async findAll() {
    return requestJson(API_URL)
  },

  async findById(id) {
    return requestJson(`${API_URL}/${id}`)
  },

  async create(filme) {
    return requestJson(API_URL, {
      method: 'POST',
      body: JSON.stringify(filme),
    })
  },

  async update(id, filme) {
    return requestJson(`${API_URL}/${id}`, {
      method: 'PUT',
      body: JSON.stringify(filme),
    })
  },

  async remove(id) {
    return requestJson(`${API_URL}/${id}`, {
      method: 'DELETE',
    })
  },
}
