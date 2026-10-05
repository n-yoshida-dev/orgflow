import { useState } from "react";
import "./App.css";

// テスト用ID/PW
// {"loginId":"test_taro","password":"password_taro"}

function App() {
  const [loginId, setLoginId] = useState("");
  const [password, setPassword] = useState("");
  const [token, setToken] = useState(localStorage.getItem("token"));
  const [tenantList, setTenantList] = useState<
    { tenantId: string; tenantName: string }[]
  >([]);

  const loginClick = async () => {
    const res = await fetch("http://localhost:8080/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ loginId: loginId, password: password }),
    });
    const json = await res.json();
    setToken(json.accessToken);
    localStorage.setItem("token", json.accessToken);
  };

  const fetchList = async () => {
    const res = await fetch("http://localhost:8080/me/tenants", {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + token,
      },
    });
    const json = await res.json();
    setTenantList(json);
  };

  const selectTenant = async (tenantId: string) => {
    const res = await fetch(
      "http://localhost:8080/tenants/" + tenantId + "/select",
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + token,
        },
      },
    );
    const json = await res.json();
    setToken(json.accessToken);
    localStorage.setItem("token", json.accessToken);
  };

  if (token === "" || token === null) {
    return (
      <div>
        <input value={loginId} onChange={(e) => setLoginId(e.target.value)} />
        <input value={password} onChange={(e) => setPassword(e.target.value)} />
        <button onClick={loginClick}>ログイン</button>
        <p>{token}</p>
      </div>
    );
  }
  return (
    <div>
      <p>tenantを選んでください</p>
      <button onClick={fetchList}>一覧を読み込む</button>
      <ul>
        {tenantList.map((tenant) => (
          <li key={tenant.tenantId}>
            <button
              onClick={() => {
                selectTenant(tenant.tenantId);
              }}
            >
              {tenant.tenantName}
            </button>
          </li>
        ))}
      </ul>
      <p>{token}</p>
      <button
        onClick={() => {
          (localStorage.removeItem("token"), setToken(""));
        }}
      >
        ログアウト
      </button>
    </div>
  );
}

export default App;
