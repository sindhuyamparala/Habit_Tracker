import { useState } from "react";
import API from "../services/api";

function AddHabit({ refresh }) {
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");

  const handleAdd = async () => {
    await API.post("/habits", {
      name,
      description,
    });

    setName("");
    setDescription("");
    refresh(); 
  };

  return (
    <div>
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

      <button onClick={handleAdd}>Add</button>
    </div>
  );
}

export default AddHabit;