package org.school.controllers;

import java.util.List;
import java.util.Optional;

import org.school.dao.NoteDAO;
import org.school.dao.ReportDAO;
import org.school.entities.Note;
import org.school.entities.Professor;
import org.school.entities.Report;
import org.school.entities.Student;
import org.school.entities.Subject;
import org.school.session.SessionManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class StudentController {

    // ==================== Dashboard Section ====================
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
    private NoteDAO noteDAO = new NoteDAO();
    private ReportDAO reportDAO = new ReportDAO();

    // ==================== Current User ====================
    private Student currentStudent;

    // ==================== Initialize ====================
    @FXML
    public void initialize() {
        // Récupérer l'étudiant connecté depuis SessionManager
        currentStudent = SessionManager.getInstance().getAsStudent();
        
        if (currentStudent == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No student logged in!");
            return;
        }
        
        setupAttestationTypeComboBox();
        setupTableColumns();
        loadDashboardData();
        
        // Setup button actions
        submitAttestationButton.setOnAction(e -> handleSubmitAttestation());
        logoutButton.setOnAction(e -> handleLogout());
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

    // ==================== DASHBOARD SETUP ====================
    private void loadDashboardData() {
        // Set welcome info
        welcomeLabel.setText("Student");
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

        Professor professor = null;
        if (currentStudent.getClasse() != null && 
            !currentStudent.getClasse().getProfessors().isEmpty()) {
            professor = currentStudent.getClasse().getProfessors().iterator().next();
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
        
        List<Report> requests = reportDAO.getReportsByStudent(currentStudent.getId());
        
        if (requests != null && !requests.isEmpty()) {
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
            // Déconnecter l'utilisateur
            SessionManager.getInstance().logout();
            
            // Retourner à l'écran de login
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
                Parent root = loader.load();
                
                Stage stage = (Stage) logoutButton.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Could not return to login page");
            }
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