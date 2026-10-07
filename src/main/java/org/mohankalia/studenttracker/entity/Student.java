package org.mohankalia.studenttracker.entity;

import jakarta.persistence.*;

@Entity
@Table(name="student")
public class Student {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @Column(name="email")
    private String email;

    public Student(String email, String lastName, String firstName) {
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public Student() {

    }


    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
