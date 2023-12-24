module com.library.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;


    exports com.library.client;
    opens com.library.client to javafx.fxml;
    exports com.library.client.controllers;
    opens com.library.client.controllers to javafx.fxml;
    exports com.library.client.dtos;
    opens com.library.client.dtos to com.fasterxml.jackson.databind;
    exports com.library.client.responses;
    opens com.library.client.responses to com.fasterxml.jackson.databind;
}
