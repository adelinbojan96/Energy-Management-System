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
        baseURL: "http://localhost:8080/api",
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
    });

    const fetchUsers = async () => {
        try {
            const res = await api.get("/users");
            const filteredUsers = res.data.filter(
                (u) => !(u.name?.toLowerCase() === "admin")
            );
            setUsers(filteredUsers);
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

    const openEditModal = (type, item) => {
        setModalType(type);
        setFormData(item);
        setShowModal(true);
    };

    const handleInputChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSave = async () => {
        try {
            if (modalType === "user") {
                if (formData.id) {
                    // Edit user
                    await api.put(`/users/${formData.id}`, formData);
                    alert("User updated successfully!");
                } else {
                    // Add new user
                    const credentialRes = await api.post("/auth/register", {
                        username: formData.username,
                        password: formData.password,
                        role: formData.role,
                    });

                    const credentialId = credentialRes.data.id;

                    const userPayload = {
                        name: formData.name,
                        age: parseInt(formData.age),
                        email: formData.email,
                        role: formData.role,
                        credentialId: credentialId,
                    };

                    await api.post("/users", userPayload);
                    alert("User added successfully!");
                }
                fetchUsers();
            } else if (modalType === "device") {
                if (formData.id) {
                    await api.put(`/device/${formData.id}`, formData);
                    alert("Device updated successfully!");
                } else {
                    await api.post("/device", {
                        name: formData.name,
                        description: formData.description,
                        maxConsumption: parseFloat(formData.maxConsumption),
                        location: formData.location,
                    });
                    alert("Device added successfully!");
                }
                fetchDevices();
            }

            setShowModal(false);
        } catch (err) {
            console.error("Error saving:", err.response?.data || err);
            alert("Failed to save item: " + (err.response?.data?.message || "Check backend logs"));
        }
    };

    const handleDelete = async (type, id) => {
        const confirmed = window.confirm("Are you sure you want to delete this?");
        if (!confirmed) return;

        try {
            if (type === "user") {
                await api.delete(`/users/${id}`);
                fetchUsers();
            } else if (type === "device") {
                await api.delete(`/device/${id}`);
                fetchDevices();
            }

            alert("Deleted successfully!");
        } catch (err) {
            console.error("Error deleting:", err.response?.data || err);
            alert("Failed to delete item: " + (err.response?.data?.message || "Check backend logs"));
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

            {/* USERS */}
            {activeTab === "users" && (
                <div className="section">
                    <div className="section-header">
                        <h2>Manage Users</h2>
                        <button className="add-btn" onClick={() => openModal("user")}>+ Add User</button>
                    </div>
                    <table className="styled-table">
                        <thead>
                        <tr>
                            <th rowSpan="2">ID</th>
                            <th rowSpan="2">Name</th>
                            <th rowSpan="2">Age</th>
                        </tr>
                        <tr>
                            <th>Edit</th>
                            <th>Delete</th>
                        </tr>
                        </thead>
                        <tbody>
                        {users.map((u) => (
                            <tr key={u.id}>
                                <td>{u.id}</td>
                                <td>{u.name}</td>
                                <td>{u.age}</td>
                                <td>
                                    <button className="edit-btn" onClick={() => openEditModal("user", u)}>Edit</button>
                                </td>
                                <td>
                                    <button className="delete-btn" onClick={() => handleDelete("user", u.id)}>Delete</button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}

            {/* DEVICES */}
            {activeTab === "devices" && (
                <div className="section">
                    <div className="section-header">
                        <h2>Manage Devices</h2>
                        <button className="add-btn" onClick={() => openModal("device")}>+ Add Device</button>
                    </div>
                    <table className="styled-table">
                        <thead>
                        <tr>
                            <th rowSpan="2">ID</th>
                            <th rowSpan="2">Name</th>
                            <th rowSpan="2">Description</th>
                            <th rowSpan="2">Consumption</th>
                            <th rowSpan="2">Location</th>
                            <th colSpan="2" className="action-header">Actions</th>
                        </tr>
                        <tr>
                            <th>Edit</th>
                            <th>Delete</th>
                        </tr>
                        </thead>
                        <tbody>
                        {devices.length === 0 ? (
                            <tr>
                                <td colSpan={7} className="empty-cell">No devices found</td>
                            </tr>
                        ) : (
                            devices.map((d) => (
                                <tr key={d.id}>
                                    <td>{d.id}</td>
                                    <td>{d.name}</td>
                                    <td>{d.description}</td>
                                    <td>{d.maxConsumption}</td>
                                    <td>{d.location}</td>
                                    <td>
                                        <button className="edit-btn" onClick={() => openEditModal("device", d)}>Edit</button>
                                    </td>
                                    <td>
                                        <button className="delete-btn" onClick={() => handleDelete("device", d.id)}>Delete</button>
                                    </td>
                                </tr>
                            ))
                        )}
                        </tbody>
                    </table>
                </div>
            )}


            {/* ASSIGN */}
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

            {/* MODAL */}
            {showModal && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h3>
                            {formData.id
                                ? `Edit ${modalType === "user" ? "User" : "Device"}`
                                : `Add ${modalType === "user" ? "User" : "Device"}`}
                        </h3>
                        {modalType === "user" ? (
                            <>
                                <input name="name" placeholder="Full Name" value={formData.name || ""} onChange={handleInputChange} />
                                <input name="age" placeholder="Age" type="number" value={formData.age || ""} onChange={handleInputChange} />
                                <input name="email" placeholder="Email" type="email" value={formData.email || ""} onChange={handleInputChange} />
                                {!formData.id && (
                                    <>
                                        <input name="username" placeholder="Username" onChange={handleInputChange} />
                                        <input name="password" placeholder="Password" type="password" onChange={handleInputChange} />
                                        <select name="role" onChange={handleInputChange} defaultValue="">
                                            <option value="" disabled>Select Role</option>
                                            <option value="ADMIN">Admin</option>
                                            <option value="CLIENT">Client</option>
                                        </select>
                                    </>
                                )}
                            </>
                        ) : (
                            <>
                                <input name="name" placeholder="Device Name" value={formData.name || ""} onChange={handleInputChange} />
                                <input name="description" placeholder="Description" value={formData.description || ""} onChange={handleInputChange} />
                                <input name="maxConsumption" placeholder="Consumption" type="number" value={formData.maxConsumption || ""} onChange={handleInputChange} />
                                <input name="location" placeholder="Location" value={formData.location || ""} onChange={handleInputChange} />
                            </>
                        )}

                        <div className="modal-actions">
                            <button className="confirm-btn" onClick={handleSave}>
                                {formData.id ? "Save Changes" : "Add"}
                            </button>
                            <button className="cancel-btn" onClick={() => setShowModal(false)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default Dashboard;
