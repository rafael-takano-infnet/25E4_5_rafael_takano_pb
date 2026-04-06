const DEFAULT_TIMEOUT_MS = 5000

function createTimeoutSignal(timeoutMs) {
  const controller = new AbortController()
  const timeoutId = setTimeout(() => controller.abort(), timeoutMs)
  return { controller, timeoutId }
}

async function parseErrorResponse(response) {
  const body = await response.json().catch(() => null)
  const validationMessage = Array.isArray(body?.errors) && body.errors.length > 0
    ? body.errors.join(', ')
    : null

  return {
    status: response.status,
    message: body?.message || validationMessage || `Erro ${response.status}`,
    errors: Array.isArray(body?.errors) ? body.errors : [],
  }
}

export async function requestJson(url, options = {}) {
  const { timeoutMs = DEFAULT_TIMEOUT_MS, headers, ...requestOptions } = options
  const { controller, timeoutId } = createTimeoutSignal(timeoutMs)

  try {
    const response = await fetch(url, {
      ...requestOptions,
      signal: controller.signal,
      headers: {
        'Content-Type': 'application/json',
        ...headers,
      },
    })

    if (!response.ok) {
      throw await parseErrorResponse(response)
    }

    if (response.status === 204) {
      return null
    }

    return await response.json()
  } catch (error) {
    if (error.name === 'AbortError') {
      throw { status: 0, message: 'Tempo limite da requisição excedido', errors: [] }
    }

    if (typeof error.status === 'number') {
      throw error
    }

    throw { status: 0, message: 'Erro de conexão com o servidor', errors: [] }
  } finally {
    clearTimeout(timeoutId)
  }
}
