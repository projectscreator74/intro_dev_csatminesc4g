const loginSection = document.getElementById('login-section');
const verificationSection = document.getElementById('verification-section');
const registerStep1Section = document.getElementById('register-step1-section');
const registerStep2Section = document.getElementById('register-step2-section');
const registerStep3Section = document.getElementById('register-step3-section');

function hideAllSections() {
  loginSection.style.display = 'none';
  verificationSection.style.display = 'none';
  registerStep1Section.style.display = 'none';
  registerStep2Section.style.display = 'none';
  registerStep3Section.style.display = 'none';
}

document.getElementById('show-register-link').addEventListener('click', (e) => {
  e.preventDefault();
  hideAllSections();
  registerStep1Section.style.display = 'block';
});

document.getElementById('show-login-link').addEventListener('click', (e) => {
  e.preventDefault();
  hideAllSections();
  loginSection.style.display = 'block';
});

// ===== Existing login flow =====

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
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });

    const result = await response.json();

    if (!response.ok || !result.success) {
      loginMessage.textContent = result.message || 'Invalid email or password.';
      return;
    }

    localStorage.setItem('studystackUserEmail', email);

    await fetch('/api/send-code', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email }),
    });

    hideAllSections();
    verificationSection.style.display = 'block';

  } catch (error) {
    loginMessage.textContent = 'Start the Java server, then try again.';
  }
});

verifyButton.addEventListener('click', async () => {
  const email = document.getElementById('email').value.trim();
  const code = document.getElementById('verification-code').value.trim();

  verificationMessage.textContent = 'Checking code...';

  try {
    const response = await fetch('/api/verify-code', {
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

// ===== Registration flow =====

let regEmail = '';
let regUserId = null;

document.getElementById('register-step1-btn').addEventListener('click', async () => {
  const email = document.getElementById('reg-email').value.trim();
  const password = document.getElementById('reg-password').value;
  const msg = document.getElementById('register-step1-message');

  if (!email || !password) {
    msg.textContent = 'Please fill in both fields.';
    return;
  }

  msg.textContent = 'Creating account...';

  try {
    const response = await fetch('/api/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });

    const result = await response.json();

    if (!response.ok || !result.success) {
      msg.textContent = 'Could not create account. Email may already be in use.';
      return;
    }

    regEmail = email;
    regUserId = result.userId;

    hideAllSections();
    registerStep2Section.style.display = 'block';
  } catch (error) {
    msg.textContent = 'Server error. Please try again.';
  }
});

document.getElementById('register-step2-btn').addEventListener('click', async () => {
  const username = document.getElementById('reg-username').value.trim();
  const displayName = document.getElementById('reg-display-name').value.trim();
  const msg = document.getElementById('register-step2-message');

  if (!username || !displayName) {
    msg.textContent = 'Please fill in both fields.';
    return;
  }

  msg.textContent = 'Saving profile...';

  try {
    const response = await fetch('/api/complete-profile', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ userId: regUserId, username, displayName }),
    });

    const result = await response.json();

    if (!response.ok || !result.success) {
      msg.textContent = 'Could not save profile.';
      return;
    }

    hideAllSections();
    registerStep3Section.style.display = 'block';
  } catch (error) {
    msg.textContent = 'Server error. Please try again.';
  }
});

async function saveRegIntegration(provider, field1, field2) {
  await fetch('/api/integrations/save', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: regEmail, provider, field1, field2 }),
  });
  const msg = document.getElementById('register-step3-message');
  msg.textContent = 'Saved!';
  setTimeout(() => { msg.textContent = ''; }, 2000);
}

document.getElementById('reg-canvas-connect-btn').addEventListener('click', async () => {
  const domain = document.getElementById('reg-canvas-domain').value.trim();
  const token = document.getElementById('reg-canvas-token').value.trim();
  if (!domain || !token) {
    alert('Please fill in both Canvas fields.');
    return;
  }
  await saveRegIntegration('canvas', domain, token);
  document.getElementById('reg-canvas-connect-btn').textContent = 'Connected \u2713';
});

document.getElementById('reg-schoology-connect-btn').addEventListener('click', async () => {
  const key = document.getElementById('reg-schoology-key').value.trim();
  const secret = document.getElementById('reg-schoology-secret').value.trim();
  if (!key || !secret) {
    alert('Please fill in both Schoology fields.');
    return;
  }
  await saveRegIntegration('schoology', key, secret);
  document.getElementById('reg-schoology-connect-btn').textContent = 'Connected \u2713';
});

document.getElementById('reg-google-connect-btn').addEventListener('click', () => {
  alert('Google Calendar connection requires OAuth setup - not available yet.');
});

document.getElementById('register-finish-btn').addEventListener('click', () => {
  hideAllSections();
  loginSection.style.display = 'block';
  document.getElementById('email').value = regEmail;
  loginMessage.textContent = 'Account created! Please sign in.';
});
document.getElementById('verification-code').addEventListener('keydown', (e) => {
  if (e.key === 'Enter') {
    verifyButton.click();
  }
});
