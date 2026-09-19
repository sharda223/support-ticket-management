export function formatApiError(error) {
  if (!error) return 'Something went wrong.'
  if (error.errors?.length) {
    const fields = error.errors
      .map((e) => (e.field ? `${e.field}: ${e.message}` : e.message))
      .join('; ')
    return `${error.message}${fields ? ` — ${fields}` : ''}`
  }
  return error.message || 'Something went wrong.'
}
