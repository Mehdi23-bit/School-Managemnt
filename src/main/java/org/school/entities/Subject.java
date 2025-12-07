// Subject.java
package org.school.entities;

import java.util.HashSet;

import jakarta.persistence.*;
import java.util.Set;
@Entity
@Table(name = "SUBJECTS")
public class Subject  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;                // e.g., "Mathématiques", "Français", "Physique"

    @Column(name = "coefficient", nullable = false)
    private double coefficient = 1.0;
@OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Professor> professors = new HashSet<>();
    
    
    public Subject() {}
    public Subject(String name, double coefficient) {
        this.name = name;
        this.coefficient = coefficient;
    }

    public void addProfessor(Professor professor) {
        professors.add(professor);
        professor.setSubject(this);
    }

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getCoefficient() { return coefficient; }
    public void setCoefficient(double coefficient) { this.coefficient = coefficient; }

    @Override
    public String toString() {
        return "Subject{id=" + id + ", name='" + name + "', coeff=" + coefficient + "}";
    }
}