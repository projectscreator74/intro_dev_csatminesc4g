const classList = [
  { id: "calc-bc", name: "AP Calculus BC", period: "Period 1", average: 94 },
  { id: "french", name: "AP French", period: "Period 2", average: 88 },
  { id: "cs-a", name: "AP Computer Science A", period: "Period 3", average: 97 },
  { id: "world-history", name: "AP World History", period: "Period 4", average: 90 },
  { id: "chem", name: "Honors Chemistry", period: "Period 5", average: 85 },
];

const grid = document.getElementById('classes-grid');

classList.forEach(cls => {
  const tile = document.createElement('a');
  tile.href = `class-detail.html?id=${cls.id}`;
  tile.className = 'class-tile';

  tile.innerHTML = `
    <div class="class-tile-top">
      <h3>${cls.name}</h3>
      <span class="grade-badge">${cls.average}%</span>
    </div>
    <p class="class-time">${cls.period}</p>
  `;

  grid.appendChild(tile);
});