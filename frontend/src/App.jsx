import { NavLink, Route, Routes } from 'react-router-dom'
import TicketListPage from './pages/TicketListPage'
import CreateTicketPage from './pages/CreateTicketPage'
import TicketDetailPage from './pages/TicketDetailPage'

export default function App() {
  return (
    <div className="app-shell">
      <nav className="topnav">
        <NavLink to="/" className="brand" end>
          Support Tickets
        </NavLink>
        <div className="nav-links">
          <NavLink to="/" end>
            All tickets
          </NavLink>
          <NavLink to="/tickets/new">New ticket</NavLink>
        </div>
      </nav>
      <main className="main">
        <Routes>
          <Route path="/" element={<TicketListPage />} />
          <Route path="/tickets/new" element={<CreateTicketPage />} />
          <Route path="/tickets/:id" element={<TicketDetailPage />} />
        </Routes>
      </main>
    </div>
  )
}
