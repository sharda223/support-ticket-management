import { useCallback, useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { listTickets } from '../api'
import { STATUSES, formatDate } from '../constants'
import ErrorBanner from '../components/ErrorBanner'
import LoadingState from '../components/LoadingState'
import EmptyState from '../components/EmptyState'
import StatusBadge from '../components/StatusBadge'

export default function TicketListPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const q = searchParams.get('q') || ''
  const status = searchParams.get('status') || ''

  const [tickets, setTickets] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [draftQ, setDraftQ] = useState(q)

  const load = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await listTickets({
        q: q || undefined,
        status: status || undefined,
      })
      setTickets(data)
    } catch (err) {
      setError(err)
      setTickets([])
    } finally {
      setLoading(false)
    }
  }, [q, status])

  useEffect(() => {
    load()
  }, [load])

  useEffect(() => {
    setDraftQ(q)
  }, [q])

  function applySearch(event) {
    event.preventDefault()
    const next = new URLSearchParams(searchParams)
    if (draftQ.trim()) next.set('q', draftQ.trim())
    else next.delete('q')
    setSearchParams(next)
  }

  function onStatusChange(value) {
    const next = new URLSearchParams(searchParams)
    if (value) next.set('status', value)
    else next.delete('status')
    setSearchParams(next)
  }

  return (
    <section className="page">
      <header className="page-header">
        <div>
          <h1>Support tickets</h1>
          <p className="muted">Search, filter, and manage tickets</p>
        </div>
        <Link className="button primary" to="/tickets/new">
          New ticket
        </Link>
      </header>

      <form className="toolbar" onSubmit={applySearch}>
        <input
          type="search"
          placeholder="Search title or description"
          value={draftQ}
          onChange={(e) => setDraftQ(e.target.value)}
          aria-label="Search tickets"
        />
        <select
          value={status}
          onChange={(e) => onStatusChange(e.target.value)}
          aria-label="Filter by status"
        >
          <option value="">All statuses</option>
          {STATUSES.map((s) => (
            <option key={s} value={s}>
              {s}
            </option>
          ))}
        </select>
        <button type="submit" className="button">
          Search
        </button>
      </form>

      <ErrorBanner error={error} onDismiss={() => setError(null)} />

      {loading && <LoadingState label="Loading tickets…" />}

      {!loading && !error && tickets.length === 0 && (
        <EmptyState
          title="No tickets found"
          detail="Try a different search, clear the status filter, or create a new ticket."
        />
      )}

      {!loading && tickets.length > 0 && (
        <div className="table-wrap">
          <table className="ticket-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Title</th>
                <th>Status</th>
                <th>Priority</th>
                <th>Assignee</th>
                <th>Updated</th>
              </tr>
            </thead>
            <tbody>
              {tickets.map((ticket) => (
                <tr key={ticket.id}>
                  <td>{ticket.id}</td>
                  <td>
                    <Link to={`/tickets/${ticket.id}`}>{ticket.title}</Link>
                  </td>
                  <td>
                    <StatusBadge status={ticket.status} />
                  </td>
                  <td>{ticket.priority}</td>
                  <td>{ticket.assignee || '—'}</td>
                  <td>{formatDate(ticket.updatedAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  )
}
