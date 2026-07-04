// Sidebar toggle
const sidebar = document.getElementById('sidebar');
const overlay = document.getElementById('overlay');
const toggleBtn = document.getElementById('sidebar-toggle');
const arrow = toggleBtn.querySelector('.arrow');

function openSidebar() {
  sidebar.classList.add('open');
  overlay.classList.add('visible');
  arrow.classList.add('open');
}

function closeSidebar() {
  sidebar.classList.remove('open');
  overlay.classList.remove('visible');
  arrow.classList.remove('open');
}

toggleBtn.addEventListener('click', () => {
  if (sidebar.classList.contains('open')) {
    closeSidebar();
  } else {
    openSidebar();
  }
});

overlay.addEventListener('click', closeSidebar);

// Calendar (isToday and getDaysInMonth come from utils.js, loaded before this script)
const monthLabel = document.getElementById('calendar-month');
const calendarGrid = document.getElementById('calendar-grid');
const prevBtn = document.getElementById('prev-month');
const nextBtn = document.getElementById('next-month');

const monthNames = [
  "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December"
];

const today = new Date();
let viewYear = today.getFullYear();
let viewMonth = today.getMonth();

function renderCalendar(year, month) {
  monthLabel.textContent = `${monthNames[month]} ${year}`;
  calendarGrid.innerHTML = "";

  const dayLabels = ["S", "M", "T", "W", "T", "F", "S"];
  dayLabels.forEach(label => {
    const labelEl = document.createElement('div');
    labelEl.className = 'day-label';
    labelEl.textContent = label;
    calendarGrid.appendChild(labelEl);
  });

  const firstDay = new Date(year, month, 1).getDay();
  const daysInMonth = getDaysInMonth(year, month);

  for (let i = 0; i < firstDay; i++) {
    const emptyEl = document.createElement('div');
    emptyEl.className = 'day-cell empty';
    calendarGrid.appendChild(emptyEl);
  }

  for (let day = 1; day <= daysInMonth; day++) {
    const dayEl = document.createElement('div');
    dayEl.className = 'day-cell';
    dayEl.textContent = day;

    if (isToday(day, month, year, today)) {
      dayEl.classList.add('today');
    }

    calendarGrid.appendChild(dayEl);
  }
}

prevBtn.addEventListener('click', () => {
  viewMonth--;
  if (viewMonth < 0) {
    viewMonth = 11;
    viewYear--;
  }
  renderCalendar(viewYear, viewMonth);
});

nextBtn.addEventListener('click', () => {
  viewMonth++;
  if (viewMonth > 11) {
    viewMonth = 0;
    viewYear++;
  }
  renderCalendar(viewYear, viewMonth);
});

renderCalendar(viewYear, viewMonth);