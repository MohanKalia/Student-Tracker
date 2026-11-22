export function distinctValues(students, field) {
  return [...new Set(students.map(student => student[field]).filter(Boolean))].sort((a, b) => a.localeCompare(b));
}

export function filterStudents(students, { search = '', university = '', studyLevel = '', ascending = true } = {}) {
  const query = search.trim().toLocaleLowerCase();
  return students.filter(student => (!university || student.university === university)
    && (!studyLevel || student.studyLevel === studyLevel)
    && (!query || [student.firstName, student.lastName, `${student.firstName} ${student.lastName}`, student.email, student.university, String(student.id)]
      .some(value => String(value ?? '').toLocaleLowerCase().includes(query))))
    .sort((a, b) => (ascending ? 1 : -1) * (`${a.firstName} ${a.lastName}`.localeCompare(`${b.firstName} ${b.lastName}`) || a.id - b.id));
}

export function toCsv(students) {
  const cell = value => {
    let text = String(value ?? '');
    // Prevent spreadsheet programs from interpreting untrusted fields as formulas.
    if (/^[\s\u0000-\u001f]*[=+@-]/.test(text) || /^[\t\r\n]/.test(text)) text = `'${text}`;
    return `"${text.replaceAll('"', '""')}"`;
  };
  return [['Student ID', 'First name', 'Last name', 'Email', 'University', 'Study level'],
    ...students.map(student => [student.id, student.firstName, student.lastName, student.email, student.university, student.studyLevel])]
    .map(row => row.map(cell).join(',')).join('\r\n');
}
