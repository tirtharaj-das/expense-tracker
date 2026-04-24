import { useState, useEffect } from "react";

function App() {
  const [expenses, setExpenses] = useState([]);
  const [total, setTotal] = useState(0);

  const [form, setForm] = useState({
    amount: "",
    category: "",
    description: "",
    date: ""
  });

  const [categoryFilter, setCategoryFilter] = useState("");
  const [sort, setSort] = useState("");

  const fetchExpenses = async () => {
    let url = "http://localhost:8080/expenses";

    const params = [];
    if (categoryFilter) params.push(`category=${categoryFilter}`);
    if (sort) params.push(`sort=${sort}`);

    if (params.length > 0) {
      url += "?" + params.join("&");
    }

    const res = await fetch(url);
    const data = await res.json();

    setExpenses(data.expenses);
    setTotal(data.total);
  };

  useEffect(() => {
    fetchExpenses();
  }, []);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    await fetch("http://localhost:8080/expenses", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Idempotency-Key": Date.now().toString()
      },
      body: JSON.stringify(form)
    });

    setForm({ amount: "", category: "", description: "", date: "" });
    fetchExpenses();
  };

  return (
    <div style={{ padding: 20 }}>
      <h2>Expense Tracker</h2>

      <form onSubmit={handleSubmit}>
        <input name="amount" placeholder="Amount" value={form.amount} onChange={handleChange} />
        <input name="category" placeholder="Category" value={form.category} onChange={handleChange} />
        <input name="description" placeholder="Description" value={form.description} onChange={handleChange} />
        <input type="date" name="date" value={form.date} onChange={handleChange} />
        <button type="submit">Add</button>
      </form>

      <hr />

      <input
        placeholder="Filter by category"
        value={categoryFilter}
        onChange={(e) => setCategoryFilter(e.target.value)}
      />

      <button onClick={() => setSort("date_desc")}>Sort by Date</button>
      <button onClick={fetchExpenses}>Apply</button>

      <h3>Total: ₹{total}</h3>

      <ul>
        {expenses.map((e) => (
          <li key={e.id}>
            {e.category} - ₹{e.amount} - {e.description} ({e.date})
          </li>
        ))}
      </ul>
    </div>
  );
}

export default App;