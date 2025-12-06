// Classe.java
package org.school.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "CLASSES")
public class Classe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50, unique = true)
    private String name;                    // e.g., "1ère Année Info", "Terminale S"

    @Column(name = "level", length = 20)
    private String level;                   // e.g., "1ère", "2ème", "Terminale"

    @Column(name = "capacity", nullable = false)
    private int capacity = 30;

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();
   
    @OneToMany(mappedBy = "classe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Student> students = new HashSet<>();
    
    @ManyToOne
    @JoinColumn(name = "major_id", nullable = false)
    private Major major;
    
    @ManyToMany(mappedBy = "classes")
    private Set<Professor> professors = new HashSet<>();

    // getter & setter
    public Major getMajor() { return major; }
    public void setMajor(Major major) { this.major = major; }

    public Classe() {}
    public Classe(String name, String level, int capacity) {
        this.name = name;
        this.level = level;
        this.capacity = capacity;
    }

    public void addStudent(Student student) {
        students.add(student);
        student.setClasse(this);
    }

    public void removeStudent(Student student) {
        students.remove(student);
        student.setClasse(null);
    }
    // getters & setters + toString
    // ... (same pattern as before)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Set<Professor> getProfessors(){return this.professors;}
    public Set<Student> getStudents(){return this.students;}
    @Override
    public String toString() {
        return "Classe{id=" + id + ", name='" + name + "', level='" + level + "', capacity=" + capacity + "}";
    }
}