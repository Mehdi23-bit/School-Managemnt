package org.school.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.school.dao.StudentDAO;
import org.school.dao.NoteDAO;
import org.school.dao.ReportDAO;
import org.school.dao.ClasseDAO;
import org.school.entities.*;

import java.util.List;
import java.util.Optional;

public class StudentController {

    // ==================== Login Section ====================
    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;
    @FXML private Button loginButton;
    @FXML private Label loginErrorLabel;
    @FXML private VBox loginPane;

    // ==================== Dashboard Section ====================
    @FXML private VBox dashboardPane;
    @FXML private Label welcomeLabel;
    @FXML private Label studentNameLabel;
    @FXML private Label cneLabel;
    @FXML private Label classLabel;
    @FXML private Button logoutButton;

    // ==================== Tabs ====================
    @FXML private TabPane mainTabPane;
    @FXML private Tab gradesTab;
    @FXML private Tab attestationTab;

    // ==================== GRADES TAB ====================
    @FXML private ComboBox<Subject> subjectComboBox;
    @FXML private TableView<Note> gradesTableView;
    @FXML private TableColumn<Note, Double> gradeValueColumn;
    @FXML private TableColumn<Note, String> examTypeColumn;
    @FXML private TableColumn<Note, String> dateColumn;
    @FXML private Label averageLabel;
    @FXML private Label noGradesLabel;

    // ==================== ATTESTATION REQUEST TAB ====================
    @FXML private ComboBox<String> attestationTypeComboBox;
    @FXML private TextArea attestationReasonArea;
    @FXML private Button submitAttestationButton;
    @FXML private Label attestationStatusLabel;
    @FXML private TableView<Report> attestationTableView;
    @FXML private TableColumn<Report, String> attestationTypeColumn;
    @FXML private TableColumn<Report, String> attestationStatusColumn;
    @FXML private TableColumn<Report, String> attestationDateColumn;

    // ==================== DAOs ====================
    private StudentDAO studentDAO = new StudentDAO();
    private NoteDAO noteDAO = new NoteDAO();
    private ReportDAO reportDAO = new ReportDAO();
    private ClasseDAO classeDAO = new ClasseDAO();
    // ==================== Current User ====================
    private Student currentStudent;

    // ==================== Initialize ====================
    @FXML
    public void initialize() {
        setupLoginPane();
        setupAttestationTypeComboBox();
        setupTableColumns();
    }

    private void setupLoginPane() {
        loginButton.setOnAction(e -> handleLogin());
    }

    private void setupAttestationTypeComboBox() {
        attestationTypeComboBox.getItems().addAll(
            "School Attendance Certificate",
            "Grade Transcript",
            "Good Conduct Certificate",
            "Enrollment Certificate",
            "Other Request"
        );
    }

