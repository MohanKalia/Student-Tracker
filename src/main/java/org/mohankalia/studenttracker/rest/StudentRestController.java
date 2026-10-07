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
        student.setId(0);
        Student temp = studentService.save(student);
        return temp;
    }

    @PutMapping("/students")
    public Student updateStudent(@RequestBody Student student) {
        Student temp = studentService.save(student);
        return temp;
    }

    @DeleteMapping("/students/{id}")
    public String deleteStudent(@PathVariable int id) {
        Student temp = studentService.findById(id);
        if (temp == null) {
            throw new RuntimeException("The Student with id " + id + " does not exist");
        }
        studentService.deleteById(id);
        return "Deleted Student with id " + id;
    }
}
