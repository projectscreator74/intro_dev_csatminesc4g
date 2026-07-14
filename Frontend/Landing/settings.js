const nameInput = document.getElementById('display-name');
const notifToggle = document.getElementById('notif-toggle');
const saveMsg = document.getElementById('save-msg');

async function loadSettingsFromServer() {
  const response = await fetch('/api/settings/get', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: USER_EMAIL }),
  });
  return await response.json();
}

async function init() {
  const settings = await loadSettingsFromServer();
  nameInput.value = settings.displayName || '';
  notifToggle.checked = settings.notifications;
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

init();