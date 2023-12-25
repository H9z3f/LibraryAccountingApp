package com.library.client.controllers;

import com.library.client.models.LibraryModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class LibraryController {
    private static LibraryController libraryController;
    private LibraryModel libraryModel;

    @FXML
    private TextField bookIdTextField;

    @FXML
    private VBox bookListField;

    @FXML
    private Label fullNameField;

    @FXML
    private TextField orderIdTextField;

    @FXML
    private VBox orderListField;

    @FXML
    private Label systemRoleField;

    public static LibraryController getLibraryController() {
        return libraryController;
    }

    @FXML
    void logOut(ActionEvent event) throws IOException {
        libraryModel.logOut();
    }

    @FXML
    void researchBooks(ActionEvent event) throws Exception {
        libraryModel.researchBooks();
    }

    @FXML
    void researchOrders(ActionEvent event) throws Exception {
        libraryModel.researchOrders();
    }

    @FXML
    void searchBook(ActionEvent event) throws Exception {
        libraryModel.searchBook();
    }

    @FXML
    void searchOrder(ActionEvent event) throws Exception {
        libraryModel.searchOrder();
    }

    @FXML
    void initialize() {
        libraryController = this;
        libraryModel = new LibraryModel();
        libraryModel.initialize();
    }

    public TextField getBookIdTextField() {
        return bookIdTextField;
    }

    public void setBookIdTextField(TextField bookIdTextField) {
        this.bookIdTextField = bookIdTextField;
    }

    public VBox getBookListField() {
        return bookListField;
    }

    public void setBookListField(VBox bookListField) {
        this.bookListField = bookListField;
    }

    public Label getFullNameField() {
        return fullNameField;
    }

    public void setFullNameField(Label fullNameField) {
        this.fullNameField = fullNameField;
    }

    public TextField getOrderIdTextField() {
        return orderIdTextField;
    }

    public void setOrderIdTextField(TextField orderIdTextField) {
        this.orderIdTextField = orderIdTextField;
    }

    public VBox getOrderListField() {
        return orderListField;
    }

    public void setOrderListField(VBox orderListField) {
        this.orderListField = orderListField;
    }

    public Label getSystemRoleField() {
        return systemRoleField;
    }

    public void setSystemRoleField(Label systemRoleField) {
        this.systemRoleField = systemRoleField;
    }
}
