// run with: node --test utils.test.js
const test = require('node:test');
const assert = require('node:assert');
const { isSameDate, daysInMonth, getClassAverage, gradeLabel, parseDueDate } = require('./utils');

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

test('parseDueDate parses a short date string correctly', () => {
  const result = parseDueDate("Jun 20", 2026);
  assert.strictEqual(result.getMonth(), 5); // June = index 5
  assert.strictEqual(result.getDate(), 20);
});

test('parseDueDate sorts earlier dates before later ones', () => {
  const a = parseDueDate("Jun 15", 2026);
  const b = parseDueDate("Jul 2", 2026);
  assert.ok(a < b);
});

test('parseDueDate never throws, even on garbage input with no date in it', () => {
  const result = parseDueDate("completely invalid text with no numbers at all", 2026);
  assert.ok(result instanceof Date);
  assert.ok(!isNaN(result.getTime()));
});