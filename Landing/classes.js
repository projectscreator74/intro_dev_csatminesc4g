// Sample class data - replace with real data later
const classes = [
  { id: "calc-bc", name: "AP Calculus BC", period: "Period 1", average: 94 },
  { id: "french", name: "AP French", period: "Period 2", average: 88 },
  { id: "cs-a", name: "AP Computer Science A", period: "Period 3", average: 97 },
  { id: "world-history", name: "AP World History", period: "Period 4", average: 90 },
  { id: "chem", name: "Honors Chemistry", period: "Period 5", average: 85 },
];

const classGrid = document.getElementById('class-grid');

classes.forEach(cls => {
  const card = document.createElement('a');
  card.href = `class-detail.html?id=${cls.id}`;
  card.className = 'class-card';

  card.innerHTML = `
    <div class="class-card-top">
      <h3>${cls.name}</h3>
      <span class="grade-pill">${cls.average}%</span>
    </div>
    <p class="class-period">${cls.period}</p>
  `;

  classGrid.appendChild(card);
});