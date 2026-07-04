function isSameDate(day, month, year, refDate = new Date()) {
  return day === refDate.getDate() && month === refDate.getMonth() && year === refDate.getFullYear();
}

function daysInMonth(year, month) {
  return new Date(year, month + 1, 0).getDate();
}

function getClassAverage(assignments) {
  const graded = assignments.filter(a => a.grade !== null && a.grade !== undefined);
  if (graded.length === 0) return null;
  const total = graded.reduce((sum, a) => sum + a.grade, 0);
  return Math.round(total / graded.length);
}

function gradeLabel(grade) {
  return grade === null || grade === undefined ? "Not graded" : `${grade}%`;
}

if (typeof module !== 'undefined' && module.exports) {
  module.exports = { isSameDate, daysInMonth, getClassAverage, gradeLabel };
}