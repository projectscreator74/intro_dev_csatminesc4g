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

function getGradeBenchmark() {
  const stored = localStorage.getItem('studystack-grade-benchmark');
  return stored ? Number(stored) : null;
}

const monthTitle = document.getElementById('month-title');
const daysContainer = document.getElementById('calendar-days');
const prevBtn = document.getElementById('prev-btn');
const nextBtn = document.getElementById('next-btn');
const todayBtn = document.getElementById('today-btn');

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
  closeDayPanel();

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
        closeDayPanel();
        return;
      }
      if (selectedCell) {
        selectedCell.classList.remove('selected');
      }
      cell.classList.add('selected');
      selectedCell = cell;
      openDayPanel(year, month, day);
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

todayBtn.addEventListener('click', () => {
  currentYear = today.getFullYear();
  currentMonth = today.getMonth();
  buildCalendar(currentYear, currentMonth);
});

function getAssignmentsForDate(classes, year, month, day) {
  const matches = [];
  classes.forEach(cls => {
    cls.assignments.forEach(a => {
      if (!a.due) return;
      const parsed = parseDueDate(a.due, year);
      if (parsed.getFullYear() === year && parsed.getMonth() === month && parsed.getDate() === day) {
        matches.push({ ...a, className: cls.name });
      }
    });
  });
  return matches;
}

let lastOpenedDate = null;

async function openDayPanel(year, month, day) {
  lastOpenedDate = { year, month, day };

  const dayPanel = document.getElementById('day-panel');
  const dayBackdrop = document.getElementById('day-backdrop');
  const dateLabel = document.getElementById('day-panel-date');
  const list = document.getElementById('day-panel-list');

  const dateObj = new Date(year, month, day);
  dateLabel.textContent = dateObj.toLocaleDateString(undefined, {
    weekday: 'long', month: 'long', day: 'numeric', year: 'numeric',
  });

  list.innerHTML = "<li class='due-date'>Loading...</li>";
  dayPanel.classList.add('open');
  dayBackdrop.classList.add('visible');

  try {
    const classes = await loadClasses();
    const dueToday = getAssignmentsForDate(classes, year, month, day);
    const benchmark = getGradeBenchmark();

    list.innerHTML = "";

    if (dueToday.length === 0) {
      list.innerHTML = "<li class='due-date'>Nothing due on this date.</li>";
      return;
    }

    dueToday.forEach(a => {
      const item = document.createElement('li');
      item.className = 'assignment-row';

      const isGraded = a.grade !== null && a.grade !== undefined;
      const isLow = isGraded && benchmark !== null && a.grade < benchmark;

      item.innerHTML = `
        <div class="assignment-meta">
          <span class="assignment-name">${a.title}</span>
          <span class="due-date">${a.className}</span>
        </div>
        <span class="grade-tag ${isGraded ? 'graded' : 'ungraded'} ${isLow ? 'low' : ''}">${gradeLabel(a.grade)}</span>
      `;

      list.appendChild(item);
    });
  } catch (err) {
    console.error('Failed to load assignments for this date:', err);
    list.innerHTML = "<li class='due-date'>Couldn't load assignments. Check the console for details.</li>";
  }
}

function closeDayPanel() {
  document.getElementById('day-panel').classList.remove('open');
  document.getElementById('day-backdrop').classList.remove('visible');
}

document.getElementById('day-panel-close').addEventListener('click', () => {
  closeDayPanel();
  if (selectedCell) {
    selectedCell.classList.remove('selected');
    selectedCell = null;
  }
});

document.getElementById('day-backdrop').addEventListener('click', () => {
  closeDayPanel();
  if (selectedCell) {
    selectedCell.classList.remove('selected');
    selectedCell = null;
  }
});

const benchmarkInput = document.getElementById('grade-benchmark');
benchmarkInput.value = getGradeBenchmark() ?? '';

benchmarkInput.addEventListener('change', () => {
  const val = benchmarkInput.value.trim();
  if (val === '') {
    localStorage.removeItem('studystack-grade-benchmark');
  } else {
    localStorage.setItem('studystack-grade-benchmark', val);
  }

  const msg = document.getElementById('benchmark-saved-msg');
  msg.textContent = 'Saved!';
  setTimeout(() => { msg.textContent = ''; }, 1500);

  if (lastOpenedDate) {
    openDayPanel(lastOpenedDate.year, lastOpenedDate.month, lastOpenedDate.day);
  }
  loadUpcomingDeadlines(); 
});

function isTodayOrLater(dueDate) {
  const startOfToday = new Date(today.getFullYear(), today.getMonth(), today.getDate());
  return dueDate >= startOfToday;
}

async function loadUpcomingDeadlines() {
  const list = document.getElementById('upcoming-list');

  try {
    const classes = await loadClasses();
    const benchmark = getGradeBenchmark();

    const all = [];
    classes.forEach(cls => {
      cls.assignments.forEach(a => {
        if (a.completed) return; 
        if (!a.due) return;
        all.push({ ...a, className: cls.name, parsedDue: parseDueDate(a.due) });
      });
    });

    const upcoming = all
      .filter(a => isTodayOrLater(a.parsedDue))
      .sort((a, b) => a.parsedDue - b.parsedDue)
      .slice(0, 5);

    list.innerHTML = "";

    if (upcoming.length === 0) {
      list.innerHTML = "<li class='due-date'>Nothing upcoming. You're all caught up!</li>";
      return;
    }

    upcoming.forEach(a => {
      const isGraded = a.grade !== null && a.grade !== undefined;
      const isLow = isGraded && benchmark !== null && a.grade < benchmark;

      const row = document.createElement('li');
      row.className = 'assignment-row';
      row.innerHTML = `
        <div class="assignment-meta">
          <span class="assignment-name">${a.title}</span>
          <span class="due-date">${a.className} · Due ${a.due}</span>
        </div>
        <span class="grade-tag ${isGraded ? 'graded' : 'ungraded'} ${isLow ? 'low' : ''}">${gradeLabel(a.grade)}</span>
      `;
      list.appendChild(row);
    });
  } catch (err) {
    console.error('Failed to load upcoming deadlines:', err);
    list.innerHTML = "<li class='due-date'>Couldn't load upcoming deadlines.</li>";
  }
}

loadUpcomingDeadlines();
buildCalendar(currentYear, currentMonth);