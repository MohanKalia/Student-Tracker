package org.mohankalia.studenttracker.rest;

import org.mohankalia.studenttracker.dao.StudentRepository;
import org.mohankalia.studenttracker.entity.Student;
import org.mohankalia.studenttracker.service.StudentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StudentRestController {

    private StudentService studentService;

    public StudentRestController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/students")
    public List<Student> findAll(){
       return studentService.findAll();
    }

}
