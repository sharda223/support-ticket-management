import { describe, it } from 'node:test'
import assert from 'node:assert/strict'
import { ALLOWED_TRANSITIONS, getNextStatuses } from './constants.js'
import { formatApiError } from './utils/errors.js'

describe('status transitions UI helper', () => {
  it('exposes only valid next statuses', () => {
    assert.deepEqual(getNextStatuses('OPEN'), ['IN_PROGRESS', 'CANCELLED'])
    assert.deepEqual(getNextStatuses('IN_PROGRESS'), ['RESOLVED', 'CANCELLED'])
    assert.deepEqual(getNextStatuses('RESOLVED'), ['CLOSED'])
    assert.deepEqual(getNextStatuses('CLOSED'), [])
    assert.deepEqual(getNextStatuses('CANCELLED'), [])
  })

  it('matches the approved transition map', () => {
    assert.deepEqual(
      Object.keys(ALLOWED_TRANSITIONS).sort(),
      ['CANCELLED', 'CLOSED', 'IN_PROGRESS', 'OPEN', 'RESOLVED'].sort(),
    )
  })
})

describe('formatApiError', () => {
  it('includes field errors when present', () => {
    const message = formatApiError({
      message: 'Validation failed',
      errors: [{ field: 'title', message: 'must not be blank' }],
    })
    assert.match(message, /Validation failed/)
    assert.match(message, /title: must not be blank/)
  })

  it('falls back to message only', () => {
    assert.equal(formatApiError({ message: 'Ticket not found: 1' }), 'Ticket not found: 1')
  })
})
