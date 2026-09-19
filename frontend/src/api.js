const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080').replace(/\/$/, '')

export class ApiError extends Error {
  constructor(message, { status, errors } = {}) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.errors = errors || []
  }
}

async function parseError(response) {
  let message = `Request failed (${response.status})`
  let errors = []
  try {
    const data = await response.json()
    if (data?.message) message = data.message
    if (Array.isArray(data?.errors)) errors = data.errors
  } catch {
    // non-JSON body
  }
  return new ApiError(message, { status: response.status, errors })
}

async function request(path, options = {}) {
  let response
  try {
    response = await fetch(`${API_BASE_URL}${path}`, {
      headers: {
        'Content-Type': 'application/json',
        ...(options.headers || {}),
      },
      ...options,
    })
  } catch {
    throw new ApiError('Could not reach the server. Check that the API is running and VITE_API_BASE_URL is correct.')
  }

  if (!response.ok) {
    throw await parseError(response)
  }

  if (response.status === 204) {
    return null
  }

  return response.json()
}

export function listTickets({ q, status } = {}) {
  const params = new URLSearchParams()
  if (q) params.set('q', q)
  if (status) params.set('status', status)
  const query = params.toString()
  return request(`/api/tickets${query ? `?${query}` : ''}`)
}

export function getTicket(id) {
  return request(`/api/tickets/${id}`)
}

export function createTicket(payload) {
  return request('/api/tickets', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function updateTicket(id, payload) {
  return request(`/api/tickets/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export function updateTicketStatus(id, status) {
  return request(`/api/tickets/${id}/status`, {
    method: 'PATCH',
    body: JSON.stringify({ status }),
  })
}

export function addComment(id, body) {
  return request(`/api/tickets/${id}/comments`, {
    method: 'POST',
    body: JSON.stringify({ body }),
  })
}
