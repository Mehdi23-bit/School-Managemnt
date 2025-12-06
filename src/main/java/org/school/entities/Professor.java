// src/main/java/org/school/entities/Professor.java
package org.school.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "PROFESSORS")
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 50, unique = true, nullable = false)
    private String username;                     // For login (e.g., "prof.math", "ahmed.phys")

    @Column(name = "password", length = 255, nullable = false)
    private String password;                     // Store hashed password (BCrypt)

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

@ManyToMany
    @JoinTable(
        name = "class_professor",
        joinColumns = @JoinColumn(name = "professor_id"),
        inverseJoinColumns = @JoinColumn(name = "class_id")
    )
    private Set<Classe> classes = new HashSet<>();

    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private boolean isActive = true;

    @Column(name = "hired_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime hiredAt = LocalDateTime.now();

    // ==================== Constructors ====================
    public Professor() {}

    public Professor(String username, String password, String fullName, String email,Subject subject) {
        this.username = username;
        this.password = password;        // Later: hash it!
        this.fullName = fullName;
        this.email = email;
        this.subject=subject;
    }

   
    public void addClass(Classe classe) {
        classes.add(classe);
        classe.getProfessors().add(this);
    }

    public void removeClass(Classe classe) {
        classes.remove(classe);
        classe.getProfessors().remove(this);
    }

    // ==================== Getters & Setters ====================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }
    public Set<Classe> getClasses(){return this.classes;}

    

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public LocalDateTime getHiredAt() { return hiredAt; }

    // ==================== toString ====================
    @Override
    public String toString() {
        return "Professor{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", name='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", subject=" + subject.getName() +
                ", active=" + isActive +
                ", hiredAt=" + hiredAt +
                '}';
    }
}