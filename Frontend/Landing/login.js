const loginForm = document.getElementById('login-form');
const loginMessage = document.getElementById('login-message');
const verifyButton = document.getElementById('verify-button');
const verificationMessage = document.getElementById('verification-message');

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

    await fetch('http://localhost:5050/send_code', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email }),
    });

    loginForm.style.display = 'none';
    document.getElementById('verification-section').style.display = 'block';

  } catch (error) {
    loginMessage.textContent = 'Start the Java server, then try again.';
  }
});

verifyButton.addEventListener('click', async () => {
  const email = document.getElementById('email').value.trim();
  const code = document.getElementById('verification-code').value.trim();

  verificationMessage.textContent = 'Checking code...';

  try {
    const response = await fetch('http://localhost:5050/verify_code', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, code }),
    });

    const result = await response.text();

    if (result === 'success') {
      window.location.href = 'index.html';
    } else {
      verificationMessage.textContent = result;
    }
  } catch (error) {
    verificationMessage.textContent = 'Could not reach verification server.';
  }
});
