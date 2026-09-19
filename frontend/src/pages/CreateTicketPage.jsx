import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { createTicket } from '../api'
import { PRIORITIES } from '../constants'
import ErrorBanner from '../components/ErrorBanner'

const initialForm = {
  title: '',
  description: '',
  priority: 'MEDIUM',
  assignee: '',
}

export default function CreateTicketPage() {
  const navigate = useNavigate()
  const [form, setForm] = useState(initialForm)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState(null)

  function updateField(field, value) {
    setForm((prev) => ({ ...prev, [field]: value }))
  }

  async function onSubmit(event) {
    event.preventDefault()
    setSubmitting(true)
    setError(null)
    try {
      const payload = {
        title: form.title.trim(),
        description: form.description.trim(),
        priority: form.priority,
        assignee: form.assignee.trim() || null,
      }
      const created = await createTicket(payload)
      navigate(`/tickets/${created.id}`)
    } catch (err) {
      setError(err)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <section className="page">
      <header className="page-header">
        <div>
          <p className="crumb">
            <Link to="/">Tickets</Link> / New
          </p>
          <h1>Create ticket</h1>
        </div>
      </header>

      <ErrorBanner error={error} onDismiss={() => setError(null)} />

      <form className="form panel" onSubmit={onSubmit}>
        <label>
          Title
          <input
            required
            maxLength={200}
            value={form.title}
            onChange={(e) => updateField('title', e.target.value)}
          />
        </label>

        <label>
          Description
          <textarea
            required
            rows={5}
            value={form.description}
            onChange={(e) => updateField('description', e.target.value)}
          />
        </label>

        <label>
          Priority
          <select
            value={form.priority}
            onChange={(e) => updateField('priority', e.target.value)}
          >
            {PRIORITIES.map((p) => (
              <option key={p} value={p}>
                {p}
              </option>
            ))}
          </select>
        </label>

        <label>
          Assignee <span className="muted">(optional)</span>
          <input
            maxLength={200}
            value={form.assignee}
            onChange={(e) => updateField('assignee', e.target.value)}
            placeholder="name or email"
          />
        </label>

        <div className="form-actions">
          <Link className="button" to="/">
            Cancel
          </Link>
          <button className="button primary" type="submit" disabled={submitting}>
            {submitting ? 'Creating…' : 'Create ticket'}
          </button>
        </div>
      </form>
    </section>
  )
}
