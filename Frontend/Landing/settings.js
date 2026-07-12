const SETTINGS_KEY = 'studystack-settings';

function loadSettings() {
  const raw = localStorage.getItem(SETTINGS_KEY);
  return raw ? JSON.parse(raw) : { displayName: 'Student', notifications: true };
}

function saveSettings(settings) {
  localStorage.setItem(SETTINGS_KEY, JSON.stringify(settings));
}

const settings = loadSettings();
const nameInput = document.getElementById('display-name');
const notifToggle = document.getElementById('notif-toggle');
const saveMsg = document.getElementById('save-msg');

nameInput.value = settings.displayName;
notifToggle.checked = settings.notifications;

document.getElementById('save-settings-btn').addEventListener('click', () => {
  saveSettings({ displayName: nameInput.value, notifications: notifToggle.checked });
  saveMsg.textContent = "Saved!";
  setTimeout(() => { saveMsg.textContent = ""; }, 2000);
});

document.getElementById('reset-data-btn').addEventListener('click', () => {
  if (confirm("This will erase any classes/assignments you've added and restore the sample data. Continue?")) {
    resetToSampleData();
    alert("Sample data restored.");
  }
});