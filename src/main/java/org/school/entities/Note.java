// Note.java
package org.school.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "NOTES")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false)
    private double value;                       // 0 to 20

    @Column(name = "exam_type", length = 20)
    private String examType;                    // DS, Examen, Devoir...

    @Column(name = "given_at")
    private LocalDateTime givenAt = LocalDateTime.now();

    public Note() {}
    public Note(Student student, Subject subject, double value, String examType) {
        this.student = student;
        this.subject = subject;
        this.value = value;
        this.examType = examType;
    }

    // getters & setters
    public Long getId() { return id; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }
    public LocalDateTime getGivenAt() { return givenAt; }

    @Override
    public String toString() {
        return "Note{student=" + student.getFullName() + ", subject=" + subject.getName() +
               ", value=" + value + ", exam=" + examType + "}";
    }
}