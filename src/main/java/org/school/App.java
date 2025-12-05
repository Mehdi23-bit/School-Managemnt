package org.school;

//hibernate imports
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.school.entities.Student;

//javafx import
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;

public class App {

//     @Override 
//     public void start(Stage primaryStage){
// Label label = new Label("Hello JavaFX");
// Scene scene = new Scene(label, 400, 300);
// primaryStage.setScene(scene);
// primaryStage.setTitle("School Management");
// primaryStage.show();

//     }
    public static void main(String[] args) {
        // Hibernate setup
        
        SessionFactory factory = new Configuration()
                .configure()
                .addAnnotatedClass(Student.class)
                .buildSessionFactory();

        // Open session
        Session session = factory.getCurrentSession();

        try {
            // Create a student object
            Student s = new Student(1, "John Doe");

            // Start transaction
            session.beginTransaction();

            // Save the student
            session.save(s);

            // Commit transaction
            session.getTransaction().commit();

            System.out.println("Student saved successfully!");
        } finally {
            factory.close();
        }
       
    }
}
