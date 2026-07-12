const classSelect = document.getElementById('class-select');
const list = document.getElementById('all-assignments-list');

function renderAssignments() {
  const classes = loadClasses();

  classSelect.innerHTML = classes.length
    ? classes.map(c => `<option value="${c.id}">${c.name}</option>`).join('')
    : `<option value="">Add a class first</option>`;

  const all = [];
  classes.forEach(cls => {
    cls.assignments.forEach(a => {
      all.push({ ...a, className: cls.name, classId: cls.id });
    });
  });

  all.sort((a, b) => parseDueDate(a.due) - parseDueDate(b.due));

  list.innerHTML = "";

  if (all.length === 0) {
    list.innerHTML = "<li class='due-date'>No assignments yet.</li>";
    return;
  }

  all.forEach(a => {
    const row = document.createElement('li');
    row.className = 'assignment-row';
    const isGraded = a.grade !== null && a.grade !== undefined;

    row.innerHTML = `
      <div class="assignment-left">
        <input type="checkbox" class="assignment-check" data-class="${a.classId}" data-id="${a.id}" ${a.completed ? 'checked' : ''}>
        <div class="assignment-meta">
          <span class="assignment-name ${a.completed ? 'completed' : ''}">${a.title}</span>
          <span class="due-date">${a.className} · Due ${a.due}</span>
        </div>
      </div>
      <span class="grade-tag ${isGraded ? 'graded' : 'ungraded'}" data-class="${a.classId}" data-id="${a.id}">${gradeLabel(a.grade)}</span>
      <button class="remove-btn" data-class="${a.classId}" data-id="${a.id}" title="Remove assignment">&times;</button>
    `;

    list.appendChild(row);
  });

  list.querySelectorAll('.assignment-check').forEach(box => {
    box.addEventListener('change', () => {
      toggleAssignmentComplete(box.dataset.class, box.dataset.id);
      renderAssignments();
    });
  });

  list.querySelectorAll('.grade-tag').forEach(tag => {
    tag.addEventListener('click', () => {
      const newGrade = prompt("Enter grade (0-100):");
      if (newGrade === null || newGrade === "") return;
      setAssignmentGrade(tag.dataset.class, tag.dataset.id, Number(newGrade));
      renderAssignments();
    });
  });

  list.querySelectorAll('.remove-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      if (confirm("Remove this assignment?")) {
        removeAssignment(btn.dataset.class, btn.dataset.id);
        renderAssignments();
      }
    });
  });
}

document.getElementById('add-assignment-btn').addEventListener('click', () => {
  const classId = classSelect.value;
  if (!classId) {
    alert("Add a class first from the Classes page.");
    return;
  }
  const title = prompt("Assignment title:");
  if (!title) return;
  const due = prompt("Due date (e.g. Jul 10):") || "";
  addAssignment(classId, title, due);
  renderAssignments();
});

renderAssignments();