const params = new URLSearchParams(window.location.search);
const classId = params.get('id');

const list = document.getElementById('assignments-list');

function getGradeBenchmark() {
  const stored = localStorage.getItem('studystack-grade-benchmark');
  return stored ? Number(stored) : null;
}

async function renderClass() {
  const cls = await getClassById(classId);
  if (!cls) {
    document.querySelector('.page-content').innerHTML = "<p>Class not found. <a href='classes.html'>Back to Classes</a></p>";
    return;
  }

  const benchmark = getGradeBenchmark();
  const avg = getClassAverage(cls.assignments);
  const avgIsLow = avg !== null && benchmark !== null && avg < benchmark;

  document.getElementById('class-title').textContent = cls.name;
  document.getElementById('time-label').textContent = cls.period;

  const gradeValueEl = document.getElementById('grade-value');
  gradeValueEl.textContent = gradeLabel(avg);
  gradeValueEl.classList.toggle('low', avgIsLow);

  list.innerHTML = "";
  cls.assignments.forEach(a => {
    const row = document.createElement('li');
    row.className = 'assignment-row';
    const isGraded = a.grade !== null && a.grade !== undefined;
    const isLow = isGraded && benchmark !== null && a.grade < benchmark;

    row.innerHTML = `
      <div class="assignment-left">
        <input type="checkbox" class="assignment-check" data-id="${a.id}" ${a.completed ? 'checked' : ''}>
        <div class="assignment-meta">
          <span class="assignment-name ${a.completed ? 'completed' : ''}">${a.title}</span>
          <span class="due-date">Due ${a.due}</span>
        </div>
      </div>
      <span class="grade-tag ${isGraded ? 'graded' : 'ungraded'} ${isLow ? 'low' : ''}" data-id="${a.id}">${gradeLabel(a.grade)}</span>
      <button class="remove-btn" data-id="${a.id}" title="Remove assignment">&times;</button>
    `;

    list.appendChild(row);
  });

  list.querySelectorAll('.assignment-check').forEach(box => {
    box.addEventListener('change', async () => {
      await toggleAssignmentComplete(classId, box.dataset.id);
      renderClass();
    });
  });

  list.querySelectorAll('.grade-tag').forEach(tag => {
    tag.addEventListener('click', async () => {
      const newGrade = prompt("Enter grade (0-100):");
      if (newGrade === null || newGrade === "") return;
      await setAssignmentGrade(classId, tag.dataset.id, Number(newGrade));
      renderClass();
    });
  });

  list.querySelectorAll('.remove-btn').forEach(btn => {
    btn.addEventListener('click', async () => {
      if (confirm("Remove this assignment?")) {
        await removeAssignment(classId, btn.dataset.id);
        renderClass();
      }
    });
  });
}

document.getElementById('add-btn').addEventListener('click', async () => {
  const title = prompt("Assignment title:");
  if (!title) return;
  const due = prompt("Due date (e.g. Jul 10):") || "";
  await addAssignment(classId, title, due);
  renderClass();
});

renderClass();