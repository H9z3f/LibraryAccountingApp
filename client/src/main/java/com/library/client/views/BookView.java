package com.library.client.views;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.client.dtos.Book;
import com.library.client.dtos.User;
import com.library.client.models.LibraryModel;
import com.library.client.responses.BookResponseBody;
import com.library.client.responses.OrderResponseBody;
import com.library.client.states.ApplicationState;
import com.library.client.utilities.HttpUtility;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class BookView extends VBox {
    private final LibraryModel libraryModel;
    private final User user;
    private final Book book;

    public BookView(LibraryModel libraryModel, User user, Book book) {
        this.libraryModel = libraryModel;
        this.user = user;
        this.book = book;

        initialize();
    }

    private void initialize() {
        this.setStyle("-fx-border-color: black; -fx-border-width: 1px; -fx-font: 16 'Baskerville Old Face'; -fx-padding: 10px; -fx-spacing: 5px; -fx-pref-width: 350px; -fx-background-color: white;");

        Label idLabel = new Label("#" + book.getId());
        idLabel.setStyle("-fx-underline: true;");

        Label authorLabel = new Label("Author: " + book.getAuthor());
        Label titleLabel = new Label("Title: " + book.getBookName());
        Label statusLabel = new Label("Status: " + book.getStatus().getStatusName());

        if (book.getStatus().getId() == 1) {
            statusLabel.setStyle("-fx-text-fill: green");
        } else if (book.getStatus().getId() == 2 || book.getStatus().getId() == 3) {
            statusLabel.setStyle("-fx-text-fill: orange");
        } else {
            statusLabel.setStyle("-fx-text-fill: red");
        }

        HBox buttonBox = new HBox();
        buttonBox.setStyle("-fx-spacing: 5px; -fx-pref-width: 330px; -fx-alignment: center-right;");

        if (user.getRole().getId() == 1) {
            Button returnButton = new Button("Return");
            returnButton.setOnAction(actionEvent -> returnBook());
            Button archiveButton = new Button("Archive");
            archiveButton.setOnAction(actionEvent -> archiveBook());

            if (book.getStatus().getId() == 1 || book.getStatus().getId() == 3) {
                returnButton.setDisable(true);
            }
            if (book.getStatus().getId() == 3 || book.getStatus().getId() == 4) {
                archiveButton.setDisable(true);
            }

            buttonBox.getChildren().addAll(returnButton, archiveButton);
        } else {
            Button orderButton = new Button("Order");
            orderButton.setOnAction(actionEvent -> orderBook());

            buttonBox.getChildren().addAll(orderButton);
        }

        this.getChildren().addAll(idLabel, authorLabel, titleLabel, statusLabel, buttonBox);
    }

    private void orderBook() {
        try {
            String url = "/order";
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("bookId", String.valueOf(book.getId()));
            String responseBody = HttpUtility.sendPostRequest(ApplicationState.getUserResponseBody().getJwt(), url, requestBody);
            ObjectMapper objectMapper = new ObjectMapper();
            OrderResponseBody orderResponseBody = objectMapper.readValue(responseBody, OrderResponseBody.class);

            if (!orderResponseBody.isSuccess()) {
                return;
            }

            libraryModel.researchBooks();
            libraryModel.researchOrders();
        } catch (Exception e) {
            return;
        }
    }

    private void returnBook() {
        try {
            String url = "/book/" + book.getId();
            String responseBody = HttpUtility.sendPutRequest(ApplicationState.getUserResponseBody().getJwt(), url);
            ObjectMapper objectMapper = new ObjectMapper();
            BookResponseBody bookResponseBody = objectMapper.readValue(responseBody, BookResponseBody.class);

            if (!bookResponseBody.isSuccess()) {
                return;
            }

            libraryModel.researchBooks();
            libraryModel.researchOrders();
        } catch (Exception e) {
            return;
        }
    }

    private void archiveBook() {
        try {
            String url = "/book/" + book.getId();
            String responseBody = HttpUtility.sendDeleteRequest(ApplicationState.getUserResponseBody().getJwt(), url);
            ObjectMapper objectMapper = new ObjectMapper();
            BookResponseBody bookResponseBody = objectMapper.readValue(responseBody, BookResponseBody.class);

            if (!bookResponseBody.isSuccess()) {
                return;
            }

            libraryModel.researchBooks();
            libraryModel.researchOrders();
        } catch (Exception e) {
            return;
        }
    }
}
