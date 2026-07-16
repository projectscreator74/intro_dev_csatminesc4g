const USER_EMAIL = localStorage.getItem('studystackUserEmail');

let currentGoals = [];
let currentFiles = [];
let editingGoalId = null;
let editingFileId = null;

async function loadGoals() {
  const response = await fetch('/api/goals/list', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: USER_EMAIL }),
  });
  return await response.json();
}

async function loadFiles() {
  const response = await fetch('/api/files/list', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: USER_EMAIL }),
  });
  return await response.json();
}

async function renderGoals() {
  currentGoals = await loadGoals();
  const list = document.getElementById("goalList");
  list.innerHTML = "";

  currentGoals.forEach(g => {
    list.innerHTML += `
      <div class="item-card">
        <h3>${g.title}</h3>
        <p>Due Date: ${g.date}</p>
        <p>Notes: ${g.notes}</p>
        <p>Status: ${g.status}</p>
        <div class="item-card-actions">
          <span class="grade-tag graded" onclick="editGoal(${g.id})">Edit</span>
          <span class="grade-tag graded" onclick="completeGoal(${g.id})">Complete</span>
          <button class="remove-btn" onclick="deleteGoal(${g.id})">&times;</button>
        </div>
      </div>
    `;
  });
}

async function renderDocuments() {
  currentFiles = await loadFiles();
  const list = document.getElementById("documentList");
  list.innerHTML = "";

  currentFiles.forEach(f => {
    list.innerHTML += `
      <div class="item-card">
        <h3>${f.name}</h3>
        <p>Type: ${f.type}</p>
        <p>Category: ${f.category}</p>
        <div class="item-card-actions">
          <span class="grade-tag graded" onclick="alert('Opening file...')">Open</span>
          <span class="grade-tag graded" onclick="editDocument(${f.id})">Rename</span>
          <button class="remove-btn" onclick="deleteDocument(${f.id})">&times;</button>
        </div>
      </div>
    `;
  });
}

function showGoalForm() {
  editingGoalId = null;
  document.getElementById("goalTitle").value = "";
  document.getElementById("goalDate").value = "";
  document.getElementById("goalNotes").value = "";
  document.getElementById("goalStatus").value = "Not Started";
  document.getElementById("goalForm").style.display = "block";
}

function hideGoalForm() {
  document.getElementById("goalForm").style.display = "none";
}

async function saveGoal() {
  const title = document.getElementById("goalTitle").value;
  const due = document.getElementById("goalDate").value;
  const notes = document.getElementById("goalNotes").value;
  const status = document.getElementById("goalStatus").value;

  if (editingGoalId === null) {
    await fetch('/api/goals/add', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: USER_EMAIL, title, due, notes, status }),
    });
  } else {
    await fetch('/api/goals/update', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: USER_EMAIL, goalId: editingGoalId, title, due, notes, status }),
    });
  }

  hideGoalForm();
  renderGoals();
}

function editGoal(id) {
  const goal = currentGoals.find(g => g.id === id);
  if (!goal) return;
  editingGoalId = id;
  document.getElementById("goalTitle").value = goal.title;
  document.getElementById("goalDate").value = goal.date;
  document.getElementById("goalNotes").value = goal.notes;
  document.getElementById("goalStatus").value = goal.status;
  document.getElementById("goalForm").style.display = "block";
}

async function completeGoal(id) {
  await fetch('/api/goals/complete', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: USER_EMAIL, goalId: id }),
  });
  renderGoals();
}

async function deleteGoal(id) {
  if (!confirm("Delete this goal?")) return;
  await fetch('/api/goals/remove', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: USER_EMAIL, goalId: id }),
  });
  renderGoals();
}

function showDocumentForm() {
  editingFileId = null;
  document.getElementById("documentName").value = "";
  document.getElementById("documentType").value = "";
  document.getElementById("documentCategory").value = "Resume";
  document.getElementById("documentForm").style.display = "block";
}

function hideDocumentForm() {
  document.getElementById("documentForm").style.display = "none";
}

async function saveDocument() {
  const name = document.getElementById("documentName").value;
  const type = document.getElementById("documentType").value;
  const category = document.getElementById("documentCategory").value;

  if (editingFileId === null) {
    await fetch('/api/files/add', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: USER_EMAIL, name, type, category }),
    });
  } else {
    await fetch('/api/files/rename', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: USER_EMAIL, fileId: editingFileId, name }),
    });
  }

  hideDocumentForm();
  renderDocuments();
}

function editDocument(id) {
  const file = currentFiles.find(f => f.id === id);
  if (!file) return;
  editingFileId = id;
  document.getElementById("documentName").value = file.name;
  document.getElementById("documentType").value = file.type;
  document.getElementById("documentCategory").value = file.category;
  document.getElementById("documentForm").style.display = "block";
}

async function deleteDocument(id) {
  if (!confirm("Delete this document?")) return;
  await fetch('/api/files/remove', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: USER_EMAIL, fileId: id }),
  });
  renderDocuments();
}

renderGoals();
renderDocuments();