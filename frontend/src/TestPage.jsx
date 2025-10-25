import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import axios from "axios";

function TestPage() {
    const [clients, setClients] = useState([]);
    const [devices, setDevices] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        axios
            .get("http://localhost:8081/users")
            .then((response) => {
                setClients(response.data);
                setLoading(false);
            })
            .catch((err) => {
                console.error(err);
                setError("Error fetching clients");
                setLoading(false);
            });
    }, []);
    useEffect(() => {
        axios
            .get("http://localhost:8080/device")
            .then((response) => {
                setDevices(response.data);
                setLoading(false);
            })
            .catch((err) => {
                console.error(err);
                setError("Error fetching devices");
                setLoading(false);
            });
    }, []);

    if (loading) return <p>Loading...</p>;
    if (error) return <p>{error}</p>;

    return (
        <div>
            <h1>Clients</h1>
            {clients.length === 0 ? (
                <p>No clients found.</p>
            ) : (
                <ul>
                    {clients.map((client) => (
                        <li key={client.id}>
                            {client.id} - {client.name} - {client.age}
                        </li>
                    ))}
                </ul>
            )}

            <h2>Devices</h2>
            {devices.length === 0 ? (
                <p>No devices found.</p>
            ) : (
                <ul>
                    {devices.map((device) => (
                        <li key={device.id}>
                            {device.id} - {device.name}
                        </li>
                    ))}
                </ul>
            )}
            <Link to="/">Back to Login</Link>
        </div>
    );
}

export default TestPage;
