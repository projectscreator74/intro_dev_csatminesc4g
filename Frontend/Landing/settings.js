const nameInput = document.getElementById('display-name');
const notifToggle = document.getElementById('notif-toggle');
const saveMsg = document.getElementById('save-msg');
const integrationsMsg = document.getElementById('integrations-msg');

async function loadSettingsFromServer() {
  const response = await fetch('/api/settings/get', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: USER_EMAIL }),
  });
  return await response.json();
}

async function loadIntegrationStatus() {
  const response = await fetch('/api/integrations/status', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: USER_EMAIL }),
  });
  const status = await response.json();

  if (status.canvas) {
    document.getElementById('canvas-connect-btn').textContent = 'Connected \u2713';
  }
  if (status.schoology) {
    document.getElementById('schoology-connect-btn').textContent = 'Connected \u2713';
  }
  if (status.google) {
    document.getElementById('google-connect-btn').textContent = 'Connected \u2713';
  }
}

async function saveIntegration(provider, field1, field2, field3) {
  await fetch('/api/integrations/save', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: USER_EMAIL, provider, field1, field2, field3: field3 || '' }),
  });
  integrationsMsg.textContent = "Saved!";
  setTimeout(() => { integrationsMsg.textContent = ""; }, 2000);
}

async function init() {
  const settings = await loadSettingsFromServer();
  nameInput.value = settings.displayName || '';
  notifToggle.checked = settings.notifications;
  await loadIntegrationStatus();
}

document.getElementById('save-settings-btn').addEventListener('click', async () => {
  await fetch('/api/settings/save', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      email: USER_EMAIL,
      displayName: nameInput.value,
      notifications: notifToggle.checked,
    }),
  });
  saveMsg.textContent = "Saved!";
  setTimeout(() => { saveMsg.textContent = ""; }, 2000);
});

document.getElementById('canvas-connect-btn').addEventListener('click', async () => {
  const domain = document.getElementById('canvas-domain').value.trim();
  const token = document.getElementById('canvas-token').value.trim();
  if (!domain || !token) {
    alert('Please fill in both Canvas fields.');
    return;
  }
  await saveIntegration('canvas', domain, token, '');
  document.getElementById('canvas-connect-btn').textContent = 'Connected \u2713';
});

document.getElementById('schoology-connect-btn').addEventListener('click', async () => {
  const key = document.getElementById('schoology-key').value.trim();
  const secret = document.getElementById('schoology-secret').value.trim();
  const courseId = document.getElementById('schoology-course-id').value.trim();
  if (!key || !secret) {
    alert('Please fill in both Schoology fields.');
    return;
  }
  await saveIntegration('schoology', key, secret, courseId);
  document.getElementById('schoology-connect-btn').textContent = 'Connected \u2713';
});

document.getElementById('google-connect-btn').addEventListener('click', () => {
  alert('This feature is currently unavailable. Sorry!');
});

document.getElementById('canvas-sync-now-btn').addEventListener('click', async () => {
  const msg = document.getElementById('canvas-sync-msg');
  msg.textContent = 'Syncing...';
  const response = await fetch('/api/sync/canvas', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: USER_EMAIL }),
  });
  const result = await response.json();
  msg.textContent = result.message || (result.success ? 'Synced!' : 'Sync failed.');
});

document.getElementById('schoology-sync-now-btn').addEventListener('click', async () => {
  const msg = document.getElementById('schoology-sync-msg');
  msg.textContent = 'Syncing...';
  const response = await fetch('/api/sync/schoology', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: USER_EMAIL }),
  });
  const result = await response.json();
  msg.textContent = result.message || (result.success ? 'Synced!' : 'Sync failed.');
});

init();

document.getElementById('delete-account-btn').addEventListener('click', async () => {
  const confirmed = confirm('This will permanently delete your account and ALL your data. This cannot be undone. Are you sure?');
  if (!confirmed) return;

  const doubleConfirmed = confirm('Really sure? This is your last chance to cancel.');
  if (!doubleConfirmed) return;

  const response = await fetch('/api/account/delete', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: USER_EMAIL }),
  });

  const result = await response.json();

  if (result.success) {
    localStorage.removeItem('studystackUserEmail');
    alert('Account deleted.');
    window.location.href = 'login.html';
  } else {
    alert('Failed to delete account.');
  }
});