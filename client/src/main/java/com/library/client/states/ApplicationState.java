package com.library.client.states;

import com.library.client.responses.UserResponseBody;
import javafx.stage.Stage;

public class ApplicationState {
    private static Stage stage;
    private static UserResponseBody userResponseBody;

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        ApplicationState.stage = stage;
    }

    public static UserResponseBody getUserResponseBody() {
        return userResponseBody;
    }

    public static void setUserResponseBody(UserResponseBody userResponseBody) {
        ApplicationState.userResponseBody = userResponseBody;
    }
}
