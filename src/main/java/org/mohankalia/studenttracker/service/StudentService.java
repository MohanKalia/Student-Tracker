package org.mohankalia.studenttracker.service;

import org.mohankalia.studenttracker.dao.StudentRepository;
import org.mohankalia.studenttracker.entity.Student;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
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
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student with id " + id + " not found");
    }

    public Student save(Student student) {
        return studentRepository.save(student);
    }

    public void deleteById(int id) {
        studentRepository.deleteById(id);
    }

    public List<Student> findByUniversity(String university) {
        return studentRepository.findByUniversity(university);
    }

    public List<Student> findByStudyLevel(String studyLevel) {
        return studentRepository.findByStudyLevel(studyLevel);
    }
}
