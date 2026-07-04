// Class data - in a real app this would come from a shared source/backend.
// Note: no "average" field here anymore - it's calculated live from assignments
// using calculateAverage() from utils.js, so it always reflects current grades.
const classes = {
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

// Get class id from the URL (e.g. class-detail.html?id=calc-bc)
const params = new URLSearchParams(window.location.search);
const classId = params.get('id') || Object.keys(classes)[0];
const cls = classes[classId];

// calculateAverage and formatGradePercent come from utils.js (loaded before this script)
const average = calculateAverage(cls.assignments);

document.getElementById('class-name').textContent = cls.name;
document.getElementById('class-period').textContent = cls.period;
document.getElementById('class-average').textContent = formatGradePercent(average);

const list = document.getElementById('assignment-list');

cls.assignments.forEach(a => {
  const item = document.createElement('li');
  item.className = 'assignment-item';

  const gradeLabel = formatGradePercent(a.grade);
  const gradeClass = (a.grade !== null && a.grade !== undefined) ? 'graded' : 'ungraded';

  item.innerHTML = `
    <div class="assignment-info">
      <span class="assignment-title">${a.title}</span>
      <span class="assignment-due">Due ${a.due}</span>
    </div>
    <span class="assignment-grade ${gradeClass}">${gradeLabel}</span>
  `;

  list.appendChild(item);
});

// Placeholder for future "add assignment" functionality
document.getElementById('add-assignment-btn').addEventListener('click', () => {
  alert('Add Assignment form goes here — build in a future task.');
});