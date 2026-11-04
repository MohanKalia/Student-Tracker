package org.mohankalia.studenttracker.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mohankalia.studenttracker.dao.StudentRepository;
import org.mohankalia.studenttracker.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:student_tracker_tests;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class StudentRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
    }

    @Test
    void createStudentReturnsTheExistingResponseFields() throws Exception {
        mockMvc.perform(post("/api/students")
                        .with(user("mary").roles("STUDENT", "PROFESSOR"))
                        .contentType("application/json")
                        .content("""
                                {"firstName":"Mohan","lastName":"Kalia","email":"mohan@example.com",
                                 "studyLevel":"Bachelor","university":"Example University"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Mohan"))
                .andExpect(jsonPath("$.lastName").value("Kalia"))
                .andExpect(jsonPath("$.email").value("mohan@example.com"))
                .andExpect(jsonPath("$.studyLevel").value("Bachelor"))
                .andExpect(jsonPath("$.university").value("Example University"));
    }

    @Test
    void createStudentRejectsMissingNamesAndInvalidEmail() throws Exception {
        mockMvc.perform(post("/api/students")
                        .with(user("mary").roles("STUDENT", "PROFESSOR"))
                        .contentType("application/json")
                        .content("""
                                {"firstName":" ","lastName":"","email":"not-an-email"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void studentRoleCanReadStudent() throws Exception {
        Student student = studentRepository.save(
                new Student(0, "Mohan", "Kalia", "mohan@example.com", "Bachelor", "Example University"));

        mockMvc.perform(get("/api/students/" + student.getId()).with(user("john").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(student.getId()))
                .andExpect(jsonPath("$.email").value("mohan@example.com"));
    }

    @Test
    void studentRoleCannotCreateStudent() throws Exception {
        mockMvc.perform(post("/api/students")
                        .with(user("john").roles("STUDENT"))
                        .contentType("application/json")
                        .content("""
                                {"firstName":"Mohan","lastName":"Kalia","email":"mohan@example.com"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void missingStudentReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/students/99").with(user("john").roles("STUDENT")))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchRejectsAnIdInTheRequestBody() throws Exception {
        Student student = studentRepository.save(
                new Student(0, "Mohan", "Kalia", "mohan@example.com", "Bachelor", "Example University"));

        mockMvc.perform(patch("/api/students/" + student.getId())
                        .with(user("mary").roles("STUDENT", "PROFESSOR"))
                        .contentType("application/json")
                        .content("{\"id\":8}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchRejectsAnInvalidEmail() throws Exception {
        Student student = studentRepository.save(
                new Student(0, "Mohan", "Kalia", "mohan@example.com", "Bachelor", "Example University"));

        mockMvc.perform(patch("/api/students/" + student.getId())
                        .with(user("mary").roles("STUDENT", "PROFESSOR"))
                        .contentType("application/json")
                        .content("{\"email\":\"not-an-email\"}"))
                .andExpect(status().isBadRequest());

        org.assertj.core.api.Assertions.assertThat(studentRepository.findById(student.getId()).orElseThrow().getEmail())
                .isEqualTo("mohan@example.com");
    }
}
