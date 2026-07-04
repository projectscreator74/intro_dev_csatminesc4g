const test = require('node:test');
const assert = require('node:assert');
const { isSameDate, daysInMonth, getClassAverage, gradeLabel } = require('./utils');

test('isSameDate matches the reference date', () => {
  const ref = new Date(2026, 5, 30);
  assert.strictEqual(isSameDate(30, 5, 2026, ref), true);
});

test('isSameDate rejects a non-matching date', () => {
  const ref = new Date(2026, 5, 30);
  assert.strictEqual(isSameDate(15, 5, 2026, ref), false);
});

test('daysInMonth returns 30 for June', () => {
  assert.strictEqual(daysInMonth(2026, 5), 30);
});

test('daysInMonth returns 29 for Feb in a leap year', () => {
  assert.strictEqual(daysInMonth(2028, 1), 29);
});

test('getClassAverage skips ungraded assignments', () => {
  const assignments = [{ grade: 95 }, { grade: null }, { grade: 85 }];
  assert.strictEqual(getClassAverage(assignments), 90);
});

test('getClassAverage returns null with nothing graded yet', () => {
  const assignments = [{ grade: null }, { grade: null }];
  assert.strictEqual(getClassAverage(assignments), null);
});

test('gradeLabel formats a number as a percent', () => {
  assert.strictEqual(gradeLabel(92), "92%");
});

test('gradeLabel shows "Not graded" for null', () => {
  assert.strictEqual(gradeLabel(null), "Not graded");
});