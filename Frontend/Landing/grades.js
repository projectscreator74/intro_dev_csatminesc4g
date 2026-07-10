function renderGrades() {
  const classes = loadClasses();
  const list = document.getElementById('grades-list');
  list.innerHTML = "";

  if (classes.length === 0) {
    list.innerHTML = "<li class='due-date'>Add a class to see grades here.</li>";
    return;
  }

  classes.forEach(cls => {
    const avg = getClassAverage(cls.assignments);
    const row = document.createElement('li');
    row.className = 'assignment-row';

    row.innerHTML = `
      <div class="assignment-meta">
        <span class="assignment-name">${cls.name}</span>
        <span class="due-date">${cls.period}</span>
      </div>
      <span class="grade-badge">${gradeLabel(avg)}</span>
    `;

    list.appendChild(row);
  });
}

renderGrades();