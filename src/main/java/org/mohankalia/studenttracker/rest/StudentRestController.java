package org.mohankalia.studenttracker.rest;

import org.mohankalia.studenttracker.dao.StudentRepository;
import org.mohankalia.studenttracker.entity.Student;
import org.mohankalia.studenttracker.service.StudentService;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StudentRestController {

    private StudentService studentService;
    private JsonMapper jsonMapper;

    public StudentRestController(StudentService studentService, JsonMapper jsonMapper) {
        this.studentService = studentService;
        this.jsonMapper = jsonMapper;
    }

    // gets all the students in the database
    @GetMapping("/students")
    public List<Student> findAll() {
        return studentService.findAll();
    }

    // returns the single student if It exists in the database
    @GetMapping("/students/{id}")
    public Student findById(@PathVariable int id) {
        Student temp = studentService.findById(id);
        if (temp == null) {
            throw new RuntimeException("The Student with id " + id + " does not exist");
        }
        return temp;
    }

    // Delete a student
    @DeleteMapping("/students/{id}")
    public String deleteStudent(@PathVariable int id) {
        Student temp = studentService.findById(id);
        if (temp == null) {
            throw new RuntimeException("The Student with id " + id + " does not exist");
        }
        studentService.deleteById(id);
        return "Deleted Student with id " + id;
    }


    @PostMapping("/students")
    public Student addStudent(@RequestBody Student student) {
        student.setId(0); // this will make sure that the student gets added and not merged
        Student temp = studentService.save(student);
        return temp;
    }

    @PutMapping("/students")
    public Student updateStudent(@RequestBody Student student) {
        Student temp = studentService.findById(student.getId());
        if (temp == null) {
            throw new RuntimeException("The Student with id " + student.getId() + " does not exist");
        }
        temp = studentService.save(student);
        return temp;
    }

    @PatchMapping("/students/{id}")
    public Student patchStudent(@PathVariable int id, @RequestBody Map<String, Object> payLoadData) {
        Student temp = studentService.findById(id);
        if (temp == null) {
            throw new RuntimeException("The Student with id " + id + " does not exist");
        }
        if (payLoadData.containsKey("id")) {
            throw new RuntimeException("For PATCH the id cannot be included into the body.");
        }
        temp = jsonMapper.updateValue(temp, payLoadData);
        Student dbStudent = studentService.save(temp);
        return dbStudent;
    }

    // New mappings for university and studylevel
    @GetMapping("/students/{university}")
    public List<Student> findByUniversity(@PathVariable String university) {
        return studentService.findByUniversity(university);
    }

    @GetMapping("/students/{studyLevel")
    public List<Student> findByStudyLevel(@PathVariable String studyLevel) {
        return studentService.findByStudyLevel(studyLevel);
    }
}
