// src/main/java/org/school/entities/Student.java
package org.school.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "STUDENTS")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cne", length = 20, unique = true, nullable = false)
    private String cne;                          // CNE or CIN

    @Column(name = "username", length = 50, unique = true, nullable = false)
    private String username;                     // For login

    @Column(name = "password", length = 255, nullable = false)
    private String password;                     // Will store hashed password (BCrypt later)

    @Column(name = "first_name", length = 50, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 50, nullable = false)
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 100, unique = true)
    private String email;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private Classe classe;

    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private boolean isActive = true;

    @Column(name = "enrolled_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime enrolledAt = LocalDateTime.now();


    // ==================== Constructors ====================
    public Student() {}

    public Student(String cne, String username, String password,
                   String firstName, String lastName, LocalDate dateOfBirth) {
        this.cne = cne;
        this.username = username;
        this.password = password;                // Later: hash it with BCrypt
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
    }

    // ==================== Getters & Setters ====================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCne() { return cne; }
    public void setCne(String cne) { this.cne = cne; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Classe getClasse() { return classe; }
    public void setClasse(Classe classe) { this.classe = classe; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public LocalDateTime getEnrolledAt() { return enrolledAt; }

    // ==================== toString ====================
    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", cne='" + cne + '\'' +
                ", username='" + username + '\'' +
                ", name='" + firstName + " " + lastName + '\'' +
                ", birth=" + dateOfBirth +
                ", class=" + (classe != null ? classe.getName() : "None") +
                ", active=" + isActive +
                '}';
    }
}