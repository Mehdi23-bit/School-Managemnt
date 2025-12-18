package org.school.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.school.dao.*;
import org.school.entities.*;
import java.time.LocalDate;
import java.util.*;
import java.time.LocalDateTime;

public class AdminDashBoard {
    
    // ===== DAOs =====
    SubjectDAO subjectdao = new SubjectDAO();
    ClasseDAO classdao = new ClasseDAO();
    MajorDAO majorDAO = new MajorDAO();
    StudentDAO studentDAO = new StudentDAO();
    ProfessorDAO professorDAO = new ProfessorDAO();
    ReportDAO reportDAO=new ReportDAO(); 
    //=========================status
    @FXML
   private Label pendingCountLabel;
   @FXML
   private Label reviewedCountLabel;
   @FXML
   private Label resolvedCountLabel;
   
   
    
    // ===== Student TableView and Columns =====
    @FXML
    private TableView<Student> studentsTable;
    @FXML
    private TableColumn<Student, String> studentCneCol;
    @FXML
    private TableColumn<Student, String> studentFirstNameCol;
    @FXML
    private TableColumn<Student, String> studentLastNameCol;
    @FXML
    private TableColumn<Student, String> studentEmailCol;
    @FXML
    private TableColumn<Student, String> studentPhoneCol;
    @FXML
    private TableColumn<Student, String> studentClassCol;
    @FXML
    private TableColumn<Student, LocalDate> studentDobCol;
    
    // ===== Professor TableView and Columns =====
    @FXML
    private TableView<Professor> professorsTable;
    @FXML
    private TableColumn<Professor, String> profFirstNameCol;
    @FXML
    private TableColumn<Professor, String> profLastNameCol;
    @FXML
    private TableColumn<Professor, String> profEmailCol;
    @FXML
    private TableColumn<Professor, String> profSubjectCol;
    
    // ===== Class TableView and Columns =====
    @FXML
    private TableView<Classe> classesTable;
    @FXML
    private TableColumn<Classe, String> classNameCol;
    @FXML
    private TableColumn<Classe, Integer> classLevelCol;
    @FXML
    private TableColumn<Classe, Integer> classCapacityCol;
    @FXML
    private TableColumn<Classe, String> classMajorCol;
    
    // ===== Major TableView and Columns =====
    @FXML
    private TableView<Major> majorsTable;
    @FXML
    private TableColumn<Major, String> majorCodeCol;
    @FXML
    private TableColumn<Major, String> majorNameCol;
    @FXML
    private TableColumn<Major, String> majorDescriptionCol;
    @FXML
    private TableColumn<Major, Integer> majorDurationCol;
    
    // ===== Subject TableView and Columns =====
    @FXML
    private TableView<Subject> subjectsTable;
    @FXML
    private TableColumn<Subject, String> subjectNameCol;
    @FXML
    private TableColumn<Subject, Double> subjectCoefficientCol;

    // ===== Subject TableView and Columns =====
    @FXML
    private TableView<Report> reportsTable;
    @FXML
    private TableColumn<Report, String> reportStudentCol;
    @FXML
    private TableColumn<Report, String> reportProfessorCol;
    @FXML
    private TableColumn<Report, String> reportTitleCol;
    @FXML
    private TableColumn<Report, String> reportStatusCol;
    @FXML
    private TableColumn<Report, LocalDateTime> reportCreatedAtCol;
    // ===== Initialize =====
    @FXML
    public void initialize() {
        setupStudentTable();
        setupProfessorTable();
        setupClassTable();
        setupMajorTable();
        setupSubjectTable();
        setupReportTable();
        
        loadStudents();
        loadProfessors();
        loadClasses();
        loadMajors();
        loadSubjects();
        loadReports();
        Long pending=reportDAO.countPendingReports();
        pendingCountLabel.setText(String.valueOf(pending));
        reviewedCountLabel.setText(String.valueOf(reportDAO.countReportsByStatus("REVIEWED")));
        resolvedCountLabel.setText(String.valueOf(reportDAO.countReportsByStatus("RESOLVED")));;
    }
    
    // ===== Setup TableViews =====
    
    private void setupStudentTable() {
        studentCneCol.setCellValueFactory(new PropertyValueFactory<>("cne"));
        studentFirstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        studentLastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        studentEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        studentPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        studentDobCol.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        
        studentClassCol.setCellValueFactory(cellData -> {
            Classe classe = cellData.getValue().getClasse();
            return new javafx.beans.property.SimpleStringProperty(
                classe != null ? classe.getName() : "No Class"
            );
        });
    }
    
    private void setupProfessorTable() {
        profFirstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        profLastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        profEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        profSubjectCol.setCellValueFactory(cellData -> {
            Subject subject = cellData.getValue().getSubject();
            return new javafx.beans.property.SimpleStringProperty(
                subject != null ? subject.getName() : "No Subject"
            );
        });
    }
    
    private void setupClassTable() {
        classNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        classLevelCol.setCellValueFactory(new PropertyValueFactory<>("level"));
        classCapacityCol.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        
        classMajorCol.setCellValueFactory(cellData -> {
            Major major = cellData.getValue().getMajor();
            return new javafx.beans.property.SimpleStringProperty(
                major != null ? major.getName() : "No Major"
            );
        });
    }
    
    private void setupMajorTable() {
        majorCodeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        majorNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        majorDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        majorDurationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
    }
    
