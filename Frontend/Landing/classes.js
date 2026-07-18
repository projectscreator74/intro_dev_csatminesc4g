const grid = document.getElementById('classes-grid');

function getGradeBenchmark() {
  const stored = localStorage.getItem('studystack-grade-benchmark');
  return stored ? Number(stored) : null;
}

async function renderClasses() {
  const classes = await loadClasses();
  const benchmark = getGradeBenchmark();
  grid.innerHTML = "";

  classes.forEach(cls => {
    const avg = getClassAverage(cls.assignments);
    const isLow = avg !== null && benchmark !== null && avg < benchmark;

    const title = document.createElement('a');
    title.href = `class-detail.html?id=${cls.id}`;
    title.className = 'class-title';

    title.innerHTML = `
      <div class="class-title-top">
        <h3>${cls.name}</h3>
        <div class="class-title-actions">
          <span class="grade-badge ${isLow ? 'low' : ''}">${gradeLabel(avg)}</span>
          <button class="remove-btn" data-id="${cls.id}" title="Remove Class">&times;</button>
        </div>
      </div>
      <p class="class-time">${cls.period}</p>
    `;

    grid.appendChild(title);
  });

  grid.querySelectorAll('.remove-btn').forEach(btn => {
    btn.addEventListener('click', async (e) => {
      e.preventDefault();
      e.stopPropagation();
      const cls = await getClassById(btn.dataset.id);
      if (confirm(`Remove ${cls.name}? This will also delete its assignments.`)) {
        await removeClass(btn.dataset.id);
        renderClasses();
      }
    });
  });
}

document.getElementById('add-class-btn').addEventListener('click', async () => {
  const name = prompt("Class name:");
  if (!name) return;
  const period = prompt("Period (e.g. Period 6):") || "";
  await addClass(name, period);
  renderClasses();
});

renderClasses();