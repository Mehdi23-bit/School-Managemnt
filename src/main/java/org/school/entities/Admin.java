package org.school.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admins   ")  // ← Its own table
public class Admin extends User {

    @Column(name = "first_name", length = 50, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 50, nullable = false)
    private String lastName;

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors
    public Admin() {
        this.role = "Admin";
    }

    public Admin(String username, String password, String firstName,
                 String lastName, String email) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = "Admin";
        this.isActive = true;
    }

    // Getters & Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return firstName + " " + lastName; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return "Admin{id=" + id + ", username='" + username + 
               "', name='" + getFullName() + "', active=" + isActive + "}";
    }
}