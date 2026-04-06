import { useState } from "react";
import { jwtDecode } from "jwt-decode";
import API from "../services/api";
import "../index.css";

function Login({ setToken }) {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isRegister, setIsRegister] = useState(false);

  const handleSubmit = async () => {
    try {
      if (isRegister) {
        await API.post("/auth/register", {
          username,
          email,
          password,
        });

        alert("Registered successfully! Now login.");
        setIsRegister(false);
        setUsername("");
        setEmail("");
        setPassword("");
      } else {
        const res = await API.post("/auth/login", { email, password });

          const token = res.data;

          const decoded = jwtDecode(token);

          const userId = decoded.userId || decoded.id || decoded.user_id;

          localStorage.setItem("token", token);
          localStorage.setItem("userId", userId);

          setToken(token);
      }
    } catch (err) {
      alert("Error: " + (err.response?.data || "Something went wrong"));
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h1 className="app-title"> Habit Tracker</h1>
        <h2>{isRegister ? " Register" : " Login"}</h2>

        {isRegister && (
          <input
            className="auth-input"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
        )}

        <input
          className="auth-input"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />

        <input
          className="auth-input"
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <button className="auth-btn" onClick={handleSubmit}>
          {isRegister ? "Register" : "Login"}
        </button>

        <p
          className="auth-toggle"
          onClick={() => setIsRegister(!isRegister)}
        >
          {isRegister
            ? "Already have an account? Login"
            : "New user? Register"}
        </p>
      </div>
    </div>
  );
}

export default Login;