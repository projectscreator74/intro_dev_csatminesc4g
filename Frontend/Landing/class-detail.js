const classData = {
  "calc-bc": { name: "AP Calculus BC", period: "Period 1", assignments: [
    { title: "Unit 6 Free Response Practice", due: "Jun 20", grade: 95 },
    { title: "Parametric Equations Quiz", due: "Jun 25", grade: 92 },
    { title: "Volumes of Solids Homework", due: "Jul 2", grade: null },
  ]},
  "french": { name: "AP French", period: "Period 2", assignments: [
    { title: "Listening Log Entry 4", due: "Jun 18", grade: 90 },
    { title: "Moroccan Cuisine Slide Project", due: "Jun 27", grade: 85 },
  ]},
  "cs-a": { name: "AP Computer Science A", period: "Period 3", assignments: [
    { title: "Merge Sort Lab", due: "Jun 15", grade: 98 },
    { title: "Sorting Algorithm Debugging", due: "Jun 22", grade: 96 },
  ]},
  "world-history": { name: "AP World History", period: "Period 4", assignments: [
    { title: "Map Activity: Trade Routes", due: "Jun 19", grade: 88 },
    { title: "Unit 5 Study Packet", due: "Jun 28", grade: null },
  ]},
  "chem": { name: "Honors Chemistry", period: "Period 5", assignments: [
    { title: "Titration Curve Lab Write-up", due: "Jun 17", grade: 82 },
    { title: "ICE Table Practice Set", due: "Jun 24", grade: 87 },
  ]},
};

const params = new URLSearchParams(window.location.search);
const classId = params.get('id') || Object.keys(classData)[0];
const cls = classData[classId];

const avg = getClassAverage(cls.assignments);

document.getElementById('class-title').textContent = cls.name;
document.getElementById('time-label').textContent = cls.period;
document.getElementById('grade-value').textContent = gradeLabel(avg);

const list = document.getElementById('assignments-list');

cls.assignments.forEach(a => {
  const row = document.createElement('li');
  row.className = 'assignment-row';

  const isGraded = a.grade !== null && a.grade !== undefined;

  row.innerHTML = `
    <div class="assignment-meta">
      <span class="assignment-name">${a.title}</span>
      <span class="due-date">Due ${a.due}</span>
    </div>
    <span class="grade-tag ${isGraded ? 'graded' : 'ungraded'}">${gradeLabel(a.grade)}</span>
  `;

  list.appendChild(row);
});

document.getElementById('add-btn').addEventListener('click', () => {
  alert('Add Assignment form goes here — build in a future task.');
});