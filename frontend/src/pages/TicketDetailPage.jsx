import { useCallback, useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { addComment, getTicket, updateTicket, updateTicketStatus } from '../api'
import { PRIORITIES, formatDate, getNextStatuses } from '../constants'
import ErrorBanner from '../components/ErrorBanner'
import LoadingState from '../components/LoadingState'
import EmptyState from '../components/EmptyState'
import StatusBadge from '../components/StatusBadge'

export default function TicketDetailPage() {
  const { id } = useParams()
  const [ticket, setTicket] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [actionError, setActionError] = useState(null)
  const [actionSuccess, setActionSuccess] = useState(null)

  const [editForm, setEditForm] = useState(null)
  const [saving, setSaving] = useState(false)
  const [statusValue, setStatusValue] = useState('')
  const [statusSaving, setStatusSaving] = useState(false)
  const [commentBody, setCommentBody] = useState('')
  const [commentSaving, setCommentSaving] = useState(false)

  const load = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await getTicket(id)
      setTicket(data)
      setEditForm({
        title: data.title,
        description: data.description,
        priority: data.priority,
        assignee: data.assignee || '',
      })
      const next = getNextStatuses(data.status)
      setStatusValue(next[0] || '')
    } catch (err) {
      setTicket(null)
      setError(err)
    } finally {
      setLoading(false)
    }
  }, [id])

  useEffect(() => {
    load()
  }, [load])

  async function onSaveFields(event) {
    event.preventDefault()
    setSaving(true)
    setActionError(null)
    setActionSuccess(null)
    try {
      const updated = await updateTicket(id, {
        title: editForm.title.trim(),
        description: editForm.description.trim(),
        priority: editForm.priority,
        assignee: editForm.assignee.trim() || null,
      })
      setTicket(updated)
      setEditForm({
        title: updated.title,
        description: updated.description,
        priority: updated.priority,
        assignee: updated.assignee || '',
      })
      setActionSuccess('Ticket updated.')
    } catch (err) {
      setActionError(err)
    } finally {
      setSaving(false)
    }
  }

  async function onChangeStatus(event) {
    event.preventDefault()
    if (!statusValue) return
    setStatusSaving(true)
    setActionError(null)
    setActionSuccess(null)
    try {
      const updated = await updateTicketStatus(id, statusValue)
      setTicket(updated)
      const next = getNextStatuses(updated.status)
      setStatusValue(next[0] || '')
      setActionSuccess(`Status changed to ${updated.status}.`)
    } catch (err) {
      setActionError(err)
    } finally {
      setStatusSaving(false)
    }
  }

  async function onAddComment(event) {
    event.preventDefault()
    setCommentSaving(true)
    setActionError(null)
    setActionSuccess(null)
    try {
      await addComment(id, commentBody.trim())
      setCommentBody('')
      await load()
      setActionSuccess('Comment added.')
    } catch (err) {
      setActionError(err)
    } finally {
      setCommentSaving(false)
    }
  }

  if (loading) {
    return (
      <section className="page">
        <LoadingState label="Loading ticket…" />
      </section>
    )
  }

  if (error || !ticket) {
    return (
      <section className="page">
        <p className="crumb">
          <Link to="/">Tickets</Link>
        </p>
        <ErrorBanner error={error || 'Ticket not found'} />
        <EmptyState
          title="Ticket not available"
          detail="The ticket may have been removed or the ID is incorrect."
        />
      </section>
    )
  }

  const nextStatuses = getNextStatuses(ticket.status)
  const isTerminal = nextStatuses.length === 0

  return (
    <section className="page">
      <header className="page-header">
        <div>
          <p className="crumb">
            <Link to="/">Tickets</Link> / #{ticket.id}
          </p>
          <h1>{ticket.title}</h1>
          <div className="meta-row">
            <StatusBadge status={ticket.status} />
            <span className="muted">Priority: {ticket.priority}</span>
            <span className="muted">Assignee: {ticket.assignee || '—'}</span>
          </div>
          <p className="muted small">
            Created {formatDate(ticket.createdAt)} · Updated {formatDate(ticket.updatedAt)}
          </p>
        </div>
      </header>

      <ErrorBanner error={actionError} onDismiss={() => setActionError(null)} />
      {actionSuccess && (
        <div className="banner banner-success" role="status">
          {actionSuccess}
        </div>
      )}

      <div className="detail-grid">
        <form className="panel form" onSubmit={onSaveFields}>
          <h2>Edit details</h2>
          <label>
            Title
            <input
              required
              maxLength={200}
              value={editForm.title}
              onChange={(e) => setEditForm({ ...editForm, title: e.target.value })}
            />
          </label>
          <label>
            Description
            <textarea
              required
              rows={5}
              value={editForm.description}
              onChange={(e) => setEditForm({ ...editForm, description: e.target.value })}
            />
          </label>
          <label>
            Priority
            <select
              value={editForm.priority}
              onChange={(e) => setEditForm({ ...editForm, priority: e.target.value })}
            >
              {PRIORITIES.map((p) => (
                <option key={p} value={p}>
                  {p}
                </option>
              ))}
            </select>
          </label>
          <label>
            Assignee
            <input
              maxLength={200}
              value={editForm.assignee}
              onChange={(e) => setEditForm({ ...editForm, assignee: e.target.value })}
            />
          </label>
          <div className="form-actions">
            <button className="button primary" type="submit" disabled={saving}>
              {saving ? 'Saving…' : 'Save changes'}
            </button>
          </div>
        </form>

        <div className="stack">
          <form className="panel form" onSubmit={onChangeStatus}>
            <h2>Change status</h2>
            {isTerminal ? (
              <p className="muted">
                Status <strong>{ticket.status}</strong> is terminal. No further transitions are allowed.
              </p>
            ) : (
              <>
                <label>
                  Next status
                  <select
                    value={statusValue}
                    onChange={(e) => setStatusValue(e.target.value)}
                    required
                  >
                    {nextStatuses.map((s) => (
                      <option key={s} value={s}>
                        {s}
                      </option>
                    ))}
                  </select>
                </label>
                <div className="form-actions">
                  <button className="button primary" type="submit" disabled={statusSaving}>
                    {statusSaving ? 'Updating…' : 'Update status'}
                  </button>
                </div>
              </>
            )}
          </form>

          <section className="panel">
            <h2>Comments</h2>
            {(!ticket.comments || ticket.comments.length === 0) && (
              <EmptyState title="No comments yet" detail="Add the first comment below." />
            )}
            {ticket.comments?.length > 0 && (
              <ul className="comment-list">
                {ticket.comments.map((comment) => (
                  <li key={comment.id} className="comment-item">
                    <p>{comment.body}</p>
                    <p className="muted small">{formatDate(comment.createdAt)}</p>
                  </li>
                ))}
              </ul>
            )}

            <form className="form comment-form" onSubmit={onAddComment}>
              <label>
                Add comment
                <textarea
                  required
                  rows={3}
                  value={commentBody}
                  onChange={(e) => setCommentBody(e.target.value)}
                  placeholder="Write a comment"
                />
              </label>
              <div className="form-actions">
                <button className="button primary" type="submit" disabled={commentSaving}>
                  {commentSaving ? 'Posting…' : 'Add comment'}
                </button>
              </div>
            </form>
          </section>
        </div>
      </div>
    </section>
  )
}
