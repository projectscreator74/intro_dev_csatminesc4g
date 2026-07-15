const USER_EMAIL = localStorage.getItem('studystackUserEmail');

async function loadWelcomeName() {
  const response = await fetch('/api/settings/get', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: USER_EMAIL }),
  });
  const settings = await response.json();
  const heading = document.getElementById('welcome-heading');
  if (heading && settings.displayName) {
    heading.textContent = `Welcome Back, ${settings.displayName}`;
  }
}

loadWelcomeName();

const monthTitle = document.getElementById('month-title');
const daysContainer = document.getElementById('calendar-days');
const prevBtn = document.getElementById('prev-btn');
const nextBtn = document.getElementById('next-btn');

const monthNames = [
  "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December"
];

const today = new Date();
let currentYear = today.getFullYear();
let currentMonth = today.getMonth();
let selectedCell = null;

function buildCalendar(year, month) {
  monthTitle.textContent = `${monthNames[month]} ${year}`;
  daysContainer.innerHTML = "";
  selectedCell = null;

  ["S", "M", "T", "W", "T", "F", "S"].forEach(label => {
    const nameEl = document.createElement('div');
    nameEl.className = 'day-name';
    nameEl.textContent = label;
    daysContainer.appendChild(nameEl);
  });

  const firstWeekday = new Date(year, month, 1).getDay();
  const totalDays = daysInMonth(year, month);

  for (let i = 0; i < firstWeekday; i++) {
    const blank = document.createElement('div');
    blank.className = 'day blank';
    daysContainer.appendChild(blank);
  }

  for (let day = 1; day <= totalDays; day++) {
    const cell = document.createElement('div');
    cell.className = 'day';
    cell.textContent = day;

    if (isSameDate(day, month, year, today)) {
      cell.classList.add('today');
    }

    cell.addEventListener('click', () => {
      if (selectedCell === cell) {
        cell.classList.remove('selected');
        selectedCell = null;
      } else {
        if (selectedCell) {
          selectedCell.classList.remove('selected');
        }
        cell.classList.add('selected');
        selectedCell = cell;
      }
    });

    daysContainer.appendChild(cell);
  }
}

prevBtn.addEventListener('click', () => {
  currentMonth--;
  if (currentMonth < 0) {
    currentMonth = 11;
    currentYear--;
  }
  buildCalendar(currentYear, currentMonth);
});

nextBtn.addEventListener('click', () => {
  currentMonth++;
  if (currentMonth > 11) {
    currentMonth = 0;
    currentYear++;
  }
  buildCalendar(currentYear, currentMonth);
});

buildCalendar(currentYear, currentMonth);