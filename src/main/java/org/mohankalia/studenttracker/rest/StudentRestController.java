package org.mohankalia.studenttracker.rest;

import org.mohankalia.studenttracker.dao.StudentRepository;
import org.mohankalia.studenttracker.service.StudentService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class StudentRestController {

    private StudentService studentService;

}
