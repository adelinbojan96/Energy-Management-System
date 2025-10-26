import { useState, useEffect } from "react";
import axios from "axios";
import "./Dashboard.css";

function Dashboard() {
    const [activeTab, setActiveTab] = useState("users");
    const [users, setUsers] = useState([]);
    const [devices, setDevices] = useState([]);
    const [selectedUser, setSelectedUser] = useState("");
    const [selectedDevice, setSelectedDevice] = useState("");
    const [showModal, setShowModal] = useState(false);
    const [modalType, setModalType] = useState("");
    const [formData, setFormData] = useState({});

    useEffect(() => {
        fetchUsers();
        fetchDevices();
    }, []);

    const api = axios.create({
        baseURL: "http://localhost:8080/api", // gateway base URL
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`, // send JWT token
        },
    });

    const fetchUsers = async () => {
        try {
            const res = await api.get("/users");
            setUsers(res.data);
        } catch (err) {
            console.error("Failed to load users", err);
        }
    };

    const fetchDevices = async () => {
        try {
            const res = await api.get("/device");
            setDevices(res.data);
        } catch (err) {
            console.error("Failed to load devices", err);
        }
    };

    const assignDevice = async () => {
        if (!selectedUser || !selectedDevice) return alert("Select both user and device");
        try {
            await api.post("/device/assign", {
                userId: selectedUser,
                deviceId: selectedDevice,
            });
            alert("Device assigned successfully!");
        } catch (err) {
            console.error("Failed to assign device", err);
            alert("Failed to assign device");
        }
    };

    const openModal = (type) => {
        setModalType(type);
        setFormData({});
        setShowModal(true);
    };

    const handleInputChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleAdd = async () => {
        try {
            if (modalType === "user") {
                await api.post("/users", formData);
                fetchUsers();
            } else if (modalType === "device") {
                await api.post("/device", formData);
                fetchDevices();
            }
            alert("Added successfully!");
            setShowModal(false);
        } catch (err) {
            console.error("Error adding:", err);
            alert("Failed to add item");
        }
    };

    const handleDelete = async (type, id) => {
        const confirmed = window.confirm("Are you sure you want to delete this?");
        if (!confirmed) return;

        try {
            if (type === "user") {
                await api.delete(`/users/${id}`);
                fetchUsers();
            } else {
                await api.delete(`/device/${id}`);
                fetchDevices();
            }
            alert("Deleted successfully!");
        } catch (err) {
            console.error("Error deleting:", err);
            alert("Failed to delete item");
        }
    };

    return (
        <div className="dashboard">
            <h1>Admin Dashboard</h1>

            <div className="tab-buttons">
                <button className={activeTab === "users" ? "active" : ""} onClick={() => setActiveTab("users")}>
                    Users
                </button>
                <button className={activeTab === "devices" ? "active" : ""} onClick={() => setActiveTab("devices")}>
                    Devices
                </button>
                <button className={activeTab === "assign" ? "active" : ""} onClick={() => setActiveTab("assign")}>
                    Assign Devices
                </button>
            </div>

            {activeTab === "users" && (
                <div className="section">
                    <div className="section-header">
                        <h2>Manage Users</h2>
                        <button className="add-btn" onClick={() => openModal("user")}>+ Add User</button>
                    </div>
                    <table>
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Username</th>
                            <th>Age</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {users.map((u) => (
                            <tr key={u.id}>
                                <td>{u.id}</td>
                                <td>{u.name}</td>
                                <td>{u.age}</td>
                                <td>
                                    <button className="delete-btn" onClick={() => handleDelete("user", u.id)}>Delete</button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}

            {activeTab === "devices" && (
                <div className="section">
                    <div className="section-header">
                        <h2>Manage Devices</h2>
                        <button className="add-btn" onClick={() => openModal("device")}>+ Add Device</button>
                    </div>
                    <table>
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Max Consumption</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {devices.map((d) => (
                            <tr key={d.id}>
                                <td>{d.id}</td>
                                <td>{d.name}</td>
                                <td>{d.maxConsumption}</td>
                                <td>
                                    <button className="delete-btn" onClick={() => handleDelete("device", d.id)}>Delete</button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}

            {activeTab === "assign" && (
                <div className="section">
                    <h2>Assign Device to User</h2>
                    <div className="assign-panel">
                        <select onChange={(e) => setSelectedUser(e.target.value)} value={selectedUser}>
                            <option value="">Select User</option>
                            {users.map((u) => (
                                <option key={u.id} value={u.id}>{u.name}</option>
                            ))}
                        </select>
                        <select onChange={(e) => setSelectedDevice(e.target.value)} value={selectedDevice}>
                            <option value="">Select Device</option>
                            {devices.map((d) => (
                                <option key={d.id} value={d.id}>{d.name}</option>
                            ))}
                        </select>
                        <button onClick={assignDevice}>Assign</button>
                    </div>
                </div>
            )}

            {showModal && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h3>{modalType === "user" ? "Add User" : "Add Device"}</h3>
                        {modalType === "user" ? (
                            <>
                                <input name="name" placeholder="Full Name" onChange={handleInputChange} />
                                <input name="age" placeholder="Age" type="number" onChange={handleInputChange} />
                                <input name="email" placeholder="Email" type="email" onChange={handleInputChange} />
                                <input name="username" placeholder="Username" onChange={handleInputChange} />
                                <input name="password" placeholder="Password" type="password" onChange={handleInputChange} />
                            </>
                        ) : (
                            <>
                                <input name="name" placeholder="Device Name" onChange={handleInputChange} />
                                <input name="maxConsumption" placeholder="Max Consumption" type="number" onChange={handleInputChange} />
                            </>
                        )}

                        <div className="modal-actions">
                            <button className="confirm-btn" onClick={handleAdd}>Add</button>
                            <button className="cancel-btn" onClick={() => setShowModal(false)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default Dashboard;