    private void setupSubjectTable() {
        subjectNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        subjectCoefficientCol.setCellValueFactory(new PropertyValueFactory<>("coefficient"));
    }
    private void setupReportTable() {
        reportStudentCol.setCellValueFactory(cellData -> {
            Student student = cellData.getValue().getStudent();
            return new javafx.beans.property.SimpleStringProperty(
                student != null ? student.getFullName() : "Unknown"
            );
        });
        
        reportProfessorCol.setCellValueFactory(cellData -> {
            Professor professor = cellData.getValue().getProfessor();
            return new javafx.beans.property.SimpleStringProperty(
                professor != null ? professor.getFullName() : "Unknown"
            );
        });
        
        reportTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        reportStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        reportCreatedAtCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        
        // Add custom cell factory for status column to show colors
        reportStatusCol.setCellFactory(column -> new TableCell<Report, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status) {
                        case "PENDING":
                            setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404;");
                            break;
                        case "REVIEWED":
                            setStyle("-fx-background-color: #d1ecf1; -fx-text-fill: #0c5460;");
                            break;
                        case "RESOLVED":
                            setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
    }
    
    
    // ===== Load Data Methods =====
    
    private void loadStudents() {
        try {
            List<Student> students = studentDAO.getAllStudents();
            ObservableList<Student> observableList = FXCollections.observableArrayList(students);
            studentsTable.setItems(observableList);
        } catch (Exception e) {
            showError("Error Loading Students", "Could not load students: " + e.getMessage());
        }
    }
    
    private void loadProfessors() {
        try {
            List<Professor> professors = professorDAO.getAllProfessors();
            ObservableList<Professor> observableList = FXCollections.observableArrayList(professors);
            professorsTable.setItems(observableList);
        } catch (Exception e) {
            showError("Error Loading Professors", "Could not load professors: " + e.getMessage());
        }
    }
    
    private void loadClasses() {
        try {
            List<Classe> classes = classdao.getAllClasses();
            ObservableList<Classe> observableList = FXCollections.observableArrayList(classes);
            classesTable.setItems(observableList);
        } catch (Exception e) {
            showError("Error Loading Classes", "Could not load classes: " + e.getMessage());
        }
    }
    
    private void loadMajors() {
        try {
            List<Major> majors = majorDAO.getAllMajors();
            ObservableList<Major> observableList = FXCollections.observableArrayList(majors);
            majorsTable.setItems(observableList);
        } catch (Exception e) {
            showError("Error Loading Majors", "Could not load majors: " + e.getMessage());
        }
    }
    
    private void loadSubjects() {
        try {
            List<Subject> subjects = subjectdao.getAllSubjects();
            ObservableList<Subject> observableList = FXCollections.observableArrayList(subjects);
            subjectsTable.setItems(observableList);
        } catch (Exception e) {
            showError("Error Loading Subjects", "Could not load subjects: " + e.getMessage());
        }
    }
    @FXML
    private void loadReports() {
        try {
            List<Report> reports = reportDAO.getPendingReports();
            if (reports == null) {
                reports = new ArrayList<>();
            }
            ObservableList<Report> observableList = FXCollections.observableArrayList(reports);
            reportsTable.setItems(observableList);
        } catch (Exception e) {
            showError("Error Loading Reports", "Could not load reports: " + e.getMessage());
        }
    }
    // ===== Click Handlers =====
    
    @FXML
    private void onStudentClick(MouseEvent event) {
        Student selectedStudent = studentsTable.getSelectionModel().getSelectedItem();
        
        if (selectedStudent != null && event.getClickCount() == 1) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Student Actions");
            alert.setHeaderText("What would you like to do?");
            alert.setContentText(selectedStudent.getFullName() + " - " + selectedStudent.getCne());
            
            ButtonType modifyButton = new ButtonType("Modify");
            ButtonType removeButton = new ButtonType("Remove");
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            
            alert.getButtonTypes().setAll(modifyButton, removeButton, cancelButton);
            
            Optional<ButtonType> result = alert.showAndWait();
            
            if (result.isPresent()) {
                if (result.get() == modifyButton) {
                    showModifyStudentDialog(selectedStudent);
                } else if (result.get() == removeButton) {
                    showRemoveStudentConfirmation(selectedStudent);
                }
            }
        }
    }
    
