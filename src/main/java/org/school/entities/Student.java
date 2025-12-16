package org.school.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")  // ← Its own table
public class Student extends User {

    @Column(name = "cne", length = 20, unique = true, nullable = false)
    private String cne;

    @Column(name = "first_name", length = 50, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 50, nullable = false)
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private Classe classe;

    @Column(name = "enrolled_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime enrolledAt = LocalDateTime.now();

    // Constructors
    public Student() {
        this.role = "Student";
    }

    public Student(String cne, String username, String password, 
                   String firstName, String lastName, LocalDate dateOfBirth,
                   String phone, String email) {
        this.cne = cne;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.phone = phone;
        this.email = email;
        this.role = "Student";
        this.isActive = true;
    }

    // Getters & Setters
    public String getCne() { return cne; }
    public void setCne(String cne) { this.cne = cne; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return firstName + " " + lastName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public Classe getClasse() { return classe; }
    public void setClasse(Classe classe) { this.classe = classe; }

    public LocalDateTime getEnrolledAt() { return enrolledAt; }

    @Override
    public String toString() {
        return this.username;
    }
}