import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Login from "./Login.jsx"; 
import Dashboard from "./Dashboard.jsx";
import ClientDashboard from "./ClientDashboard.jsx";
import MonitoringPage from "./MonitoringPage.jsx"; 

function App() {
    const token = localStorage.getItem("token");

    return (
        <Router>
            <Routes>
                <Route path="/" element={<Navigate to="/login" />} />
                <Route path="/login" element={<Login />} />
                <Route path="/admin-dashboard" element={<Dashboard />} />
                <Route path="/client-dashboard" element={<ClientDashboard />} />
                <Route path="/monitoring" element={<MonitoringPage />} />
            </Routes>
        </Router>
    );
}

export default App;