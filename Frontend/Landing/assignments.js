const classSelect = document.getElementById('class-select');
const list = document.getElementById('all-assignments-list');

function getGradeBenchmark() {
  const stored = localStorage.getItem('studystack-grade-benchmark');
  return stored ? Number(stored) : null;
}

async function renderAssignments() {
  const previousSelection = classSelect.value;
  const classes = await loadClasses();
  const benchmark = getGradeBenchmark();

  const options = classes.map(c => `<option value="${c.id}">${c.name}</option>`).join('');
  classSelect.innerHTML = classes.length
    ? `<option value="">All Classes</option>` + options
    : `<option value="">Add a class first</option>`;

  if ([...classSelect.options].some(opt => opt.value === previousSelection)) {
    classSelect.value = previousSelection;
  }

  const selectedClassId = classSelect.value;

  const all = [];
  classes.forEach(cls => {
    if (selectedClassId && String(cls.id) !== selectedClassId) return;
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
    const isLow = isGraded && benchmark !== null && a.grade < benchmark;

    row.innerHTML = `
      <div class="assignment-left">
        <input type="checkbox" class="assignment-check" data-class="${a.classId}" data-id="${a.id}" ${a.completed ? 'checked' : ''}>
        <div class="assignment-meta">
          <span class="assignment-name ${a.completed ? 'completed' : ''}">${a.title}</span>
          <span class="due-date">${a.className} · Due ${a.due}</span>
        </div>
      </div>
      <span class="grade-tag ${isGraded ? 'graded' : 'ungraded'} ${isLow ? 'low' : ''}" data-class="${a.classId}" data-id="${a.id}">${gradeLabel(a.grade)}</span>
      <button class="remove-btn" data-class="${a.classId}" data-id="${a.id}" title="Remove assignment">&times;</button>
    `;

    list.appendChild(row);
  });

  list.querySelectorAll('.assignment-check').forEach(box => {
    box.addEventListener('change', async () => {
      await toggleAssignmentComplete(box.dataset.class, box.dataset.id);
      renderAssignments();
    });
  });

  list.querySelectorAll('.grade-tag').forEach(tag => {
    tag.addEventListener('click', async () => {
      const newGrade = prompt("Enter grade (0-100):");
      if (newGrade === null || newGrade === "") return;
      await setAssignmentGrade(tag.dataset.class, tag.dataset.id, Number(newGrade));
      renderAssignments();
    });
  });

  list.querySelectorAll('.remove-btn').forEach(btn => {
    btn.addEventListener('click', async () => {
      if (confirm("Remove this assignment?")) {
        await removeAssignment(btn.dataset.class, btn.dataset.id);
        renderAssignments();
      }
    });
  });
}

classSelect.addEventListener('change', () => {
  renderAssignments();
});

document.getElementById('add-assignment-btn').addEventListener('click', async () => {
  const classId = classSelect.value;
  if (!classId) {
    alert("Select a specific class first (not \"All Classes\") to add an assignment.");
    return;
  }
  const title = prompt("Assignment title:");
  if (!title) return;
  const due = prompt("Due date (e.g. Jul 10):") || "";
  await addAssignment(classId, title, due);
  renderAssignments();
});

renderAssignments();