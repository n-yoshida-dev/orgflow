import { useState } from 'react';
import './App.css'

// テスト用ID/PW
// {"loginId":"test_taro","password":"password_taro"}

function App() {
  const [loginId, setLoginId] = useState("");
  const [password, setPassword] = useState("");
  const [token, setToken] = useState(localStorage.getItem("token"));

  const loginClick = async () => {
      const res = await fetch("http://localhost:8080/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({"loginId":loginId,"password":password}),
      });
      const json = await res.json();
      setToken(json.accessToken)
      localStorage.setItem("token", json.accessToken)
    };

  return(
  <div>
    <input value={loginId} onChange={(e) => setLoginId(e.target.value)} />
    <input value={password} onChange={(e) => setPassword(e.target.value)} />
    <button onClick={loginClick}>ログイン</button>
    <p>{token}</p>
  </div>
  )
}

export default App;