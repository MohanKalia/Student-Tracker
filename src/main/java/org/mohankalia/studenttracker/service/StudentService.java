package org.mohankalia.studenttracker.service;

import org.mohankalia.studenttracker.dao.StudentRepository;
import org.mohankalia.studenttracker.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private StudentRepository studentRepository;

    @Autowired
    public void setStudentRepository(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public Student findById(int id) {
        Optional<Student> temp = studentRepository.findById(id);
        if (temp.get() == null) {
            throw new RuntimeException("The student does not exist with the id :" + id);
        }
        return temp.get();
    }

    public Student save(Student student) {
        return studentRepository.save(student);
    }
}
