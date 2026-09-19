import { formatApiError } from '../utils/errors'

export default function ErrorBanner({ error, onDismiss }) {
  if (!error) return null
  const text = typeof error === 'string' ? error : formatApiError(error)

  return (
    <div className="banner banner-error" role="alert">
      <span>{text}</span>
      {onDismiss && (
        <button type="button" className="banner-dismiss" onClick={onDismiss} aria-label="Dismiss">
          ×
        </button>
      )}
    </div>
  )
}
