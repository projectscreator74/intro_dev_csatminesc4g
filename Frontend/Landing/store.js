const USER_EMAIL = localStorage.getItem('studystackUserEmail');

async function loadClasses() {
  const response = await fetch('/api/classes/list', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: USER_EMAIL }),
  });
  return await response.json();
}

async function getClassById(classId) {
  const classes = await loadClasses();
  return classes.find(c => c.id === Number(classId));
}

async function addClass(name, period) {
  const response = await fetch('/api/classes/add', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: USER_EMAIL, name, period }),
  });
  const result = await response.json();
  return result.classId;
}

async function removeClass(classId) {
  await fetch('/api/classes/remove', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: USER_EMAIL, classId: Number(classId) }),
  });
}

async function addAssignment(classId, title, due) {
  await fetch('/api/assignments/add', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: USER_EMAIL, classId: Number(classId), title, due }),
  });
}

async function removeAssignment(classId, assignmentId) {
  await fetch('/api/assignments/remove', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: USER_EMAIL, assignmentId: Number(assignmentId) }),
  });
}

async function setAssignmentGrade(classId, assignmentId, grade) {
  await fetch('/api/assignments/grade', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: USER_EMAIL, assignmentId: Number(assignmentId), grade }),
  });
}

async function toggleAssignmentComplete(classId, assignmentId) {
  await fetch('/api/assignments/complete', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: USER_EMAIL, assignmentId: Number(assignmentId) }),
  });
}