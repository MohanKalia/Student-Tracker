package org.mohankalia.studenttracker.dao;

import org.mohankalia.studenttracker.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    List<Student> findByUniversity(String university);
    List<Student> findByStudyLevel(String studyLevel);
}
