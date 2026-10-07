package org.mohankalia.studenttracker.rest;

import org.mohankalia.studenttracker.dao.StudentRepository;
import org.mohankalia.studenttracker.entity.Student;
import org.mohankalia.studenttracker.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StudentRestController {

    private StudentService studentService;

    public StudentRestController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/students")
    public List<Student> findAll() {
        return studentService.findAll();
    }

    @GetMapping("/students/{id}")
    public Student findById(@PathVariable int id) {
        Student temp = studentService.findById(id);
        if (temp == null) {
            throw new RuntimeException("The Student with id " + id + " does not exist");
        }
        return temp;
    }


    @PostMapping("/students")
    public Student addStudent(@RequestBody Student student) {
        Student temp = studentService.save(student);
        return temp;
    }

    @PutMapping("/students/{id}")
    public Student updateStudent(@PathVariable int id, @RequestBody Student student) {
        Student temp = studentService.findById(id);
        if (temp == null) {
            throw new RuntimeException("The Student with id " + id + " does not exist");
        }
        return studentService.save(student);
    }

}
