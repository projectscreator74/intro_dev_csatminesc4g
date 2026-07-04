const loginForm = document.getElementById('login-form');
const loginMessage = document.getElementById('login-message');

loginForm.addEventListener('submit', async (event) => {
  event.preventDefault();

  const email = document.getElementById('email').value.trim();
  const password = document.getElementById('password').value;

  loginMessage.textContent = 'Checking login...';

  try {
    const response = await fetch('/api/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ email, password }),
    });

    const result = await response.json();

    if (!response.ok || !result.success) {
      loginMessage.textContent = result.message || 'Invalid email or password.';
      return;
    }

    localStorage.setItem('studystackUserEmail', email);
    window.location.href = 'index.html';
  } catch (error) {
    loginMessage.textContent = 'Start the Java server, then try again.';
  }
});
