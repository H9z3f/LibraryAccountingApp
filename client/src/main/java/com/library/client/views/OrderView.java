package com.library.client.views;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.client.dtos.Order;
import com.library.client.dtos.User;
import com.library.client.models.LibraryModel;
import com.library.client.responses.OrderResponseBody;
import com.library.client.states.ApplicationState;
import com.library.client.utilities.HttpUtility;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class OrderView extends VBox {
    private final LibraryModel libraryModel;
    private final User user;
    private final Order order;

    public OrderView(LibraryModel libraryModel, User user, Order order) {
        this.libraryModel = libraryModel;
        this.user = user;
        this.order = order;

        initialize();
    }

    private void initialize() {
        this.setStyle("-fx-border-color: black; -fx-border-width: 1px; -fx-font: 16 'Baskerville Old Face'; -fx-padding: 10px; -fx-spacing: 5px; -fx-pref-width: 350px; -fx-background-color: white;");

        Label idLabel = new Label("#" + order.getId());
        idLabel.setStyle("-fx-underline: true;");

        Label recipientLabel = new Label("Recipient: " + order.getUser().getFullName());
        Label bookLabel = new Label("Book: " + order.getBook().getBookName() + " (" + order.getBook().getAuthor() + ")");

        Label statusLabel = new Label("Status: Not ready");
        statusLabel.setStyle("-fx-text-fill: red");

        if (order.getReady()) {
            statusLabel.setText("Status: Ready");
            statusLabel.setStyle("-fx-text-fill: orange");
        }
        if (order.getBeenIssued()) {
            statusLabel.setText("Status: Received");
            statusLabel.setStyle("-fx-text-fill: green");
        }

        this.getChildren().addAll(idLabel, recipientLabel, bookLabel, statusLabel);

        if (user.getRole().getId() == 1) {
            HBox buttonBox = new HBox();
            buttonBox.setStyle("-fx-spacing: 5px; -fx-pref-width: 330px; -fx-alignment: center-right;");

            Button readyButton = new Button("Ready");
            readyButton.setOnAction(actionEvent -> readyOrder());
            Button receivedButton = new Button("Received");
            receivedButton.setOnAction(actionEvent -> receivedOrder());

            if (order.getReady()) {
                readyButton.setDisable(true);
            }
            if (order.getBeenIssued()) {
                receivedButton.setDisable(true);
            }

            buttonBox.getChildren().addAll(readyButton, receivedButton);
            this.getChildren().addAll(buttonBox);
        }
    }

    private void readyOrder() {
        try {
            String url = "/order/" + order.getId();
            String responseBody = HttpUtility.sendPutRequest(ApplicationState.getUserResponseBody().getJwt(), url);
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

    private void receivedOrder() {
        try {
            String url = "/order/" + order.getId();
            String responseBody = HttpUtility.sendDeleteRequest(ApplicationState.getUserResponseBody().getJwt(), url);
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
}
