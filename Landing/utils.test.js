// Test suite for utils.js
// Run with: node --test utils.test.js
// Uses Node's built-in test runner - no npm install needed.

const test = require('node:test');
const assert = require('node:assert');
const { isToday, getDaysInMonth, calculateAverage, formatGradePercent } = require('./utils');

test('isToday returns true when date matches the reference date', () => {
  const reference = new Date(2026, 5, 30); // June 30, 2026
  assert.strictEqual(isToday(30, 5, 2026, reference), true);
});

test('isToday returns false when date does not match the reference date', () => {
  const reference = new Date(2026, 5, 30);
  assert.strictEqual(isToday(15, 5, 2026, reference), false);
});

test('getDaysInMonth returns 30 for June', () => {
  assert.strictEqual(getDaysInMonth(2026, 5), 30);
});

test('getDaysInMonth returns 29 for February in a leap year', () => {
  assert.strictEqual(getDaysInMonth(2028, 1), 29); // 2028 is a leap year
});

test('calculateAverage averages only graded assignments, ignoring ungraded ones', () => {
  const assignments = [{ grade: 95 }, { grade: null }, { grade: 85 }];
  assert.strictEqual(calculateAverage(assignments), 90);
});

test('calculateAverage returns null when no assignments are graded yet', () => {
  const assignments = [{ grade: null }, { grade: null }];
  assert.strictEqual(calculateAverage(assignments), null);
});

test('formatGradePercent formats a number as a percent string', () => {
  assert.strictEqual(formatGradePercent(92), "92%");
});

test('formatGradePercent returns "Not graded" for null grades', () => {
  assert.strictEqual(formatGradePercent(null), "Not graded");
});