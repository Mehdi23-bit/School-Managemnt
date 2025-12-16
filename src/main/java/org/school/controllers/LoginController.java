package org.school.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.school.dao.AdminDAO;
import org.school.dao.ProfessorDAO;
import org.school.dao.StudentDAO;
import org.school.entities.Admin;
import org.school.entities.Professor;
import org.school.entities.Student;
import org.school.session.SessionManager;

import java.io.IOException;
import java.time.LocalDate;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label messageLabel;
    @FXML private ComboBox<String> roleComboBox;

    private StudentDAO studentDAO;
    private ProfessorDAO professorDAO;
    private AdminDAO adminDAO;

    @FXML
    public void initialize() {
        // ---- INITIALIZE DAOs ----
        studentDAO = new StudentDAO();
        professorDAO = new ProfessorDAO();
        adminDAO = new AdminDAO();

        // ---- SET ROLE OPTIONS ----
        roleComboBox.getItems().addAll("Student", "Professor", "Admin");
        roleComboBox.setValue("Student");

        // ---- INSERT SAMPLE DATA IF EMPTY ----
        if (studentDAO.countStudents() == 0) {
            System.out.println(">>> Adding initial students...");
            addSampleStudents();
        }

        // ---- LOGIN ACTION ----
        loginButton.setOnAction(e -> authenticate());
    }

    private void addSampleStudents() {
        Student s1 = new Student(
                "CNE0001", "ayman01", "pass123", "Ayman", "El Fassi",
                LocalDate.of(2002, 5, 14), "0612345678", "ayman.fassi@example.com"
        );

        Student s2 = new Student(
                "CNE0002", "salma02", "pass123", "Salma", "Bennani",
                LocalDate.of(2001, 11, 3), "0623456789", "salma.bennani@example.com"
        );

        Student s3 = new Student(
                "CNE0003", "youssef03", "pass123", "Youssef", "Chakir",
                LocalDate.of(2003, 2, 20), "0634567890", "youssef.chakir@example.com"
        );

        Student s4 = new Student(
                "CNE0004", "iman04", "pass123", "Iman", "Touimi",
                LocalDate.of(2000, 9, 28), "0645678901", "iman.touimi@example.com"
        );

        Student s5 = new Student(
                "CNE0005", "mehdi05", "pass123", "Mehdi", "Skalli",
                LocalDate.of(2002, 12, 6), "0656789012", "mehdi.skalli@example.com"
        );

        studentDAO.saveStudent(s1);
        studentDAO.saveStudent(s2);
        studentDAO.saveStudent(s3);
        studentDAO.saveStudent(s4);
        studentDAO.saveStudent(s5);

        System.out.println(">>> Students added successfully!");
    }

    @FXML
    private void authenticate() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String selectedRole = roleComboBox.getValue();

        // Validate input
        if (username == null || username.trim().isEmpty()) {
            showError("Please enter username");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            showError("Please enter password");
            return;
        }

        if (selectedRole == null) {
            showError("Please select a role");
            return;
        }

        // Authenticate based on role
        SessionManager sm = SessionManager.getInstance();

        switch (selectedRole) {
            case "Student":
                Student student = studentDAO.getStudentByCredentials(username, password);
                if (student != null) {
                    sm.loginStudent(student);
                    showSuccess("Welcome, " + student.getFullName() + "!");
                    System.out.println("✓ Student logged in: " + student.getUsername());
                    navigateToDashboard("Student");
                } else {
                    showError("Invalid credentials");
                }
                break;

            case "Professor":
                Professor professor = professorDAO.getProfessorByCredentials(username, password);
                if (professor != null) {
                    sm.loginProfessor(professor);
                    showSuccess("Welcome, Prof. " + professor.getFullName() + "!");
                    System.out.println("✓ Professor logged in: " + professor.getUsername());
                    navigateToDashboard("Professor");
                } else {
                    showError("Invalid credentials");
                }
                break;

            case "Admin":
                Admin admin = adminDAO.getAdminByCredentials(username, password);
                if (admin != null) {
                    sm.loginAdmin(admin);
                    showSuccess("Welcome, Admin " + admin.getFullName() + "!");
                    System.out.println("✓ Admin logged in: " + admin.getUsername());
                    navigateToDashboard("Admin");
                } else {
                    showError("Invalid credentials");
                }
                break;

            default:
                showError("Invalid role selected");
        }
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
    }

    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
    }

    private void navigateToDashboard(String role) {
        try {
            String fxmlFile = "";

            switch (role) {
                case "Student":
                    fxmlFile = "/fxml/StudentDashboard.fxml";
                    break;
                case "Professor":
                    fxmlFile = "/fxml/ProfessorDashboard.fxml";
                    break;
                case "Admin":
                    fxmlFile = "/fxml/AdminDashboard.fxml";
                    break;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Get current stage and change scene
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading dashboard: " + e.getMessage());
        }
    }
}