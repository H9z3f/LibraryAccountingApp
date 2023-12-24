package com.library.client.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.client.App;
import com.library.client.controllers.UserLoginController;
import com.library.client.responses.UserResponseBody;
import com.library.client.states.ApplicationState;
import com.library.client.utilities.HttpUtility;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserLoginModel {
    private UserLoginController loginController;
    private Stage stage;
    private ObjectMapper objectMapper;

    public UserLoginModel() {
        loginController = UserLoginController.getLoginController();
        stage = ApplicationState.getStage();
        objectMapper = new ObjectMapper();
    }

    public void changeWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("register-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Registration window");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public void login() throws Exception {
        String url = "/user/login";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", loginController.getEmailTextField().getText());
        requestBody.put("password", loginController.getPasswordTextField().getText());
        String responseBody = HttpUtility.sendPostRequest(null, url, requestBody);
        UserResponseBody userResponseBody = objectMapper.readValue(responseBody, UserResponseBody.class);

        if (!userResponseBody.isSuccess()) {
            loginController.getErrorMessageLabel().setText(userResponseBody.getMessage());
            return;
        }

        ApplicationState.setUserResponseBody(userResponseBody);

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("library-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Library window");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
