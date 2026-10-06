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
  const [isDraft, setIsDraft] = useState(false);

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

  const logout = () => {
    localStorage.removeItem("token");
    setToken("");
    setIsDraft(false);
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

  if (isDraft === true) {
    return (
      <div>
        <p>ドラフト作成</p>
        <button onClick={logout}>ログアウト</button>
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
      <button
        onClick={() => {
          setIsDraft(true);
        }}
      >
        申請
      </button>
      <p>{token}</p>
      <button onClick={logout}>ログアウト</button>
    </div>
  );
}

export default App;
