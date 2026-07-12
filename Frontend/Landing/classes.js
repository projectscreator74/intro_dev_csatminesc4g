const grid = document.getElementById('classes-grid');

function renderClasses() {
  const classes = loadClasses();
  grid.innerHTML = "";

  classes.forEach(cls => {
    const avg = getClassAverage(cls.assignments);

    const title = document.createElement('a');
    title.href = `class-detail.html?id=${cls.id}`;
    title.className = 'class-title';

    title.innerHTML = `
      <div class="class-title-top">
        <h3>${cls.name}</h3>
        <div class="class-title-actions">
          <span class="grade-badge">${gradeLabel(avg)}</span>
          <button class="remove-btn" data-id="${cls.id}" title="Remove Class">&times;</button>
        </div>
      </div>
      <p class="class-time">${cls.period}</p>
    `;

    grid.appendChild(title);
  });

  grid.querySelectorAll('.remove-btn').forEach(btn => {
    btn.addEventListener('click', (e) => {
      e.preventDefault();
      e.stopPropagation();
      const cls = getClassById(btn.dataset.id);
      if (confirm(`Remove ${cls.name}? This will also delete its assignments.`)) {
        removeClass(btn.dataset.id);
        renderClasses();
      }
    });
  });
}
 
document.getElementById('add-class-btn').addEventListener('click', () => {
  const name = prompt("Class name:");
  if (!name) return;
  const period = prompt("Period (e.g. Period 6):") || "";
  addClass(name, period);
  renderClasses();
});
 
renderClasses();