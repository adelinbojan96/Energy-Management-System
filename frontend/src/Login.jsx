import { useState } from "react";
import "./Login.css";
import { useNavigate } from "react-router-dom";
import axios from "axios";

function Login() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setError("");
        setLoading(true);

        try {
            const response = await axios.post("http://localhost:8088/api/auth/login", {
                username,
                password,
            });

            const token = response.data;
            localStorage.setItem("token", token);

            const decoded = JSON.parse(atob(token.split(".")[1]));
            
            const credentialId = decoded.credentialId; 
            console.log("Credential ID din token:", credentialId);

            const userResponse = await axios.get(`http://localhost:8088/api/users/by-credential/${credentialId}`, {
                headers: { Authorization: `Bearer ${token}` }
            });

            const realUserId = userResponse.data.id;
            
            if (realUserId) {
                localStorage.setItem("userId", realUserId);
                console.log("User ID corect salvat:", realUserId);
            }
            const role = decoded.role || "CLIENT";
            if (role === "ADMIN") {
                navigate("/admin-dashboard");
            } else {
                navigate("/client-dashboard");
            }

        } catch (err) {
            console.error("Login error:", err);
            if (err.response && err.response.status === 401) {
                setError("Invalid username or password");
            } else {
                setError("Login failed. Check console.");
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-page">
            <h1 className="title">Energy Management System</h1>
            <form className="login-box" onSubmit={handleLogin}>
                <h2 className="login-header">Log Into Your Account</h2>
                <div className="input-group">
                    <label htmlFor="username">Username</label>
                    <input
                        id="username"
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        placeholder="Enter your username"
                        required
                    />
                </div>
                <div className="input-group">
                    <label htmlFor="password">Password</label>
                    <input
                        id="password"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Enter your password"
                        required
                    />
                </div>
                {error && <p className="error-message">{error}</p>}
                <button type="submit" className="login-btn" disabled={loading}>
                    {loading ? "Logging in..." : "Log In"}
                </button>
            </form>
        </div>
    );
}

export default Login;