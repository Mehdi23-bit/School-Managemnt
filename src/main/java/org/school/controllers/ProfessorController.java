package org.school.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.school.dao.*;
import org.school.entities.*;
import org.school.session.SessionManager;

import java.util.List;
import java.util.Optional;

public class ProfessorController {
     
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
    @FXML private MenuItem editNoteMenuItem;
    @FXML private MenuItem deleteNoteMenuItem;

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
    private ClasseDAO classedao = new ClasseDAO();
    
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
        setupComboBoxCellFactories();
        setupTableContextMenu();
        loadDashboardData();
        
        // Setup button actions
        submitNoteButton.setOnAction(e -> handleSubmitNote());
        submitReportButton.setOnAction(e -> handleSubmitReport());
        logoutButton.setOnAction(e -> handleLogout());
    }

    // ✅ Setup context menu for notes table
    private void setupTableContextMenu() {
        if (editNoteMenuItem != null) {
            editNoteMenuItem.setOnAction(e -> handleEditNote());
        }
        if (deleteNoteMenuItem != null) {
            deleteNoteMenuItem.setOnAction(e -> handleDeleteNote());
        }
        
        // Disable menu items when no row is selected
        notesTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            if (editNoteMenuItem != null) editNoteMenuItem.setDisable(!hasSelection);
            if (deleteNoteMenuItem != null) deleteNoteMenuItem.setDisable(!hasSelection);
        });
    }

    // ✅ Handle Edit Note
    @FXML
    private void handleEditNote() {
        Note selectedNote = notesTableView.getSelectionModel().getSelectedItem();
        if (selectedNote == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a grade to edit!");
            return;
        }
        
        showEditNoteDialog(selectedNote);
    }

    // ✅ Show Edit Note Dialog
    private void showEditNoteDialog(Note note) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Grade");
        dialog.setHeaderText("Modify grade for: " + note.getStudent().getFullName());
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Grade field
        TextField gradeField = new TextField(String.valueOf(note.getValue()));
        gradeField.setPromptText("Grade (0-20)");
        
        // Exam type combo
        ComboBox<String> examTypeCombo = new ComboBox<>();
        examTypeCombo.getItems().addAll("DS", "Examen", "Devoir", "Contrôle", "Quiz");
        examTypeCombo.setValue(note.getExamType());
        
        grid.add(new Label("Student:"), 0, 0);
        grid.add(new Label(note.getStudent().getFullName()), 1, 0);
        grid.add(new Label("Subject:"), 0, 1);
        grid.add(new Label(note.getSubject().getName()), 1, 1);
        grid.add(new Label("Grade (0-20):"), 0, 2);
        grid.add(gradeField, 1, 2);
        grid.add(new Label("Exam Type:"), 0, 3);
        grid.add(examTypeCombo, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);
        
        Optional<ButtonType> result = dialog.showAndWait();
        
        if (result.isPresent() && result.get() == saveButton) {
            try {
                double newGrade = Double.parseDouble(gradeField.getText().trim());
                String newExamType = examTypeCombo.getValue();
                
                if (newGrade < 0 || newGrade > 20) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Grade", "Grade must be between 0 and 20!");
                    return;
                }
                
                if (newExamType == null || newExamType.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Missing Information", "Please select an exam type!");
                    return;
                }
                
                // Update note
                note.setValue(newGrade);
                note.setExamType(newExamType);
                noteDAO.updateNote(note);
                
                noteStatusLabel.setText("✓ Grade updated successfully!");
                noteStatusLabel.setStyle("-fx-text-fill: #51cf66;");
                
                // Refresh table
                loadNotesForStudent();
                
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for the grade!");
            }
        }
    }

    // ✅ Handle Delete Note
    @FXML
    private void handleDeleteNote() {
        Note selectedNote = notesTableView.getSelectionModel().getSelectedItem();
        if (selectedNote == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a grade to delete!");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Grade");
        confirmAlert.setContentText(
            "Are you sure you want to delete this grade?\n\n" +
            "Student: " + selectedNote.getStudent().getFullName() + "\n" +
            "Grade: " + selectedNote.getValue() + "\n" +
            "Exam Type: " + selectedNote.getExamType()
        );
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                noteDAO.deleteNote(selectedNote.getId());
                
                noteStatusLabel.setText("✓ Grade deleted successfully!");
                noteStatusLabel.setStyle("-fx-text-fill: #51cf66;");
                
                // Refresh table
                loadNotesForStudent();
                
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not delete grade: " + e.getMessage());
            }
        }
    }

    // ✅ Configuration des cellFactory pour afficher les noms
    private void setupComboBoxCellFactories() {
        // ComboBox pour Classe
        classComboBox.setCellFactory(param -> new ListCell<Classe>() {
            @Override
            protected void updateItem(Classe classe, boolean empty) {
                super.updateItem(classe, empty);
                setText(empty || classe == null ? null : classe.getName());
            }
        });
        classComboBox.setButtonCell(new ListCell<Classe>() {
            @Override
            protected void updateItem(Classe classe, boolean empty) {
                super.updateItem(classe, empty);
                setText(empty || classe == null ? null : classe.getName());
            }
        });

        // ComboBox pour Student (Notes)
        studentComboBox.setCellFactory(param -> new ListCell<Student>() {
            @Override
            protected void updateItem(Student student, boolean empty) {
                super.updateItem(student, empty);
                setText(empty || student == null ? null : student.getFullName());
            }
        });
        studentComboBox.setButtonCell(new ListCell<Student>() {
            @Override
            protected void updateItem(Student student, boolean empty) {
                super.updateItem(student, empty);
                setText(empty || student == null ? null : student.getFullName());
            }
        });

        // ComboBox pour Student (Reports)
        reportStudentComboBox.setCellFactory(param -> new ListCell<Student>() {
            @Override
            protected void updateItem(Student student, boolean empty) {
                super.updateItem(student, empty);
                setText(empty || student == null ? null : student.getFullName());
            }
        });
        reportStudentComboBox.setButtonCell(new ListCell<Student>() {
            @Override
            protected void updateItem(Student student, boolean empty) {
                super.updateItem(student, empty);
                setText(empty || student == null ? null : student.getFullName());
            }
        });
    }

    private void setupExamTypeComboBox() {
        examTypeComboBox.getItems().addAll("DS", "Examen", "Devoir", "Contrôle", "Quiz");
    }

    private void setupTableColumns() {
        // Notes table - Afficher le nom complet de l'étudiant
        studentNameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStudent().getFullName()
            )
        );
        noteValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        examTypeColumn.setCellValueFactory(new PropertyValueFactory<>("examType"));

        // Reports table - Afficher le nom complet de l'étudiant
        reportStudentColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStudent().getFullName()
            )
        );
        reportTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        reportStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
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
        
        // Setup student combo box listener
        studentComboBox.setOnAction(e -> loadNotesForStudent());
        
        // Load reports
        loadReportsForProfessor();
    }

    private void loadClassesForProfessor() {
        classComboBox.getItems().clear();
        List<Classe> classes = professorDAO.getClassesByProfessor(currentProfessor.getId());

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

        System.out.println("Loading students for class: " + selectedClass.getName() + " (ID: " + selectedClass.getId() + ")");

        studentComboBox.getItems().clear();
        reportStudentComboBox.getItems().clear();
        notesTableView.getItems().clear();

        List<Student> students = classedao.getStudentsInClasse(selectedClass.getId());
        
        if (students != null && !students.isEmpty()) {
            System.out.println("Students found: " + students.size());
            studentComboBox.getItems().addAll(students);
            reportStudentComboBox.getItems().addAll(students);
            
            // Sélectionner le premier étudiant et charger ses notes
            studentComboBox.getSelectionModel().selectFirst();
            loadNotesForStudent();
        } else {
            System.out.println("No students found for this class");
        }
    }

    // ==================== NOTES MANAGEMENT ====================
    private void loadNotesForStudent() {
        Student student = studentComboBox.getValue();
        if (student == null) {
            notesTableView.getItems().clear();
            return;
        }

        notesTableView.getItems().clear();
        
        // ✅ Filtrer par étudiant ET matière du professeur UNIQUEMENT
        Long subjectId = currentProfessor.getSubject().getId();
        List<Note> notes = noteDAO.getNotesByStudentAndSubject(student.getId(), subjectId);
        
        System.out.println("=== LOADING NOTES ===");
        System.out.println("Professor: " + currentProfessor.getFullName());
        System.out.println("Professor Subject ID: " + subjectId + " (" + currentProfessor.getSubject().getName() + ")");
        System.out.println("Student: " + student.getFullName() + " (ID: " + student.getId() + ")");
        System.out.println("Notes trouvées: " + (notes != null ? notes.size() : 0));
        
        if (notes != null) {
            for (Note note : notes) {
                System.out.println("  - Grade: " + note.getValue() + ", Exam: " + note.getExamType() + ", Subject: " + note.getSubject().getName());
            }
        }
        
        if (notes != null && !notes.isEmpty()) {
            notesTableView.getItems().addAll(notes);
        }
    }

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
            dashboardPane.setVisible(false);
            clearAllFields();
        }
    }

    private void clearAllFields() {
        noteValueField.clear();
        reportTitleField.clear();
        reportContentArea.clear();
        classComboBox.getItems().clear();
        studentComboBox.getItems().clear();
        reportStudentComboBox.getItems().clear();
        notesTableView.getItems().clear();
        reportsTableView.getItems().clear();
        examTypeComboBox.getSelectionModel().clearSelection();
        noteStatusLabel.setText("");
        reportStatusLabel.setText("");
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