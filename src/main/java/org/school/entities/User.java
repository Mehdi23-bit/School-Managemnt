package org.school.entities;

import jakarta.persistence.*;

@MappedSuperclass  // ← Changed from @Entity
public abstract class User {  // ← Make it abstract again
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    
    @Column(nullable = false, unique = true)
    protected String username;
    
    @Column(nullable = false)
    protected String password;
    
    @Column(nullable = false)
    protected String role;
    
    @Column(name = "phone", length = 20)
    protected String phone;

    @Column(name = "email", length = 100, unique = true)
    protected String email;

    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    protected boolean isActive = true;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
}