    private void setupTableColumns() {
        // Grades table
        gradeValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        examTypeColumn.setCellValueFactory(new PropertyValueFactory<>("examType"));
        dateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getGivenAt().toString()
            )
        );

        // Attestation table
        attestationTypeColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getTitle()
            )
        );
        attestationStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        attestationDateColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getCreatedAt().toString()
            )
        );
    }

    // ==================== LOGIN ====================
    @FXML
    private void handleLogin() {
        String username = loginUsernameField.getText().trim();
        String password = loginPasswordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            loginErrorLabel.setText("Username and password required!");
            loginErrorLabel.setStyle("-fx-text-fill: #ff6b6b;");
            return;
        }

        currentStudent = studentDAO.getStudentByCredentials(username, password);

        if (currentStudent != null && currentStudent.isActive()) {
            showDashboard();
            loadDashboardData();
        } else {
            loginErrorLabel.setText("Invalid credentials or account inactive!");
            loginErrorLabel.setStyle("-fx-text-fill: #ff6b6b;");
            loginPasswordField.clear();
        }
    }

    private void showDashboard() {
        loginPane.setVisible(false);
        dashboardPane.setVisible(true);
    }

    // ==================== DASHBOARD SETUP ====================
    private void loadDashboardData() {
        // Set welcome info
        welcomeLabel.setText("Welcome, Student!");
        studentNameLabel.setText(currentStudent.getFullName());
        cneLabel.setText("CNE: " + currentStudent.getCne());
        if (currentStudent.getClasse() != null) {
            classLabel.setText("Class: " + currentStudent.getClasse().getName());
        }

        // Load subjects for grade selection
        loadSubjectsForStudent();

        // Load attestation requests
        loadAttestationRequests();

        // Setup subject combo box listener
        subjectComboBox.setOnAction(e -> loadGradesForSubject());
    }

    private void loadSubjectsForStudent() {
        subjectComboBox.getItems().clear();
        
        List<Note> allNotes = noteDAO.getNotesByStudent(currentStudent.getId());
        
        if (allNotes != null && !allNotes.isEmpty()) {
            // Get unique subjects
            ObservableList<Subject> subjects = FXCollections.observableArrayList();
            for (Note note : allNotes) {
                if (!subjects.contains(note.getSubject())) {
                    subjects.add(note.getSubject());
                }
            }
            
            subjectComboBox.setItems(subjects);
            if (!subjects.isEmpty()) {
                subjectComboBox.getSelectionModel().selectFirst();
                loadGradesForSubject();
            }
        } else {
            noGradesLabel.setText("No grades available yet");
            noGradesLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 14;");
        }
    }

    // ==================== GRADES TAB ====================
    private void loadGradesForSubject() {
        Subject selectedSubject = subjectComboBox.getValue();
        if (selectedSubject == null) return;

        gradesTableView.getItems().clear();
        
        List<Note> grades = noteDAO.getNotesByStudentAndSubject(
            currentStudent.getId(), 
            selectedSubject.getId()
        );

        if (grades != null && !grades.isEmpty()) {
            gradesTableView.getItems().addAll(grades);
            
            // Calculate average
            Double average = noteDAO.getAverageNoteByStudentAndSubject(
                currentStudent.getId(),
                selectedSubject.getId()
            );
            
            averageLabel.setText(String.format("Average: %.2f/20", average));
            averageLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #3498db;");
            
            noGradesLabel.setText("");
        } else {
            averageLabel.setText("");
            noGradesLabel.setText("No grades for this subject");
            noGradesLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 12;");
        }
    }

    // ==================== ATTESTATION REQUEST TAB ====================
    @FXML
    private void handleSubmitAttestation() {
        String attestationType = attestationTypeComboBox.getValue();
        String reason = attestationReasonArea.getText().trim();

        if (attestationType == null || reason.isEmpty()) {
            attestationStatusLabel.setText("Please fill all fields!");
            attestationStatusLabel.setStyle("-fx-text-fill: #ff6b6b;");
            return;
        }

        if (reason.length() > 500) {
            attestationStatusLabel.setText("Reason must be less than 500 characters!");
            attestationStatusLabel.setStyle("-fx-text-fill: #ff6b6b;");
            return;
        }

        // Create report as attestation request to admin
        String title = "Attestation Request: " + attestationType;
        String content = "Student: " + currentStudent.getFullName() + "\n" +
                        "CNE: " + currentStudent.getCne() + "\n" +
                        "Request Type: " + attestationType + "\n\n" +
                        "Reason:\n" + reason;

        // Get a professor (or use null if needed for admin)
        // For now, we'll use the first professor from the student's class
        Professor professor = null;

if (currentStudent.getClasse() != null) {
    List<Professor> professors =
        classeDAO.getProfessorsInClasse(currentStudent.getClasse().getId());

    if (professors != null && !professors.isEmpty()) {
        professor = professors.get(0);
    }
}


        if (professor != null) {
            Report attestationRequest = new Report(currentStudent, professor, title, content);
            reportDAO.saveReport(attestationRequest);

            attestationStatusLabel.setText("✓ Request submitted to Admin!");
            attestationStatusLabel.setStyle("-fx-text-fill: #51cf66;");

            attestationReasonArea.clear();
            attestationTypeComboBox.getSelectionModel().clearSelection();
            loadAttestationRequests();
        } else {
            attestationStatusLabel.setText("Error: No professor assigned to your class!");
            attestationStatusLabel.setStyle("-fx-text-fill: #ff6b6b;");
        }
    }

    private void loadAttestationRequests() {
        attestationTableView.getItems().clear();
        
        // Get all reports related to this student
        List<Report> requests = reportDAO.getReportsByStudent(currentStudent.getId());
        
        if (requests != null && !requests.isEmpty()) {
            // Filter only attestation requests
            requests = requests.stream()
                .filter(r -> r.getTitle().startsWith("Attestation Request:"))
                .toList();
            
            attestationTableView.getItems().addAll(requests);
        }
    }

    // ==================== LOGOUT ====================
    @FXML
    private void handleLogout() {
        Optional<ButtonType> result = showConfirmation("Logout", "Are you sure you want to logout?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            currentStudent = null;
            loginPane.setVisible(true);
            dashboardPane.setVisible(false);
            loginUsernameField.clear();
            loginPasswordField.clear();
            loginErrorLabel.setText("");
        }
    }

    // ==================== UTILITIES ====================
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Optional<ButtonType> showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        return alert.showAndWait();
    }
}