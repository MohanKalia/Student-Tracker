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
        if (temp.isPresent()) {
            return temp.get();
        }
        throw new RuntimeException("Student with id " + id + " not found");
    }

    public Student save(Student student) {
        return studentRepository.save(student);
    }

    public void deleteById(int id) {
        studentRepository.deleteById(id);
    }
}
