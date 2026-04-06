import { useState, useEffect } from "react";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
import API from "../services/api";
import "../App.css";

function Dashboard({ setToken }) {
  const [date, setDate] = useState(new Date());
  const [monthData, setMonthData] = useState([]);
  const [habits, setHabits] = useState([]);
  const [statusMap, setStatusMap] = useState({});
  const [selectedStatus, setSelectedStatus] = useState({});
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [monthlyStreaks, setMonthlyStreaks] = useState({});

  const fetchHabits = async () => {
    const res = await API.get("/habits");
    setHabits(res.data);
  };

  useEffect(() => {
    fetchHabits();
  }, []);

  const fetchMonthData = async (selectedDate) => {
    const month = selectedDate.getMonth() + 1;
    const year = selectedDate.getFullYear();

    const res = await API.get(
      `/track/month?month=${month}&year=${year}`
    );

    setMonthData(res.data);
  };

  useEffect(() => {
    fetchMonthData(date);
  }, [date]);

  const fetchMonthlyStreaks = async (selectedDate) => {
    const month = selectedDate.getMonth() + 1;
    const year = selectedDate.getFullYear();

    const res = await API.get(
      `/track/monthly-streak?month=${month}&year=${year}`
    );

    console.log("Monthly streaks:", res.data); // DEBUG

    setMonthlyStreaks(res.data);
  };

  useEffect(() => {
    fetchMonthlyStreaks(date);
  }, [date]);

  const tileClassName = ({ date, view }) => {
    if (view !== "month") return "";

    const formattedDate = date.toLocaleDateString("en-CA");

    const dayData = monthData.find(
      (d) => d.date === formattedDate
    );

    if (!dayData) return "";

    if (dayData.status === "GREEN") return "green";
    if (dayData.status === "YELLOW") return "yellow";
    if (dayData.status === "RED") return "red";

    return "";
  };

  const fetchStatus = async () => {
    const selectedDate = date.toLocaleDateString("en-CA");

    const res = await API.get(`/track/date?date=${selectedDate}`);

    const map = {};
    res.data.forEach((item) => {
      map[item.habit.id] = item.status;
    });

    setStatusMap(map);
    setSelectedStatus(map);
  };

  useEffect(() => {
    fetchStatus();
  }, [date]);

  const handleAddHabit = async () => {
    await API.post("/habits", { name, description });
    setName("");
    setDescription("");
    fetchHabits();
  };

  const deleteHabit = async (id) => {
    await API.delete(`/habits/${id}`);
    fetchHabits();
  };

  const saveAllStatuses = async () => {
    const selectedDate = date.toLocaleDateString("en-CA");
    const entries = Object.entries(selectedStatus);

    if (entries.length === 0) {
      alert("Please select at least one habit");
      return;
    }

    try {
      for (const [habitId, status] of entries) {
        if (!status) continue;

        await API.post(
          `/track?habitId=${habitId}&date=${selectedDate}&status=${status}`
        );
      }

      alert("Saved successfully!");
      await fetchHabits();
      await fetchMonthData(date);
      await fetchStatus();

    } catch (error) {
      console.error(error);
      alert("Error saving habits");
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    setToken(null);
  };

  return (
    <div className="container">
      <button className="logout-btn" onClick={handleLogout}>
        Logout
      </button>

      <h2>✨ Habit Tracker</h2>

      {/* 📅 Calendar */}
      <div className="calendar-container">
        <Calendar
          onChange={setDate}
          value={date}
          tileClassName={tileClassName}
        />
      </div>

      <h3>Selected Date: {date.toDateString()}</h3>

      {/* 📋 Habit List */}
      <h3>Habits</h3>

      {habits.map((habit) => {
        const currentStatus =
          selectedStatus[habit.id] || statusMap[habit.id];

        return (
          <div key={habit.id} className="card">
            <div className="habit-row">

              {/* LEFT */}
              <div>
                <div className="habit-name">{habit.name}</div>

                {/* ✅ NEW: Description */}
                <div className="habit-description">
                  {habit.description}
                </div>

                <div className="streak">
                  🏆 {monthlyStreaks[habit.id] || 0}
                </div>
              </div>

              {/* MIDDLE */}
              <div className="habit-actions">
                <label>
                  <input
                    type="radio"
                    name={`habit-${habit.id}`}
                    checked={currentStatus === "DONE"}
                    onChange={() =>
                      setSelectedStatus({
                        ...selectedStatus,
                        [habit.id]: "DONE",
                      })
                    }
                  />
                  Done
                </label>

                <label>
                  <input
                    type="radio"
                    name={`habit-${habit.id}`}
                    checked={currentStatus === "NOT_DONE"}
                    onChange={() =>
                      setSelectedStatus({
                        ...selectedStatus,
                        [habit.id]: "NOT_DONE",
                      })
                    }
                  />
                  Not Done
                </label>
              </div>

              {/* STATUS */}
              <div className="status">
                {currentStatus === "DONE" && "🟢"}
                {currentStatus === "NOT_DONE" && "🔴"}
                {!currentStatus && "⚪"}
              </div>

              {/* DELETE */}
              <button
                className="delete-btn"
                onClick={() => deleteHabit(habit.id)}
              >
                🗑
              </button>

            </div>
          </div>
        );
      })}

      <button className="save-btn" onClick={saveAllStatuses}>
         Save All
      </button>

      {/* ADD HABIT */}
      <div className="card">
        <h3>Add Habit</h3>
        <input
          placeholder="Habit name"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />
        <input
          placeholder="Description"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
        <button onClick={handleAddHabit}>Add</button>
      </div>
    </div>
  );
}

export default Dashboard;