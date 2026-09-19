export default function StatusBadge({ status }) {
  const tone = status?.toLowerCase().replace('_', '-') || 'unknown'
  return <span className={`badge badge-${tone}`}>{status}</span>
}
