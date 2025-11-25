import { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

function ClientDashboard() {
    const [devices, setDevices] = useState([]);
    const [username, setUsername] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (!token) return;

        try {
            const decoded = JSON.parse(atob(token.split(".")[1]));
            setUsername(decoded.sub || decoded.username || "Client");
            fetchAssignedDevices(decoded.id || decoded.userId);
        } catch (err) {
            console.error("Invalid token", err);
        }
    }, []);

    const api = axios.create({
        baseURL:'http://localhost:8088/api',
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
    });

    const fetchAssignedDevices = async (userId) => {
        try {
            const res = await api.get(`/device`);
            const assigned = res.data.filter((d) => d.userId === userId);
            setDevices(assigned);
        } catch (err) {
            console.error("Failed to load devices", err);
        }
    };

    const goToMonitoring = () => {
        navigate("/monitoring");
    };

    return (
        <div className="client-container">
            <h1 className="client-header">Welcome, {username}</h1>

            <div className="button-container">
                <button 
                    className="monitoring-button" 
                    onClick={goToMonitoring}
                >
                    View My Energy Consumption
                </button>
            </div>

            <h2 className="client-subheader">Your Assigned Devices</h2>

            <div className="device-section">
                {devices.length === 0 ? (
                    <div className="empty-state">
                        <div className="empty-icon">📟</div>
                        <h3>No devices assigned yet</h3>
                        <p>Please contact your administrator for device assignment.</p>
                    </div>
                ) : (
                    <table className="device-table">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Description</th>
                            <th>Max Consumption</th>
                            <th>Location</th>
                        </tr>
                        </thead>
                        <tbody>
                        {devices.map((d) => (
                            <tr key={d.id}>
                                <td>{d.id}</td>
                                <td>{d.name}</td>
                                <td>{d.description}</td>
                                <td>{d.maxConsumption}</td>
                                <td>{d.location}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
}

export default ClientDashboard;