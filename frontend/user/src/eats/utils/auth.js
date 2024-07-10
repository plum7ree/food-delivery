// utils/auth.js
export const getAuthToken = () => {
  return new Promise((resolve) => {
    if (document.readyState === 'complete') {
      resolve(getCookieValue('AUTH_TOKEN'));
    } else {
      window.addEventListener('load', () => {
        resolve(getCookieValue('AUTH_TOKEN'));
      });
    }
  });
};

const getCookieValue = (name) => {
  const cookies = document.cookie.split(';');
  for (let cookie of cookies) {
    const [cookieName, cookieValue] = cookie.trim().split('=');
    if (cookieName === name) {
      return cookieValue;
    }
  }
  return null;
};