package com.library.client.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.client.App;
import com.library.client.controllers.LibraryController;
import com.library.client.responses.UserResponseBody;
import com.library.client.states.ApplicationState;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LibraryModel {
    private LibraryController libraryController;
    private Stage stage;
    private UserResponseBody userResponseBody;
    private ObjectMapper objectMapper;

    public LibraryModel() {
        libraryController = LibraryController.getLibraryController();
        stage = ApplicationState.getStage();
        userResponseBody = ApplicationState.getUserResponseBody();
        objectMapper = new ObjectMapper();
    }

    public void initialize() {

    }

    public void logOut() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Login window");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public void researchBooks() {

    }

    public void researchOrders() {

    }

    public void searchBook() {

    }

    public void searchOrder() {

    }

    private void clearBooks() {

    }

    public void displayBooks() {
        clearBooks();
    }

    private void clearOrders() {

    }

    public void displayOrders() {
        clearOrders();
    }
}
