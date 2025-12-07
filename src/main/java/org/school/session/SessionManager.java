package org.school.session;

import org.school.entities.Student;
import org.school.entities.Professor;
import org.school.entities.Admin;

public class SessionManager {
    
    private static SessionManager instance;
    private Object loggedInUser;  // ← Changed to Object
    private String userRole;
    
    private SessionManager() {}
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    // Login methods for each type
    public void loginStudent(Student student) {
        this.loggedInUser = student;
        this.userRole = "Student";
    }
    
    public void loginProfessor(Professor professor) {
        this.loggedInUser = professor;
        this.userRole = "Professor";
    }
    
    public void loginAdmin(Admin admin) {
        this.loggedInUser = admin;
        this.userRole = "Admin";
    }
    
    // Get as specific type
    public Student getAsStudent() {
        return (loggedInUser instanceof Student) ? (Student) loggedInUser : null;
    }
    
    public Professor getAsProfessor() {
        return (loggedInUser instanceof Professor) ? (Professor) loggedInUser : null;
    }
    
    public Admin getAsAdmin() {
        return (loggedInUser instanceof Admin) ? (Admin) loggedInUser : null;
    }
    
    // Type checking
    public boolean isStudent() {
        return loggedInUser instanceof Student;
    }
    
    public boolean isProfessor() {
        return loggedInUser instanceof Professor;
    }
    
    public boolean isAdmin() {
        return loggedInUser instanceof Admin;
    }
    
    public boolean isLoggedIn() {
        return loggedInUser != null;
    }
    
    // Common properties
    public String getRole() {
        return userRole;
    }
    
    public String getUsername() {
        if (loggedInUser instanceof Student) {
            return ((Student) loggedInUser).getUsername();
        } else if (loggedInUser instanceof Professor) {
            return ((Professor) loggedInUser).getUsername();
        } else if (loggedInUser instanceof Admin) {
            return ((Admin) loggedInUser).getUsername();
        }
        return null;
    }
    
    public Long getUserId() {
        if (loggedInUser instanceof Student) {
            return ((Student) loggedInUser).getId();
        } else if (loggedInUser instanceof Professor) {
            return ((Professor) loggedInUser).getId();
        } else if (loggedInUser instanceof Admin) {
            return ((Admin) loggedInUser).getId();
        }
        return null;
    }
    
    public void logout() {
        loggedInUser = null;
        userRole = null;
    }
}