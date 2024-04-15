import React, { useState } from 'react';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loggedIn, setLoggedIn] = useState(false);

  const handleLogin = (e) => {
    e.preventDefault();
    // 실제 로그인 로직을 여기에 구현합니다.
    // 예를 들어, 서버에 로그인 요청을 보내고 응답을 처리할 수 있습니다.
    if (username === 'admin' && password === 'password') {
      setLoggedIn(true);
    } else {
      alert('잘못된 사용자 이름 또는 비밀번호입니다.');
    }
  };

  const handleLogout = () => {
    setLoggedIn(false);
    setUsername('');
    setPassword('');
  };

  return (
    <div>
      {loggedIn ? (
        <div>
          <h2>환영합니다, {username}!</h2>
          <button onClick={handleLogout}>로그아웃</button>
        </div>
      ) : (
        <form onSubmit={handleLogin}>
          <h2>로그인</h2>
          <div>
            <label>사용자 이름:</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
            />
          </div>
          <div>
            <label>비밀번호:</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>
          <button type="submit">로그인</button>
        </form>
      )}
    </div>
  );
};

export default Login;