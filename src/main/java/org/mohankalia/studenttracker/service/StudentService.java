package org.mohankalia.studenttracker.service;

import org.mohankalia.studenttracker.dao.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class StudentService {
    private StudentRepository studentRepository;

    @Autowired
    public void setStudentRepository(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

}
