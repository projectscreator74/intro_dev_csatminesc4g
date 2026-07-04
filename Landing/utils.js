// Shared utility functions for StudyStack.
// Loaded as a <script> tag in the browser, and required directly in tests.

// Returns true if day/month/year matches the reference date (defaults to now)
function isToday(day, month, year, referenceDate = new Date()) {
  return (
    day === referenceDate.getDate() &&
    month === referenceDate.getMonth() &&
    year === referenceDate.getFullYear()
  );
}

// Returns the number of days in a given month (month is 0-indexed, like JS Date)
function getDaysInMonth(year, month) {
  return new Date(year, month + 1, 0).getDate();
}

// Calculates a class average from an array of assignments ({ grade: number|null }).
// Ungraded assignments (grade === null) are ignored. Returns null if none are graded.
function calculateAverage(assignments) {
  const graded = assignments.filter(a => a.grade !== null && a.grade !== undefined);
  if (graded.length === 0) return null;
  const total = graded.reduce((sum, a) => sum + a.grade, 0);
  return Math.round(total / graded.length);
}

// Formats a grade as a percent string, or "Not graded" if null/undefined
function formatGradePercent(grade) {
  return grade === null || grade === undefined ? "Not graded" : `${grade}%`;
}

// Makes these functions available to Node (for tests) without breaking browser <script> tags,
// where they simply become global functions instead.
if (typeof module !== 'undefined' && module.exports) {
  module.exports = { isToday, getDaysInMonth, calculateAverage, formatGradePercent };
}