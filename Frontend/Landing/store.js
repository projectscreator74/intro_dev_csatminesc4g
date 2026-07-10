const STORAGE_KEY = 'studystack-classes';

function seedClasses() {
  return [
    { id: 'calc-bc', name: 'AP Calculus BC', period: 'Period 1', assignments: [
      { id: 'a1', title: 'Unit 6 Free Response Practice', due: 'Jun 20', grade: 95, completed: true },
      { id: 'a2', title: 'Parametric Equations Quiz', due: 'Jun 25', grade: 92, completed: true },
      { id: 'a3', title: 'Volumes of Solids Homework', due: 'Jul 2', grade: null, completed: false },
    ]},
    { id: 'french', name: 'AP French', period: 'Period 2', assignments: [
      { id: 'a4', title: 'Listening Log Entry 4', due: 'Jun 18', grade: 90, completed: true },
      { id: 'a5', title: 'Moroccan Cuisine Slide Project', due: 'Jun 27', grade: 85, completed: true },
    ]},
    { id: 'cs-a', name: 'AP Computer Science A', period: 'Period 3', assignments: [
      { id: 'a6', title: 'Merge Sort Lab', due: 'Jun 15', grade: 98, completed: true },
      { id: 'a7', title: 'Sorting Algorithm Debugging', due: 'Jun 22', grade: 96, completed: true },
    ]},
    { id: 'world-history', name: 'AP World History', period: 'Period 4', assignments: [
      { id: 'a8', title: 'Map Activity: Trade Routes', due: 'Jun 19', grade: 88, completed: true },
      { id: 'a9', title: 'Unit 5 Study Packet', due: 'Jun 28', grade: null, completed: false },
    ]},
    { id: 'chem', name: 'Honors Chemistry', period: 'Period 5', assignments: [
      { id: 'a10', title: 'Titration Curve Lab Write-up', due: 'Jun 17', grade: 82, completed: true },
      { id: 'a11', title: 'ICE Table Practice Set', due: 'Jun 24', grade: 87, completed: true },
    ]},
  ];
}

function loadClasses() {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (!raw) {
    const seeded = seedClasses();
    saveClasses(seeded);
    return seeded;
  }
  return JSON.parse(raw);
}

function saveClasses(classes) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(classes));
}

function getClassById(classId) {
  return loadClasses().find(c => c.id === classId);
}

function addClass(name, period) {
  const classes = loadClasses();
  const id = name.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/(^-|-$)/g, '') + '-' + Date.now();
  classes.push({ id, name, period, assignments: [] });
  saveClasses(classes);
  return id;
}

function removeClass(classId) {
  const classes = loadClasses().filter(c => c.id !== classId);
  saveClasses(classes);
}

function addAssignment(classId, title, due) {
  const classes = loadClasses();
  const cls = classes.find(c => c.id === classId);
  if (!cls) return;
  cls.assignments.push({ id: 'a' + Date.now(), title, due, grade: null, completed: false });
  saveClasses(classes);
}

function removeAssignment(classId, assignmentId) {
  const classes = loadClasses();
  const cls = classes.find(c => c.id === classId);
  if (!cls) return;
  cls.assignments = cls.assignments.filter(a => a.id !== assignmentId);
  saveClasses(classes);
}

function setAssignmentGrade(classId, assignmentId, grade) {
  const classes = loadClasses();
  const cls = classes.find(c => c.id === classId);
  if (!cls) return;
  const a = cls.assignments.find(a => a.id === assignmentId);
  if (a) a.grade = grade;
  saveClasses(classes);
}

function toggleAssignmentComplete(classId, assignmentId) {
  const classes = loadClasses();
  const cls = classes.find(c => c.id === classId);
  if (!cls) return;
  const a = cls.assignments.find(a => a.id === assignmentId);
  if (a) a.completed = !a.completed;
  saveClasses(classes);
}

function resetToSampleData() {
  saveClasses(seedClasses());
}