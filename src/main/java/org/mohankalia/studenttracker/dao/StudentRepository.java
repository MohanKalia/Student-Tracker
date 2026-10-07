package org.mohankalia.studenttracker.dao;

import org.mohankalia.studenttracker.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Integer> {
}
