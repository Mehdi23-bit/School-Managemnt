package org.school.controllers;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Node;

public class Navigation {
     public static void logout(Node node, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(Navigation.class.getResource(fxmlPath));
        Stage stage = (Stage) node.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
