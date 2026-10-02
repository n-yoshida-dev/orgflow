import './App.css'

function App() {
  const handleClick = async () => {
    const res = await fetch("http://localhost:8080/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({"loginId":"test_taro","password":"password_taro"}),
    });
    console.log(res.status);
  };

  return <button onClick={handleClick}>login を叩く</button>;
}

export default App;