import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Login from "./Login.jsx";
import Dashboard from "./Dashboard.jsx";
import ClientDashboard from "./ClientDashboard.jsx";

function App() {
    const token = localStorage.getItem("token");
    let role = null;

    if (token) {
        try {
            const decoded = JSON.parse(atob(token.split(".")[1]));
            role = decoded.role || "CLIENT";
        } catch {}
    }

    return (
        <Router>
            <Routes>
                <Route path="/" element={<Navigate to="/login" />} />
                <Route path="/login" element={<Login />} />
                <Route path="/admin-dashboard" element={<Dashboard />} />
                <Route path="/client-dashboard" element={<ClientDashboard />} />
            </Routes>
        </Router>
    );
}

export default App;
