package com.library.client.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.client.App;
import com.library.client.controllers.LibraryController;
import com.library.client.dtos.Book;
import com.library.client.dtos.Order;
import com.library.client.responses.BookResponseBody;
import com.library.client.responses.MultipleBookResponseBody;
import com.library.client.responses.MultipleOrderResponseBody;
import com.library.client.responses.UserResponseBody;
import com.library.client.states.ApplicationState;
import com.library.client.utilities.HttpUtility;
import com.library.client.views.BookView;
import com.library.client.views.OrderView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class LibraryModel {
    private LibraryController libraryController;
    private Stage stage;
    private Stage secondaryStage;
    private UserResponseBody userResponseBody;
    private ObjectMapper objectMapper;

    public LibraryModel() {
        libraryController = LibraryController.getLibraryController();
        stage = ApplicationState.getStage();
        userResponseBody = ApplicationState.getUserResponseBody();
        objectMapper = new ObjectMapper();
    }

    public void initialize() {
        libraryController.getSystemRoleField().setText("System role: " + userResponseBody.getUser().getRole().getRoleName());
        libraryController.getFullNameField().setText(userResponseBody.getUser().getFullName());

        displayBooks();
        displayOrders();
    }

    public void logOut() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Login window");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public void researchBooks() throws Exception {
        String url = "/book";
        String responseBody = HttpUtility.sendGetRequest(userResponseBody.getJwt(), url);
        MultipleBookResponseBody multipleBookResponseBody = objectMapper.readValue(responseBody, MultipleBookResponseBody.class);

        if (!multipleBookResponseBody.isSuccess()) {
            return;
        }

        userResponseBody.setBooks(multipleBookResponseBody.getBooks());
        displayBooks();
    }

    public void researchOrders() throws Exception {
        String url = "/order";
        String responseBody = HttpUtility.sendGetRequest(userResponseBody.getJwt(), url);
        MultipleOrderResponseBody multipleOrderResponseBody = objectMapper.readValue(responseBody, MultipleOrderResponseBody.class);

        if (!multipleOrderResponseBody.isSuccess()) {
            return;
        }

        userResponseBody.setOrders(multipleOrderResponseBody.getOrders());
        displayOrders();
    }

    public void searchBook() throws Exception {
        researchBooks();

        String id = libraryController.getBookIdTextField().getText();
        Integer bookId = getId(id);
        if (bookId == null) {
            userResponseBody.setBooks(new ArrayList<>());
            displayBooks();

            return;
        }

        List<Book> books = new ArrayList<>();
        for (Book book : userResponseBody.getBooks()) {
            if (Objects.equals(book.getId(), bookId)) {
                books.add(book);

                break;
            }
        }

        userResponseBody.setBooks(books);
        displayBooks();
    }

    public void searchOrder() throws Exception {
        researchOrders();

        String id = libraryController.getOrderIdTextField().getText();
        Integer orderId = getId(id);
        if (orderId == null) {
            userResponseBody.setOrders(new ArrayList<>());
            displayOrders();

            return;
        }

        List<Order> orders = new ArrayList<>();
        for (Order order : userResponseBody.getOrders()) {
            if (Objects.equals(order.getId(), orderId)) {
                orders.add(order);

                break;
            }
        }

        userResponseBody.setOrders(orders);
        displayOrders();
    }

    private void clearBooks() {
        libraryController.getBookListField().getChildren().clear();

        if (userResponseBody.getUser().getRole().getId() == 1) {
            Button addBookButton = new Button("Add new book");
            addBookButton.setStyle("-fx-font: 16 'Baskerville Old Face'; -fx-pref-width: 350px;");
            addBookButton.setOnAction(actionEvent -> createBookCreationWindow());

            libraryController.getBookListField().getChildren().addAll(addBookButton);
        }
    }

    public void displayBooks() {
        clearBooks();

        if (userResponseBody.getBooks().size() == 0) {
            Label emptyLabel = new Label("List of books is currently empty");
            emptyLabel.setStyle("-fx-font: 16 'Baskerville Old Face'; -fx-pref-width: 350px;");

            libraryController.getBookListField().getChildren().addAll(emptyLabel);

            return;
        }

        for (Book book : userResponseBody.getBooks()) {
            libraryController.getBookListField().getChildren().addAll(new BookView(this, userResponseBody.getUser(), book));
        }
    }

    private void clearOrders() {
        libraryController.getOrderListField().getChildren().clear();
    }

    public void displayOrders() {
        clearOrders();

        if (userResponseBody.getOrders().size() == 0) {
            Label emptyLabel = new Label("List of orders is currently empty");
            emptyLabel.setStyle("-fx-font: 16 'Baskerville Old Face'; -fx-pref-width: 350px;");

            libraryController.getOrderListField().getChildren().addAll(emptyLabel);

            return;
        }

        for (Order order : userResponseBody.getOrders()) {
            libraryController.getOrderListField().getChildren().addAll(new OrderView(this, userResponseBody.getUser(), order));
        }
    }

    private void createBookCreationWindow() {
        secondaryStage = new Stage();
        secondaryStage.initModality(Modality.APPLICATION_MODAL);
        secondaryStage.initOwner(stage);
        secondaryStage.setTitle("Book creation window");

        Label label = new Label("Fill out the form");
        label.setStyle("-fx-font: 18 'Baskerville Old Face'; -fx-pref-width: 300px; -fx-alignment: center;");

        Label authorLabel = new Label("Enter author:");
        authorLabel.setStyle("-fx-font: 16 'Baskerville Old Face';");

        TextField authorTextField = new TextField();
        authorTextField.setStyle("-fx-font: 16 'Baskerville Old Face'; -fx-pref-width: 300px;");

        Label bookNameLabel = new Label("Enter book name:");
        bookNameLabel.setStyle("-fx-font: 16 'Baskerville Old Face';");

        TextField bookNameTextField = new TextField();
        bookNameTextField.setStyle("-fx-font: 16 'Baskerville Old Face'; -fx-pref-width: 300px;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-font: 16 'Baskerville Old Face'; -fx-text-fill: red;");

        Button submitButton = new Button("Add book");
        submitButton.setStyle("-fx-font: 16 'Baskerville Old Face'; -fx-pref-width: 300px;");
        submitButton.setOnAction(actionEvent -> {
            try {
                String url = "/book";
                Map<String, String> requestBody = new HashMap<>();
                requestBody.put("author", authorTextField.getText());
                requestBody.put("bookName", bookNameTextField.getText());
                String responseBody = HttpUtility.sendPostRequest(ApplicationState.getUserResponseBody().getJwt(), url, requestBody);
                ObjectMapper objectMapper = new ObjectMapper();
                BookResponseBody bookResponseBody = objectMapper.readValue(responseBody, BookResponseBody.class);

                if (!bookResponseBody.isSuccess()) {
                    errorLabel.setText(bookResponseBody.getMessage());
                    return;
                }

                destroyBookCreationWindow();
                researchBooks();
            } catch (Exception e) {
                errorLabel.setText(e.getMessage());
            }
        });

        VBox dialogVbox = new VBox();
        dialogVbox.setStyle("-fx-padding: 25px; -fx-spacing: 10px; -fx-pref-width: 350px;");
        dialogVbox.getChildren().addAll(label, authorLabel, authorTextField, bookNameLabel, bookNameTextField, errorLabel, submitButton);

        Scene dialogScene = new Scene(dialogVbox);

        secondaryStage.setScene(dialogScene);
        secondaryStage.showAndWait();
    }

    private void destroyBookCreationWindow() {
        secondaryStage.close();
    }

    private Integer getId(String id) {
        if (id.equals("") || !isNumber(id)) {
            return null;
        }

        return Integer.parseInt(id);
    }

    private boolean isNumber(String parameterId) {
        for (char c : parameterId.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return true;
    }
}
