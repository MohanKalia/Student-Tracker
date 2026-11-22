import test from 'node:test';
import assert from 'node:assert/strict';
import { distinctValues, filterStudents, toCsv } from '../../main/resources/static/students.mjs';

const students = [
  { id: 3, firstName: 'Zoe', lastName: 'Rossi', email: 'zoe@example.com', university: 'Bologna', studyLevel: 'Master' },
  { id: 1, firstName: 'Ada', lastName: 'Lovelace', email: 'ada@example.com', university: 'Oxford', studyLevel: 'Bachelor' },
  { id: 2, firstName: 'Mohan', lastName: 'Kalia', email: 'mohan@example.com', university: 'Bologna', studyLevel: 'Bachelor' }
];
test('search matches full names, emails, university and student ID without case sensitivity', () => {
  for (const search of ['  MOHAN KALIA ', 'mohan@', '2']) assert.deepEqual(filterStudents(students, { search }).map(s => s.id), [2]);
  assert.deepEqual(filterStudents(students, { search: 'bologna' }).map(s => s.id), [2, 3]);
});
test('university, level and search filters compose', () => {
  assert.deepEqual(filterStudents(students, { university: 'Bologna', studyLevel: 'Bachelor', search: 'Kalia' }).map(s => s.id), [2]);
  assert.deepEqual(filterStudents(students, { university: 'Oxford', studyLevel: 'Master' }), []);
});
test('sorting is reversible and preserves the original data', () => {
  assert.deepEqual(filterStudents(students).map(s => s.id), [1, 2, 3]);
  assert.deepEqual(filterStudents(students, { ascending: false }).map(s => s.id), [3, 2, 1]);
  assert.deepEqual(students.map(s => s.id), [3, 1, 2]);
});
test('options exclude missing values and duplicates', () => {
  assert.deepEqual(distinctValues([...students, { university: null }], 'university'), ['Bologna', 'Oxford']);
  assert.deepEqual(filterStudents([{ id: 5, firstName: 'Test', lastName: 'Student' }], { search: 'student' }).map(s => s.id), [5]);
});
test('CSV quotes commas, quotes, newlines and neutralizes spreadsheet formulas', () => {
  const csv = toCsv([{ id: 1, firstName: '=1+1', lastName: 'A,"B"', email: 'a@example.com', university: 'Rome\nItaly', studyLevel: null }]);
  assert.ok(csv.includes('"\'=1+1"'));
  assert.ok(csv.includes('"A,""B"""'));
  assert.ok(csv.includes('"Rome\nItaly"'));
  for (const value of ['+cmd', '-cmd', '@cmd', '\t=cmd', '  =cmd', '\ncmd']) {
    assert.ok(toCsv([{ firstName: value }]).includes(`"'${value}"`));
  }
  assert.equal(toCsv([]).split('\r\n').length, 1);
});
