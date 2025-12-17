package org.school.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "professors")  // ← Its own table
public class Professor extends User {

    @Column(name = "first_name", length = 50, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 50, nullable = false)
    private String lastName;

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

    @Column(name = "hired_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime hiredAt = LocalDateTime.now();

    // Constructors
    public Professor() {
        this.role = "Professor";
    }

    public Professor(String username, String password, String firstName,
                     String lastName, String email, Subject subject) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.subject = subject;
        this.role = "Professor";
        this.isActive = true;
    }

    // Helper methods
    public void addClass(Classe classe) {
        classes.add(classe);
        classe.getProfessors().add(this);
    }

    public void removeClass(Classe classe) {
        classes.remove(classe);
        classe.getProfessors().remove(this);
    }

    // Getters & Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return firstName + " " + lastName; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public Set<Classe> getClasses() { return classes; }
    public void setClasses(Set<Classe> classes) {  this.classes=classes; }

    public LocalDateTime getHiredAt() { return hiredAt; }

    @Override
    public String toString() {
        return "Professor{id=" + id + ", username='" + username + 
               "', name='" + getFullName() + "', subject=" + 
               (subject != null ? subject.getName() : "None") + ", active=" + isActive + "}";
    }
}