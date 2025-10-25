import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import "./App.css";
import TestPage from "./TestPage.jsx";

function Login() {
    return (
        <div className="login-page">
            <h1 className="title">Energy Management System</h1>

            <form className="login-box">
                <h2 className="login-header">Log Into Your Account</h2>

                <div className="input-group">
                    <label htmlFor="username">Username</label>
                    <input id="username" type="text" placeholder="Enter your username" />
                </div>

                <div className="input-group">
                    <label htmlFor="password">Password</label>
                    <input id="password" type="password" placeholder="Enter your password" />
                </div>

                <button type="submit" className="login-btn">Log In</button>

                <p className="test-link">
                    or <Link to="/test">Go to Test Page</Link>
                </p>
            </form>
        </div>
    );
}

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Login />} />
                <Route path="/test" element={<TestPage />} />
            </Routes>
        </Router>
    );
}

export default App;
