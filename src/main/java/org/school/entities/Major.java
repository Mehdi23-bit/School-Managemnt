// src/main/java/org/school/entities/Major.java
package org.school.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "MAJORS")
public class Major {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", length = 20, unique = false, nullable = false)
    private String code;                    // e.g., "INFO", "SM", "SVT", "ECO"

    @Column(name = "name", length = 100, nullable = false)
    private String name;                    // e.g., "Informatique", "Sciences Maths", "Économie"

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "duration_years", nullable = false)
    private int durationYears = 2;          // Bac+2, Bac+3, etc.

    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    // One Major → Many Classes (1ère année Info, 2ème année Info, etc.)
    @OneToMany(mappedBy = "major", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Classe> classes = new HashSet<>();

    // ==================== Constructors ====================
    public Major() {}

    public Major(String code, String name, int durationYears) {
        this.code = code.toUpperCase();
        this.name = name;
        this.durationYears = durationYears;
    }

    // ==================== Helper Methods ====================
    public void addClass(Classe classe) {
        classes.add(classe);
        classe.setMajor(this);
    }

    public void removeClass(Classe classe) {
        classes.remove(classe);
        classe.setMajor(null);
    }

    // ==================== Getters & Setters ====================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code.toUpperCase(); }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getDurationYears() { return durationYears; }
    public void setDurationYears(int durationYears) { this.durationYears = durationYears; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public Set<Classe> getClasses() { return classes; }
    public void setClasses(Set<Classe> classes) { this.classes = classes; }

    // ==================== toString ====================
    @Override
    public String toString() {
        return "Major{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", duration=" + durationYears + " years" +
                ", classes=" + classes.size() +
                ", active=" + isActive +
                '}';
    }
}