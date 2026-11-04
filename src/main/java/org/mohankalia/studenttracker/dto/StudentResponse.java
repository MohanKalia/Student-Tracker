package org.mohankalia.studenttracker.dto;

import org.mohankalia.studenttracker.entity.Student;

public record StudentResponse(int id, String firstName, String lastName, String email,
                              String studyLevel, String university) {

    public static StudentResponse from(Student student) {
        return new StudentResponse(student.getId(), student.getFirstName(), student.getLastName(),
                student.getEmail(), student.getStudyLevel(), student.getUniversity());
    }
}
