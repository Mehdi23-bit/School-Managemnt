package org.school.controllers;

import java.util.List;
import java.util.Optional;

import org.school.dao.NoteDAO;
import org.school.dao.ProfessorDAO;
import org.school.dao.ReportDAO;
import org.school.dao.StudentDAO;
import org.school.entities.Classe;
import org.school.entities.Note;
import org.school.entities.Professor;
import org.school.entities.Report;
import org.school.entities.Student;
import org.school.session.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class ProfessorController {

    // ==================== Login Section ====================
    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;
    @FXML private Button loginButton;
    @FXML private Label loginErrorLabel;
    @FXML private VBox loginPane;

    // ==================== Dashboard Section ====================
    @FXML private VBox dashboardPane;
    @FXML private Label welcomeLabel;
    @FXML private Label professorNameLabel;
    @FXML private Label subjectLabel;
    @FXML private Button logoutButton;

    // ==================== Tabs ====================
    @FXML private TabPane mainTabPane;
    @FXML private Tab notesTab;
    @FXML private Tab reportsTab;

    // ==================== Notes Management ====================
    @FXML private ComboBox<Classe> classComboBox;
    @FXML private ComboBox<Student> studentComboBox;
    @FXML private TextField noteValueField;
    @FXML private ComboBox<String> examTypeComboBox;
    @FXML private Button submitNoteButton;
    @FXML private Label noteStatusLabel;
    @FXML private TableView<Note> notesTableView;
    @FXML private TableColumn<Note, String> studentNameColumn;
    @FXML private TableColumn<Note, Double> noteValueColumn;
    @FXML private TableColumn<Note, String> examTypeColumn;

    // ==================== Reports Management ====================
    @FXML private ComboBox<Student> reportStudentComboBox;
    @FXML private TextField reportTitleField;
    @FXML private TextArea reportContentArea;
    @FXML private Button submitReportButton;
    @FXML private Label reportStatusLabel;
    @FXML private TableView<Report> reportsTableView;
    @FXML private TableColumn<Report, String> reportStudentColumn;
    @FXML private TableColumn<Report, String> reportTitleColumn;
    @FXML private TableColumn<Report, String> reportStatusColumn;

    // ==================== DAOs ====================
    private ProfessorDAO professorDAO = new ProfessorDAO();
    private NoteDAO noteDAO = new NoteDAO();
    private ReportDAO reportDAO = new ReportDAO();
    private StudentDAO studentDAO = new StudentDAO();

    // ==================== Current User ====================
    private Professor currentProfessor;

     @FXML
    public void initialize() {
        // Récupérer le professeur connecté depuis SessionManager
        currentProfessor = SessionManager.getInstance().getAsProfessor();
        
        if (currentProfessor == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No professor logged in!");
            return;
        }
        
        setupExamTypeComboBox();
        setupTableColumns();
        loadDashboardData();
        
        // Setup button actions
        submitNoteButton.setOnAction(e -> handleSubmitNote());
        submitReportButton.setOnAction(e -> handleSubmitReport());
        logoutButton.setOnAction(e -> handleLogout());
    }

    private void setupLoginPane() {
        loginButton.setOnAction(e -> handleLogin());
    }

    private void setupExamTypeComboBox() {
        examTypeComboBox.getItems().addAll("DS", "Examen", "Devoir", "Contrôle", "Quiz");
    }

    private void setupTableColumns() {
        // Notes table
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("student"));
        noteValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        examTypeColumn.setCellValueFactory(new PropertyValueFactory<>("examType"));

        // Reports table
        reportStudentColumn.setCellValueFactory(new PropertyValueFactory<>("student"));
        reportTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        reportStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
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

        currentProfessor = professorDAO.getProfessorByCredentials(username, password);

        if (currentProfessor != null && currentProfessor.isActive()) {
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
        welcomeLabel.setText("Welcome, Professor!");
        professorNameLabel.setText(currentProfessor.getFullName());
        subjectLabel.setText("Subject: " + currentProfessor.getSubject().getName());

        // Load classes
        loadClassesForProfessor();

        // Setup class combo box listener
        classComboBox.setOnAction(e -> loadStudentsForClass());
    }

    private void loadClassesForProfessor() {
        classComboBox.getItems().clear();
        List<Classe> classes = professorDAO.getProfessorsByClass(currentProfessor.getId()).stream()
                .flatMap(p -> p.getClasses().stream())
                .distinct()
                .toList();

        if (classes != null && !classes.isEmpty()) {
            classComboBox.getItems().addAll(classes);
            classComboBox.getSelectionModel().selectFirst();
            loadStudentsForClass();
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Info", "No classes assigned");
        }
    }

    private void loadStudentsForClass() {
        Classe selectedClass = classComboBox.getValue();
        if (selectedClass == null) return;

        studentComboBox.getItems().clear();
        reportStudentComboBox.getItems().clear();

        List<Student> students = selectedClass.getStudents().stream().toList();
        if (students != null && !students.isEmpty()) {
            studentComboBox.getItems().addAll(students);
            reportStudentComboBox.getItems().addAll(students);
        }
    }

    // ==================== NOTES MANAGEMENT ====================
    @FXML
    private void handleSubmitNote() {
        Student student = studentComboBox.getValue();
        String noteValueStr = noteValueField.getText().trim();
        String examType = examTypeComboBox.getValue();

        if (student == null || noteValueStr.isEmpty() || examType == null) {
            noteStatusLabel.setText("Please fill all fields!");
            noteStatusLabel.setStyle("-fx-text-fill: #ff6b6b;");
            return;
        }

        try {
            double noteValue = Double.parseDouble(noteValueStr);

            if (noteValue < 0 || noteValue > 20) {
                noteStatusLabel.setText("Note must be between 0 and 20!");
                noteStatusLabel.setStyle("-fx-text-fill: #ff6b6b;");
                return;
            }

            Note note = new Note(student, currentProfessor.getSubject(), noteValue, examType);
            noteDAO.saveNote(note);

            noteStatusLabel.setText("✓ Note saved successfully!");
            noteStatusLabel.setStyle("-fx-text-fill: #51cf66;");

            noteValueField.clear();
            examTypeComboBox.getSelectionModel().clearSelection();
            loadNotesForStudent();
        } catch (NumberFormatException e) {
            noteStatusLabel.setText("Invalid note value!");
            noteStatusLabel.setStyle("-fx-text-fill: #ff6b6b;");
        }
    }

    private void loadNotesForStudent() {
        Student student = studentComboBox.getValue();
        if (student == null) return;

        notesTableView.getItems().clear();
        List<Note> notes = noteDAO.getNotesByStudent(student.getId());
        if (notes != null && !notes.isEmpty()) {
            notesTableView.getItems().addAll(notes);
        }
    }

    // ==================== REPORTS MANAGEMENT ====================
    @FXML
    private void handleSubmitReport() {
        Student student = reportStudentComboBox.getValue();
        String title = reportTitleField.getText().trim();
        String content = reportContentArea.getText().trim();

        if (student == null || title.isEmpty() || content.isEmpty()) {
            reportStatusLabel.setText("Please fill all fields!");
            reportStatusLabel.setStyle("-fx-text-fill: #ff6b6b;");
            return;
        }

        if (title.length() > 150) {
            reportStatusLabel.setText("Title must be less than 150 characters!");
            reportStatusLabel.setStyle("-fx-text-fill: #ff6b6b;");
            return;
        }

        Report report = new Report(student, currentProfessor, title, content);
        reportDAO.saveReport(report);

        reportStatusLabel.setText("✓ Report submitted to Admin!");
        reportStatusLabel.setStyle("-fx-text-fill: #51cf66;");

        reportTitleField.clear();
        reportContentArea.clear();
        reportStudentComboBox.getSelectionModel().clearSelection();
        loadReportsForProfessor();
    }

    private void loadReportsForProfessor() {
        reportsTableView.getItems().clear();
        List<Report> reports = reportDAO.getReportsByProfessor(currentProfessor.getId());
        if (reports != null && !reports.isEmpty()) {
            reportsTableView.getItems().addAll(reports);
        }
    }

    // ==================== LOGOUT ====================
    @FXML
    private void handleLogout() {
        Optional<ButtonType> result = showConfirmation("Logout", "Are you sure you want to logout?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            currentProfessor = null;
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