    @FXML
    private void onProfessorClick(MouseEvent event) {
        Professor selectedProfessor = professorsTable.getSelectionModel().getSelectedItem();
        
        if (selectedProfessor != null && event.getClickCount() == 1) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Professor Actions");
            alert.setHeaderText("What would you like to do?");
            alert.setContentText(selectedProfessor.getFullName());
            
            ButtonType modifyButton = new ButtonType("Modify");
            ButtonType removeButton = new ButtonType("Remove");
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            
            alert.getButtonTypes().setAll(modifyButton, removeButton, cancelButton);
            
            Optional<ButtonType> result = alert.showAndWait();
            
            if (result.isPresent()) {
                if (result.get() == modifyButton) {
                    showModifyProfessorDialog(selectedProfessor);
                } else if (result.get() == removeButton) {
                    showRemoveProfessorConfirmation(selectedProfessor);
                }
            }
        }
    }
    
    @FXML
    private void onClassClick(MouseEvent event) {
        Classe selectedClass = classesTable.getSelectionModel().getSelectedItem();
        
        if (selectedClass != null && event.getClickCount() == 1) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Class Actions");
            alert.setHeaderText("What would you like to do?");
            alert.setContentText(selectedClass.getName());
            
            ButtonType modifyButton = new ButtonType("Modify");
            ButtonType removeButton = new ButtonType("Remove");
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            
            alert.getButtonTypes().setAll(modifyButton, removeButton, cancelButton);
            
            Optional<ButtonType> result = alert.showAndWait();
            
            if (result.isPresent()) {
                if (result.get() == modifyButton) {
                    showModifyClassDialog(selectedClass);
                } else if (result.get() == removeButton) {
                    showRemoveClassConfirmation(selectedClass);
                }
            }
        }
    }
    
    @FXML
    private void onMajorClick(MouseEvent event) {
        Major selectedMajor = majorsTable.getSelectionModel().getSelectedItem();
        
        if (selectedMajor != null && event.getClickCount() == 1) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Major Actions");
            alert.setHeaderText("What would you like to do?");
            alert.setContentText(selectedMajor.getName());
            
            ButtonType modifyButton = new ButtonType("Modify");
            ButtonType removeButton = new ButtonType("Remove");
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            
            alert.getButtonTypes().setAll(modifyButton, removeButton, cancelButton);
            
            Optional<ButtonType> result = alert.showAndWait();
            
            if (result.isPresent()) {
                if (result.get() == modifyButton) {
                    showModifyMajorDialog(selectedMajor);
                } else if (result.get() == removeButton) {
                    showRemoveMajorConfirmation(selectedMajor);
                }
            }
        }
    }
    
    @FXML
    private void onSubjectClick(MouseEvent event) {
        Subject selectedSubject = subjectsTable.getSelectionModel().getSelectedItem();
        
        if (selectedSubject != null && event.getClickCount() == 1) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Subject Actions");
            alert.setHeaderText("What would you like to do?");
            alert.setContentText(selectedSubject.getName());
            
            ButtonType modifyButton = new ButtonType("Modify");
            ButtonType removeButton = new ButtonType("Remove");
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            
            alert.getButtonTypes().setAll(modifyButton, removeButton, cancelButton);
            
            Optional<ButtonType> result = alert.showAndWait();
            
            if (result.isPresent()) {
                if (result.get() == modifyButton) {
                    showModifySubjectDialog(selectedSubject);
                } else if (result.get() == removeButton) {
                    showRemoveSubjectConfirmation(selectedSubject);
                }
            }
        }
    }
    @FXML
private void onReportClick(MouseEvent event) {
    Report selectedReport = reportsTable.getSelectionModel().getSelectedItem();
    
    if (selectedReport != null && event.getClickCount() == 1) {
        showReportDetailsDialog(selectedReport);
    }
}

// Add dialog to view and update report
private void showReportDetailsDialog(Report report) {
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Report Details");
    dialog.setHeaderText("Report from " + report.getProfessor().getFullName());
    
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));
    
    // Display fields (read-only)
    Label studentLabel = new Label(report.getStudent().getFullName());
    Label professorLabel = new Label(report.getProfessor().getFullName());
    Label titleLabel = new Label(report.getTitle());
    titleLabel.setWrapText(true);
    titleLabel.setMaxWidth(300);
    
    TextArea contentArea = new TextArea(report.getContent());
    contentArea.setEditable(false);
    contentArea.setWrapText(true);
    contentArea.setPrefRowCount(8);
    contentArea.setMaxWidth(300);
    
    Label createdAtLabel = new Label(report.getCreatedAt().toString());
    
    // Status ComboBox (editable)
    ComboBox<String> statusCombo = new ComboBox<>();
    statusCombo.getItems().addAll("PENDING", "REVIEWED", "RESOLVED");
    statusCombo.setValue(report.getStatus());
    
    grid.add(new Label("Student:"), 0, 0);
    grid.add(studentLabel, 1, 0);
    grid.add(new Label("Professor:"), 0, 1);
    grid.add(professorLabel, 1, 1);
    grid.add(new Label("Title:"), 0, 2);
    grid.add(titleLabel, 1, 2);
    grid.add(new Label("Content:"), 0, 3);
    grid.add(contentArea, 1, 3);
    grid.add(new Label("Created At:"), 0, 4);
    grid.add(createdAtLabel, 1, 4);
    grid.add(new Label("Status:"), 0, 5);
    grid.add(statusCombo, 1, 5);
    
    dialog.getDialogPane().setContent(grid);
    
    ButtonType updateButton = new ButtonType("Update Status", ButtonBar.ButtonData.OK_DONE);
    ButtonType deleteButton = new ButtonType("Delete Report", ButtonBar.ButtonData.OTHER);
    dialog.getDialogPane().getButtonTypes().addAll(updateButton, deleteButton, ButtonType.CLOSE);
    
    Optional<ButtonType> result = dialog.showAndWait();
    
    if (result.isPresent()) {
        if (result.get() == updateButton) {
            try {
                String newStatus = statusCombo.getValue();
                reportDAO.updateReportStatus(report.getId(), newStatus);
                loadReports();
                showSuccess("Report status updated to: " + newStatus);
            } catch (Exception e) {
                showError("Error", "Could not update report: " + e.getMessage());
            }
        } else if (result.get() == deleteButton) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Deletion");
            confirmAlert.setHeaderText("Delete Report");
            confirmAlert.setContentText("Are you sure you want to delete this report?");
            
            Optional<ButtonType> confirmResult = confirmAlert.showAndWait();
            if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
                try {
                    reportDAO.deleteReport(report.getId());
                    loadReports();
                    showSuccess("Report deleted successfully!");
                } catch (Exception e) {
                    showError("Error", "Could not delete report: " + e.getMessage());
                }
            }
        }
    }
}

    // ===== Add Methods =====
    
    @FXML
    private void onAddStudent(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Student");
        dialog.setHeaderText("Enter student information");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField cneField = new TextField();
        cneField.setPromptText("CNE");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        TextField passwordField = new TextField();
        passwordField.setPromptText("Password");
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        DatePicker dobPicker = new DatePicker();
        dobPicker.setPromptText("Date of Birth");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        
        ComboBox<Classe> classCombo = new ComboBox<>();
        List<Classe> classList = classdao.getAllClasses();
        classCombo.setItems(FXCollections.observableArrayList(classList));
        classCombo.setPromptText("Select Class");
        
        classCombo.setCellFactory(param -> new ListCell<Classe>() {
            @Override
            protected void updateItem(Classe classe, boolean empty) {
                super.updateItem(classe, empty);
                setText(empty || classe == null ? null : classe.getName());
            }
        });
        
        classCombo.setButtonCell(new ListCell<Classe>() {
            @Override
            protected void updateItem(Classe classe, boolean empty) {
                super.updateItem(classe, empty);
                setText(empty || classe == null ? null : classe.getName());
            }
        });
        
        grid.add(new Label("CNE:"), 0, 0);
        grid.add(cneField, 1, 0);
        grid.add(new Label("Username:"), 0, 1);
        grid.add(usernameField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(new Label("First Name:"), 0, 3);
        grid.add(firstNameField, 1, 3);
        grid.add(new Label("Last Name:"), 0, 4);
        grid.add(lastNameField, 1, 4);
        grid.add(new Label("Date of Birth:"), 0, 5);
        grid.add(dobPicker, 1, 5);
        grid.add(new Label("Phone:"), 0, 6);
        grid.add(phoneField, 1, 6);
        grid.add(new Label("Email:"), 0, 7);
        grid.add(emailField, 1, 7);
        grid.add(new Label("Class:"), 0, 8);
        grid.add(classCombo, 1, 8);
        
        dialog.getDialogPane().setContent(grid);
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);
        
        Optional<ButtonType> result = dialog.showAndWait();
        
        if (result.isPresent() && result.get() == addButton) {
            if (cneField.getText().isEmpty() || usernameField.getText().isEmpty() || 
                firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() || 
                emailField.getText().isEmpty()) {
                
                showError("Missing Information", "Please fill in all required fields!");
                return;
            }
            
            try {
                Student newStudent = new Student(
                    cneField.getText(),
                    usernameField.getText(),
                    passwordField.getText(),
                    firstNameField.getText(),
                    lastNameField.getText(),
                    dobPicker.getValue(),
                    phoneField.getText(),
                    emailField.getText()
                );
                
                if (classCombo.getValue() != null) {
                    newStudent.setClasse(classCombo.getValue());
                }
                
                studentDAO.saveStudent(newStudent);
                loadStudents();
                
                showSuccess("Student added successfully!");
            } catch (Exception e) {
                showError("Error", "Could not add student: " + e.getMessage());
            }
        }
    }
    
    @FXML
