package org.mohankalia.studenttracker.service;

import org.mohankalia.studenttracker.dao.StudentRepository;
import org.mohankalia.studenttracker.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {
    private StudentRepository studentRepository;

    @Autowired
    public void setStudentRepository(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> findAll(){
        return  studentRepository.findAll();
    }

}
