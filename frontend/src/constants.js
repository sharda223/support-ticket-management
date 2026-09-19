export const STATUSES = ['OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED', 'CANCELLED']

export const PRIORITIES = ['LOW', 'MEDIUM', 'HIGH']

/** Valid next statuses per current status (matches backend state machine). */
export const ALLOWED_TRANSITIONS = {
  OPEN: ['IN_PROGRESS', 'CANCELLED'],
  IN_PROGRESS: ['RESOLVED', 'CANCELLED'],
  RESOLVED: ['CLOSED'],
  CLOSED: [],
  CANCELLED: [],
}

export function getNextStatuses(currentStatus) {
  return ALLOWED_TRANSITIONS[currentStatus] ?? []
}

export function formatDate(iso) {
  if (!iso) return '—'
  try {
    return new Date(iso).toLocaleString()
  } catch {
    return iso
  }
}
