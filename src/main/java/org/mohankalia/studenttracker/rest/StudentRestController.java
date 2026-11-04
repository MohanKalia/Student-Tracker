package org.mohankalia.studenttracker.rest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.Valid;
import org.mohankalia.studenttracker.entity.Student;
import org.mohankalia.studenttracker.dto.StudentPatchRequest;
import org.mohankalia.studenttracker.dto.StudentRequest;
import org.mohankalia.studenttracker.dto.StudentResponse;
import org.mohankalia.studenttracker.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class StudentRestController {

    private final StudentService studentService;
    private final JsonMapper jsonMapper;
    private final Validator validator;

    public StudentRestController(StudentService studentService, JsonMapper jsonMapper, Validator validator) {
        this.studentService = studentService;
        this.jsonMapper = jsonMapper;
        this.validator = validator;
    }

    // gets all the students in the database
    @GetMapping("/students")
    public List<StudentResponse> findAll() {
        return studentService.findAll().stream().map(StudentResponse::from).toList();
    }

    // returns the single student if It exists in the database
    @GetMapping("/students/{id}")
    public StudentResponse findById(@PathVariable int id) {
        Student temp = studentService.findById(id);
        return StudentResponse.from(temp);
    }

    // Delete a student
    @DeleteMapping("/students/{id}")
    public String deleteStudent(@PathVariable int id) {
        studentService.findById(id);
        studentService.deleteById(id);
        return "Deleted Student with id " + id;
    }


    @PostMapping("/students")
    public StudentResponse addStudent(@Valid @RequestBody StudentRequest request) {
        Student student = toStudent(request);
        student.setId(0); // this will make sure that the student gets added and not merged
        return StudentResponse.from(studentService.save(student));
    }

    @PutMapping("/students")
    public StudentResponse updateStudent(@Valid @RequestBody StudentRequest request) {
        Student student = toStudent(request);
        studentService.findById(student.getId());
        return StudentResponse.from(studentService.save(student));
    }

    @PatchMapping("/students/{id}")
    public StudentResponse patchStudent(@PathVariable int id, @RequestBody StudentPatchRequest request) {
        Student temp = studentService.findById(id);
        var payLoadData = request.getFields();
        if (payLoadData.containsKey("id")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "For PATCH the id cannot be included into the body.");
        }
        StudentRequest updatedStudent = toRequest(temp);
        jsonMapper.updateValue(updatedStudent, payLoadData);
        Set<ConstraintViolation<StudentRequest>> violations = validator.validate(updatedStudent);
        if (!violations.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student details are invalid.");
        }
        return StudentResponse.from(studentService.save(toStudent(updatedStudent)));
    }

    // New mappings for university and studylevel
    @GetMapping("/students/university/{university}")
    public List<StudentResponse> findByUniversity(@PathVariable String university) {
        return studentService.findByUniversity(university).stream().map(StudentResponse::from).toList();
    }

    @GetMapping("/students/studylevel/{studyLevel}")
    public List<StudentResponse> findByStudyLevel(@PathVariable String studyLevel) {
        return studentService.findByStudyLevel(studyLevel).stream().map(StudentResponse::from).toList();
    }

    private Student toStudent(StudentRequest request) {
        return new Student(request.getId(), request.getFirstName(), request.getLastName(), request.getEmail(),
                request.getStudyLevel(), request.getUniversity());
    }

    private StudentRequest toRequest(Student student) {
        StudentRequest request = new StudentRequest();
        request.setId(student.getId());
        request.setFirstName(student.getFirstName());
        request.setLastName(student.getLastName());
        request.setEmail(student.getEmail());
        request.setStudyLevel(student.getStudyLevel());
        request.setUniversity(student.getUniversity());
        return request;
    }
}
