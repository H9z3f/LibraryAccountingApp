package com.library.client.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.client.App;
import com.library.client.controllers.UserRegisterController;
import com.library.client.responses.UserResponseBody;
import com.library.client.states.ApplicationState;
import com.library.client.utilities.HttpUtility;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserRegisterModel {
    private UserRegisterController registerController;
    private Stage stage;
    private ObjectMapper objectMapper;

    public UserRegisterModel() {
        registerController = UserRegisterController.getRegisterController();
        stage = ApplicationState.getStage();
        objectMapper = new ObjectMapper();
    }

    public void changeWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Login window");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public void register() throws Exception {
        String url = "/user/register";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("fullName", registerController.getFullNameTextField().getText());
        requestBody.put("email", registerController.getEmailTextField().getText());
        requestBody.put("password", registerController.getPasswordTextField().getText());
        String responseBody = HttpUtility.sendPostRequest(null, url, requestBody);
        UserResponseBody userResponseBody = objectMapper.readValue(responseBody, UserResponseBody.class);

        if (!userResponseBody.isSuccess()) {
            registerController.getErrorMessageLabel().setText(userResponseBody.getMessage());
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
