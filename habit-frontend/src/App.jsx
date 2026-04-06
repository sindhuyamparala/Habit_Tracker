import { useState, useEffect } from "react";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import { jwtDecode } from "jwt-decode";

function App() {
  const [token, setToken] = useState(null);

  useEffect(() => {
    const storedToken = localStorage.getItem("token");

    if (!storedToken) return;

    try {
      const decoded = jwtDecode(storedToken);

      if (decoded.exp * 1000 > Date.now()) {
        setToken(storedToken);
      } else {
        localStorage.removeItem("token");
        localStorage.removeItem("userId");
      }
    } catch (err) {
      localStorage.removeItem("token");
      localStorage.removeItem("userId");
    }
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("userId");
    setToken(null);
  };

  if (!token) {
    return <Login setToken={setToken} />;
  }

  return <Dashboard setToken={setToken} onLogout={handleLogout} />;
}

export default App;