private void onAddProfessor(ActionEvent event) {
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Add New Professor");
    dialog.setHeaderText("Enter professor information");
    
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));
    
    TextField usernameField = new TextField();
    usernameField.setPromptText("Username");
    TextField passwordField = new TextField();
    passwordField.setPromptText("Password");
    TextField firstNameField = new TextField();
    firstNameField.setPromptText("First Name");
    TextField lastNameField = new TextField();
    lastNameField.setPromptText("Last Name");
    TextField emailField = new TextField();
    emailField.setPromptText("Email");
    
    ComboBox<Subject> subjectCombo = new ComboBox<>();
    List<Subject> subjectList = subjectdao.getAllSubjects();
    subjectCombo.setItems(FXCollections.observableArrayList(subjectList));
    subjectCombo.setPromptText("Select Subject");
    
    subjectCombo.setCellFactory(param -> new ListCell<Subject>() {
        @Override
        protected void updateItem(Subject subject, boolean empty) {
            super.updateItem(subject, empty);
            setText(empty || subject == null ? null : subject.getName());
        }
    });
    
    subjectCombo.setButtonCell(new ListCell<Subject>() {
        @Override
        protected void updateItem(Subject subject, boolean empty) {
            super.updateItem(subject, empty);
            setText(empty || subject == null ? null : subject.getName());
        }
    });
    
    ListView<Classe> classListView = new ListView<>();
    List<Classe> classList = classdao.getAllClasses();
    classListView.setItems(FXCollections.observableArrayList(classList));
    classListView.setPrefHeight(100);
    classListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    
    classListView.setCellFactory(param -> new ListCell<Classe>() {
        @Override
        protected void updateItem(Classe classe, boolean empty) {
            super.updateItem(classe, empty);
            setText(empty || classe == null ? null : classe.getName());
        }
    });
    
    Label classLabel = new Label("Classes:");
    Label classHint = new Label("(Hold Ctrl/Cmd to select multiple)");
    classHint.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
    
    grid.add(new Label("Username:"), 0, 0);
    grid.add(usernameField, 1, 0);
    grid.add(new Label("Password:"), 0, 1);
    grid.add(passwordField, 1, 1);
    grid.add(new Label("First Name:"), 0, 2);
    grid.add(firstNameField, 1, 2);
    grid.add(new Label("Last Name:"), 0, 3);
    grid.add(lastNameField, 1, 3);
    grid.add(new Label("Email:"), 0, 4);
    grid.add(emailField, 1, 4);
    grid.add(new Label("Subject:"), 0, 5);
    grid.add(subjectCombo, 1, 5);
    grid.add(classLabel, 0, 6);
    grid.add(classListView, 1, 6);
    grid.add(classHint, 1, 7);
    
    dialog.getDialogPane().setContent(grid);
    ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);
    
    Optional<ButtonType> result = dialog.showAndWait();
    
    if (result.isPresent() && result.get() == addButton) {
        if (usernameField.getText().isEmpty() || firstNameField.getText().isEmpty() || 
            lastNameField.getText().isEmpty() || emailField.getText().isEmpty()) {
            
            showError("Missing Information", "Please fill in all required fields!");
            return;
        }
        
        if (subjectCombo.getValue() == null) {
            showError("Missing Information", "Please select a subject!");
            return;
        }
        
        try {
            Subject selectedSubject = subjectCombo.getValue();
            ObservableList<Classe> selectedClasses = classListView.getSelectionModel().getSelectedItems();
            
            // ✅ VALIDATE: Check for conflicts with existing professors
            if (!selectedClasses.isEmpty()) {
                List<String> conflicts = new ArrayList<>();
                
                for (Classe classe : selectedClasses) {
                    if (professorDAO.isSubjectAlreadyTaughtInClasse(classe.getId(), selectedSubject.getId(), null)) {
                        Professor existingProf = professorDAO.getProfessorByClasseAndSubject(
                            classe.getId(), 
                            selectedSubject.getId()
                        );
                        conflicts.add("• " + classe.getName() + " → already taught by " + existingProf.getFullName());
                    }
                }
                
                if (!conflicts.isEmpty()) {
                    Alert conflictAlert = new Alert(Alert.AlertType.ERROR);
                    conflictAlert.setTitle("Assignment Conflict");
                    conflictAlert.setHeaderText("Cannot assign classes - Subject conflicts detected!");
                    conflictAlert.setContentText(
                        "The following classes already have a " + selectedSubject.getName() + " professor:\n\n" +
                        String.join("\n", conflicts) + "\n\n" +
                        "Each class can only have ONE professor per subject.\n" +
                        "Please deselect these classes or remove the existing assignments first."
                    );
                    conflictAlert.showAndWait();
                    return;
                }
            }
            
            // No conflicts - proceed with creation
            Professor newProfessor = new Professor(
                usernameField.getText(),
                passwordField.getText(),
                firstNameField.getText(),
                lastNameField.getText(),
                emailField.getText(),
                null
            );
            
            newProfessor.setSubject(selectedSubject);
            
            if (!selectedClasses.isEmpty()) {
                Set<Classe> classes = new HashSet<>(selectedClasses);
                newProfessor.setClasses(classes);
            }
            
            professorDAO.saveProfessor(newProfessor);
            loadProfessors();
            
            showSuccess("Professor added successfully!");
        } catch (Exception e) {
            showError("Error", "Could not add professor: " + e.getMessage());
        }
    }
}
    
    @FXML
    private void onAddClass(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Class");
        dialog.setHeaderText("Enter class information");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField();
        nameField.setPromptText("Class Name");
        TextField levelField = new TextField();
        levelField.setPromptText("Level (e.g., 1, 2, 3)");
        TextField capacityField = new TextField();
        capacityField.setPromptText("Capacity");
        
        ComboBox<Major> majorCombo = new ComboBox<>();
        List<Major> majorList = majorDAO.getAllMajors();
        majorCombo.setItems(FXCollections.observableArrayList(majorList));
        majorCombo.setPromptText("Select Major");
        
        majorCombo.setCellFactory(param -> new ListCell<Major>() {
            @Override
            protected void updateItem(Major major, boolean empty) {
                super.updateItem(major, empty);
                setText(empty || major == null ? null : major.getName());
            }
        });
        
        majorCombo.setButtonCell(new ListCell<Major>() {
            @Override
            protected void updateItem(Major major, boolean empty) {
                super.updateItem(major, empty);
                setText(empty || major == null ? null : major.getName());
            }
        });
        
        grid.add(new Label("Class Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Level:"), 0, 1);
        grid.add(levelField, 1, 1);
        grid.add(new Label("Capacity:"), 0, 2);
        grid.add(capacityField, 1, 2);
        grid.add(new Label("Major:"), 0, 3);
        grid.add(majorCombo, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);
        
        Optional<ButtonType> result = dialog.showAndWait();
        
        if (result.isPresent() && result.get() == addButton) {
            if (nameField.getText().isEmpty() || levelField.getText().isEmpty()) {
                showError("Missing Information", "Please fill in all required fields!");
                return;
            }
            
            try {
                Classe newClass = new Classe(
                    nameField.getText(),
                    levelField.getText(),
                    Integer.parseInt(capacityField.getText())
                );
                
                if (majorCombo.getValue() != null) {
                    newClass.setMajor(majorCombo.getValue());
                }
                
                classdao.saveClasse(newClass);
                loadClasses();
                
                showSuccess("Class added successfully!");
            } catch (Exception e) {
                showError("Error", "Could not add class: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void onAddMajor(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Major");
        dialog.setHeaderText("Enter major information");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField codeField = new TextField();
        codeField.setPromptText("Major Code (e.g., INFO, SM)");
        TextField nameField = new TextField();
        nameField.setPromptText("Major Name");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description");
        descriptionArea.setPrefRowCount(3);
        TextField durationField = new TextField();
        durationField.setPromptText("Duration (years)");
        durationField.setText("2");
        
        grid.add(new Label("Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Description:"), 0, 2);
        grid.add(descriptionArea, 1, 2);
        grid.add(new Label("Duration (years):"), 0, 3);
        grid.add(durationField, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);
        
        Optional<ButtonType> result = dialog.showAndWait();
        
        if (result.isPresent() && result.get() == addButton) {
            if (codeField.getText().isEmpty() || nameField.getText().isEmpty()) {
                showError("Missing Information", "Please fill in all required fields!");
                return;
            }
            
            try {
                Major newMajor = new Major(
                    codeField.getText(),
                    nameField.getText(),
                    Integer.parseInt(durationField.getText())
                );
                newMajor.setDescription(descriptionArea.getText());
                
                majorDAO.saveMajor(newMajor);
                loadMajors();
                
                showSuccess("Major added successfully!");
            } catch (Exception e) {
                showError("Error", "Could not add major: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void onAddSubject(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Subject");
        dialog.setHeaderText("Enter subject information");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField();
        nameField.setPromptText("Subject Name");
        TextField coefficientField = new TextField();
        coefficientField.setPromptText("Coefficient");
        coefficientField.setText("1.0");
        
        grid.add(new Label("Subject Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Coefficient:"), 0, 1);
        grid.add(coefficientField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);
        
        Optional<ButtonType> result = dialog.showAndWait();
        
        if (result.isPresent() && result.get() == addButton) {
            if (nameField.getText().isEmpty()) {
                showError("Missing Information", "Please fill in the subject name!");
                return;
            }
            
            try {
                Subject newSubject = new Subject(
                    nameField.getText(),
                    Double.parseDouble(coefficientField.getText())
                );
                
                subjectdao.saveSubject(newSubject);
                loadSubjects();
                
                showSuccess("Subject added successfully!");
            } catch (Exception e) {
                showError("Error", "Could not add subject: " + e.getMessage());
            }
        }
    }
    
    // ===== Modify Dialogs =====
    
    private void showModifyStudentDialog(Student student) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modify Student");
        dialog.setHeaderText("Edit student information");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField cneField = new TextField(student.getCne());
        TextField usernameField = new TextField(student.getUsername());
        TextField passwordField = new TextField(student.getPassword());
        TextField firstNameField = new TextField(student.getFirstName());
        TextField lastNameField = new TextField(student.getLastName());
        DatePicker dobPicker = new DatePicker(student.getDateOfBirth());
        TextField phoneField = new TextField(student.getPhone());
        TextField emailField = new TextField(student.getEmail());
        
        ComboBox<Classe> classCombo = new ComboBox<>();
        List<Classe> classList = classdao.getAllClasses();
        classCombo.setItems(FXCollections.observableArrayList(classList));
        classCombo.setValue(student.getClasse());
        
        classCombo.setCellFactory(param -> new ListCell<Classe>() {
            @Override
            protected void updateItem(Classe classe, boolean empty) {
                super.updateItem(classe, empty);
                setText(empty || classe == null ? null : classe.getName());
            }
        });
        
        classCombo.setButtonCell(new ListCell<Classe>() {
            @Override
            protected void updateItem(Classe classe, boolean empty) {
                super.updateItem(classe, empty);
                setText(empty || classe == null ? null : classe.getName());
            }
        });
        
        grid.add(new Label("CNE:"), 0, 0);
        grid.add(cneField, 1, 0);
        grid.add(new Label("Username:"), 0, 1);
        grid.add(usernameField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(new Label("First Name:"), 0, 3);
        grid.add(firstNameField, 1, 3);
        grid.add(new Label("Last Name:"), 0, 4);
        grid.add(lastNameField, 1, 4);
        grid.add(new Label("Date of Birth:"), 0, 5);
        grid.add(dobPicker, 1, 5);
        grid.add(new Label("Phone:"), 0, 6);
        grid.add(phoneField, 1, 6);
        grid.add(new Label("Email:"), 0, 7);
        grid.add(emailField, 1, 7);
        grid.add(new Label("Class:"), 0, 8);
        grid.add(classCombo, 1, 8);
        
        dialog.getDialogPane().setContent(grid);
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);
        
        Optional<ButtonType> result = dialog.showAndWait();
        
        if (result.isPresent() && result.get() == saveButton) {
            try {
                student.setCne(cneField.getText());
                student.setUsername(usernameField.getText());
                student.setPassword(passwordField.getText());
                student.setFirstName(firstNameField.getText());
                student.setLastName(lastNameField.getText());
                student.setDateOfBirth(dobPicker.getValue());
                student.setPhone(phoneField.getText());
                student.setEmail(emailField.getText());
                student.setClasse(classCombo.getValue());
                
                studentDAO.updateStudent(student);
                loadStudents();
                
                showSuccess("Student updated successfully!");
            } catch (Exception e) {
                showError("Error", "Could not update student: " + e.getMessage());
            }
        }
    }
    
    private void showModifyProfessorDialog(Professor professor) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modify Professor");
        dialog.setHeaderText("Edit professor information");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField usernameField = new TextField(professor.getUsername());
        TextField passwordField = new TextField(professor.getPassword());
        TextField firstNameField = new TextField(professor.getFirstName());
        TextField lastNameField = new TextField(professor.getLastName());
        TextField emailField = new TextField(professor.getEmail());
        
        ComboBox<Subject> subjectCombo = new ComboBox<>();
        List<Subject> subjectList = subjectdao.getAllSubjects();
        subjectCombo.setItems(FXCollections.observableArrayList(subjectList));
        subjectCombo.setValue(professor.getSubject());
        
        subjectCombo.setCellFactory(param -> new ListCell<Subject>() {
            @Override
            protected void updateItem(Subject subject, boolean empty) {
                super.updateItem(subject, empty);
                setText(empty || subject == null ? null : subject.getName());
            }
        });
        
        subjectCombo.setButtonCell(new ListCell<Subject>() {
            @Override
            protected void updateItem(Subject subject, boolean empty) {
                super.updateItem(subject, empty);
                setText(empty || subject == null ? null : subject.getName());
            }
        });
        
        // ✅ Add ListView for classes
        ListView<Classe> classListView = new ListView<>();
        List<Classe> classList = classdao.getAllClasses();
        classListView.setItems(FXCollections.observableArrayList(classList));
        classListView.setPrefHeight(100);
        classListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        // ✅ Pre-select current classes
        List<Classe> currentClasses = professorDAO.getClassesByProfessor(professor.getId());
        if (currentClasses != null) {
            for (Classe c : currentClasses) {
                for (Classe listClasse : classList) {
                    if (listClasse.getId().equals(c.getId())) {
                        classListView.getSelectionModel().select(listClasse);
                        break;
                    }
                }
            }
        }
        
        classListView.setCellFactory(param -> new ListCell<Classe>() {
            @Override
            protected void updateItem(Classe classe, boolean empty) {
                super.updateItem(classe, empty);
                setText(empty || classe == null ? null : classe.getName());
            }
        });
        
        Label classLabel = new Label("Classes:");
        Label classHint = new Label("(Hold Ctrl/Cmd to select multiple)");
        classHint.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
        
        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("First Name:"), 0, 2);
        grid.add(firstNameField, 1, 2);
        grid.add(new Label("Last Name:"), 0, 3);
        grid.add(lastNameField, 1, 3);
        grid.add(new Label("Email:"), 0, 4);
        grid.add(emailField, 1, 4);
        grid.add(new Label("Subject:"), 0, 5);
        grid.add(subjectCombo, 1, 5);
        grid.add(classLabel, 0, 6);
        grid.add(classListView, 1, 6);
        grid.add(classHint, 1, 7);
        
        dialog.getDialogPane().setContent(grid);
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);
        
        Optional<ButtonType> result = dialog.showAndWait();
        
        if (result.isPresent() && result.get() == saveButton) {
            try {
                Subject selectedSubject = subjectCombo.getValue();
                
                if (selectedSubject == null) {
                    showError("Missing Information", "Please select a subject!");
                    return;
                }
                
                ObservableList<Classe> selectedClasses = classListView.getSelectionModel().getSelectedItems();
                
                // ✅ VALIDATE: Check for conflicts (excluding current professor)
                if (!selectedClasses.isEmpty()) {
                    List<String> conflicts = new ArrayList<>();
                    
                    for (Classe classe : selectedClasses) {
                        if (professorDAO.isSubjectAlreadyTaughtInClasse(classe.getId(), selectedSubject.getId(), professor.getId())) {
                            Professor existingProf = professorDAO.getProfessorByClasseAndSubject(
                                classe.getId(), 
                                selectedSubject.getId()
                            );
                            conflicts.add("• " + classe.getName() + " → already taught by " + existingProf.getFullName());
                        }
                    }
                    
                    if (!conflicts.isEmpty()) {
                        Alert conflictAlert = new Alert(Alert.AlertType.ERROR);
                        conflictAlert.setTitle("Assignment Conflict");
                        conflictAlert.setHeaderText("Cannot update - Subject conflicts detected!");
                        conflictAlert.setContentText(
                            "The following classes already have a " + selectedSubject.getName() + " professor:\n\n" +
                            String.join("\n", conflicts) + "\n\n" +
                            "Each class can only have ONE professor per subject.\n" +
                            "Please deselect these classes or remove the existing assignments first."
                        );
                        conflictAlert.showAndWait();
                        return;
                    }
                }
                
                // No conflicts - proceed with update
                professor.setUsername(usernameField.getText());
                professor.setPassword(passwordField.getText());
                professor.setFirstName(firstNameField.getText());
                professor.setLastName(lastNameField.getText());
                professor.setEmail(emailField.getText());
                professor.setSubject(selectedSubject);
                
                // Update classes
                if (!selectedClasses.isEmpty()) {
                    Set<Classe> classes = new HashSet<>(selectedClasses);
                    professor.setClasses(classes);
                } else {
                    professor.setClasses(new HashSet<>());
                }
                
                professorDAO.updateProfessor(professor);
                loadProfessors();
                
                showSuccess("Professor updated successfully!");
            } catch (Exception e) {
                showError("Error", "Could not update professor: " + e.getMessage());
            }
        }
    }
    
    private void showModifyClassDialog(Classe classe) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modify Class");
        dialog.setHeaderText("Edit class information");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField(classe.getName());
        TextField levelField = new TextField(String.valueOf(classe.getLevel()));
        TextField capacityField = new TextField(String.valueOf(classe.getCapacity()));
        
        ComboBox<Major> majorCombo = new ComboBox<>();
        List<Major> majorList = majorDAO.getAllMajors();
        majorCombo.setItems(FXCollections.observableArrayList(majorList));
        majorCombo.setValue(classe.getMajor());
        
        majorCombo.setCellFactory(param -> new ListCell<Major>() {
            @Override
            protected void updateItem(Major major, boolean empty) {
                super.updateItem(major, empty);
                setText(empty || major == null ? null : major.getName());
            }
        });
        
        majorCombo.setButtonCell(new ListCell<Major>() {
            @Override
            protected void updateItem(Major major, boolean empty) {
                super.updateItem(major, empty);
                setText(empty || major == null ? null : major.getName());
            }
        });
        
        grid.add(new Label("Class Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Level:"), 0, 1);
        grid.add(levelField, 1, 1);
        grid.add(new Label("Capacity:"), 0, 2);
        grid.add(capacityField, 1, 2);
        grid.add(new Label("Major:"), 0, 3);
        grid.add(majorCombo, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);
        
        Optional<ButtonType> result = dialog.showAndWait();
        
        if (result.isPresent() && result.get() == saveButton) {
            try {
                classe.setName(nameField.getText());
                classe.setLevel(levelField.getText());
                classe.setCapacity(Integer.parseInt(capacityField.getText()));
                classe.setMajor(majorCombo.getValue());
                
                classdao.updateClasse(classe);
                loadClasses();
                
                showSuccess("Class updated successfully!");
            } catch (Exception e) {
                showError("Error", "Could not update class: " + e.getMessage());
            }
        }
    }
    
    private void showModifyMajorDialog(Major major) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modify Major");
        dialog.setHeaderText("Edit major information");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField codeField = new TextField(major.getCode());
        TextField nameField = new TextField(major.getName());
        TextArea descriptionArea = new TextArea(major.getDescription());
        descriptionArea.setPrefRowCount(3);
        TextField durationField = new TextField(String.valueOf(major.getDurationYears()));
        
        grid.add(new Label("Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Description:"), 0, 2);
        grid.add(descriptionArea, 1, 2);
        grid.add(new Label("Duration (years):"), 0, 3);
        grid.add(durationField, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);
        
        Optional<ButtonType> result = dialog.showAndWait();
        
        if (result.isPresent() && result.get() == saveButton) {
            try {
                major.setCode(codeField.getText());
                major.setName(nameField.getText());
                major.setDescription(descriptionArea.getText());
                major.setDurationYears(Integer.parseInt(durationField.getText()));
                
                majorDAO.updateMajor(major);
                loadMajors();
                
                showSuccess("Major updated successfully!");
            } catch (Exception e) {
                showError("Error", "Could not update major: " + e.getMessage());
            }
        }
    }
    
    private void showModifySubjectDialog(Subject subject) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modify Subject");
        dialog.setHeaderText("Edit subject information");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField(subject.getName());
        TextField coefficientField = new TextField(String.valueOf(subject.getCoefficient()));
        
        grid.add(new Label("Subject Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Coefficient:"), 0, 1);
        grid.add(coefficientField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);
        
        Optional<ButtonType> result = dialog.showAndWait();
        
        if (result.isPresent() && result.get() == saveButton) {
            try {
                subject.setName(nameField.getText());
                subject.setCoefficient(Double.parseDouble(coefficientField.getText()));
                
                subjectdao.updateSubject(subject);
                loadSubjects();
                
                showSuccess("Subject updated successfully!");
            } catch (Exception e) {
                showError("Error", "Could not update subject: " + e.getMessage());
            }
        }
    }
    
    // ===== Remove Confirmation Dialogs =====
    
    private void showRemoveStudentConfirmation(Student student) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Removal");
        alert.setHeaderText("Remove Student");
        alert.setContentText("Are you sure you want to remove " + student.getFullName() + "?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                studentDAO.deleteStudent(student.getId());
                loadStudents();
                showSuccess("Student removed successfully!");
            } catch (Exception e) {
                showError("Error", "Could not remove student: " + e.getMessage());
            }
        }
    }
    
    private void showRemoveProfessorConfirmation(Professor professor) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Removal");
        alert.setHeaderText("Remove Professor");
        alert.setContentText("Are you sure you want to remove " + professor.getFullName() + "?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                professorDAO.deleteProfessor(professor.getId());
                loadProfessors();
                showSuccess("Professor removed successfully!");
            } catch (Exception e) {
                showError("Error", "Could not remove professor: " + e.getMessage());
            }
        }
    }
    
    private void showRemoveClassConfirmation(Classe classe) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Removal");
        alert.setHeaderText("Remove Class");
        alert.setContentText("Are you sure you want to remove " + classe.getName() + "?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                classdao.deleteClasse(classe.getId());
                loadClasses();
                showSuccess("Class removed successfully!");
            } catch (Exception e) {
                showError("Error", "Could not remove class: " + e.getMessage());
            }
        }
    }
    
    private void showRemoveMajorConfirmation(Major major) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Removal");
        alert.setHeaderText("Remove Major");
        alert.setContentText("Are you sure you want to remove " + major.getName() + "?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                majorDAO.deleteMajor(major.getId());
                loadMajors();
                showSuccess("Major removed successfully!");
            } catch (Exception e) {
                showError("Error", "Could not remove major: " + e.getMessage());
            }
        }
    }
    
    private void showRemoveSubjectConfirmation(Subject subject) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Removal");
        alert.setHeaderText("Remove Subject");
        alert.setContentText("Are you sure you want to remove " + subject.getName() + "?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                subjectdao.deleteSubject(subject.getId());
                loadSubjects();
                showSuccess("Subject removed successfully!");
            } catch (Exception e) {
                showError("Error", "Could not remove subject: " + e.getMessage());
            }
        }
    }
    
    // ===== Utility Methods =====
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
  
    }
    @FXML
private ComboBox<String> reportFilterCombo;

private void setupReportFilter() {
    if (reportFilterCombo != null) {
        reportFilterCombo.getItems().addAll("All Reports", "PENDING", "REVIEWED", "RESOLVED");
        reportFilterCombo.setValue("All Reports");
        reportFilterCombo.setOnAction(e -> filterReports());
    }
}

private void filterReports() {
    try {
        String selectedFilter = reportFilterCombo.getValue();
        List<Report> reports;
        
        if ("All Reports".equals(selectedFilter)) {
            // You may need to add a getAllReports() method to ReportDAO
            reports = reportDAO.getPendingReports(); // Modify this
        } else {
            // You may need to add this method to ReportDAO
            reports = reportDAO.getPendingReports(); // Filter by status
        }
        
        if (reports == null) {
            reports = new ArrayList<>();
        }
        
        ObservableList<Report> observableList = FXCollections.observableArrayList(reports);
        reportsTable.setItems(observableList);
    } catch (Exception e) {
        showError("Error", "Could not filter reports: " + e.getMessage());
    }
}
}