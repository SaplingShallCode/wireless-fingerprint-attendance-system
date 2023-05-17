package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utility.Const;

/**
 * The LoginWindow is a nested class that will appear during the initialization
 * phase of the program. it will ask the user what host and port should the
 * server be bound to.
 */
public class LoginWindow {
    private int port;
    private String host;
    private final Stage login_stage;
    private boolean host_is_valid = true;
    private boolean port_is_valid = true;
    private boolean willExitApp;

    // components
    Label host_label;
    TextField host_textfield;
    Label port_label;
    TextField port_textfield;
    Button enter_button;


    /**
     * Instantiate a LoginWindow object.
     * @param stage the stage object. should be undecorated.
     */
    LoginWindow (Stage stage) {
        login_stage = stage;
        login_stage.setAlwaysOnTop(true);
        login_stage.setResizable(false);
        for (Const.Icons i : Const.Icons.values()) {
            login_stage.getIcons().add(i.getIconImage());
        }
    }


    /**
     * Initialize the UI of the login window.
     */
    public void initUI() {
        // ----- Row 1 ----- //
        HBox row1 = new HBox();
        row1.setAlignment(Pos.CENTER);

        host_label = new Label("Bind to host:");
        host_label.setPrefWidth(Const.LoginWindowSizes.LABEL_WIDTH.getValue());

        host_textfield = new TextField("0.0.0.0");
        host_textfield.setPromptText("Default: 0.0.0.0"); // placeholder text
        host_textfield.setPrefWidth(Const.LoginWindowSizes.TEXTFIELD_WIDTH.getValue());
        host_textfield.textProperty().addListener((observable, old_value, new_value) -> {
            host_is_valid = host_textfield.getText().matches(
                    "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$"
            );
            changeButtonState();
        });
        row1.getChildren().addAll(host_label, host_textfield);

        // ----- Row 2 ----- //
        HBox row2 = new HBox();
        row2.setAlignment(Pos.CENTER);

        port_label = new Label("Bind to port: ");
        port_label.setPrefWidth(Const.LoginWindowSizes.LABEL_WIDTH.getValue());

        port_textfield = new TextField("62609");
        port_textfield.setPromptText("Default: 62609"); // placeholder text
        port_textfield.setTooltip(new Tooltip("Valid port range is between 49152 to 65535."));
        port_textfield.setPrefWidth(Const.LoginWindowSizes.TEXTFIELD_WIDTH.getValue());
        port_textfield.textProperty().addListener((observable, old_value, new_value) -> {
            port_is_valid = port_textfield.getText().matches(
                    "^(49[1-9][5-9][2-9]|"      +
                            "5[0-9][0-9][0-9][0-9]|"  +
                            "6[0-4][0-9]{3}|"         +
                            "65[0-4][0-9]{2}|"        +
                            "655[0-2][0-9]|"          +
                            "6553[0-5])$"
            );
            changeButtonState();
        });
        row2.getChildren().addAll(port_label, port_textfield);

        // ----- Row 3 ----- //
        HBox row3 = new HBox();
        row3.setAlignment(Pos.CENTER);

        enter_button = new Button("Enter");
        enter_button.setMaxWidth(Double.MAX_VALUE);
        row3.getChildren().addAll(enter_button);
        enter_button.setOnAction(e -> {
            // Assign the text inputs from the text fields to the respective variables.
            host = host_textfield.getText();
            port = Integer.parseInt(port_textfield.getText());
            willExitApp = false;
            closeWindow();
        });

        HBox.setHgrow(enter_button, Priority.ALWAYS);

        // ----- Layout ----- //
        BorderPane root = new BorderPane();
        VBox semi_root = new VBox(10);
        root.setCenter(semi_root);
        semi_root.setAlignment(Pos.CENTER);
        semi_root.getChildren().addAll(
                row1,
                row2,
                row3
        );
        BorderPane.setMargin(semi_root, new Insets(Const.WindowSizes.BORDERPANE_MARGIN.getValue()));

        // ----- Scene ----- //
        Scene scene = new Scene(root); // set the main layout of the scene.
        login_stage.setWidth(Const.LoginWindowSizes.PRIMARY_WIDTH.getValue());
        login_stage.setHeight(Const.LoginWindowSizes.PRIMARY_HEIGHT.getValue());
        login_stage.setTitle(Const.StringValues.LOGIN_WINDOW_TITLE.getValue());
        login_stage.setScene(scene); // set the main scene.
    }


    /**
     * Changes the button state.
     * @implNote this method is called after checking the host and port inputs.
     */
    public void changeButtonState() {
        enter_button.setDisable(true);
        if (host_is_valid && port_is_valid) {
            enter_button.setDisable(false);
        }
    }


    /**
     * Handle the LoginWindow close event.
     */
    public void handleClose() {
        login_stage.setOnCloseRequest(event ->
                willExitApp = true
        );
    }


    /**
     * Get the host input from the text field.
     * @return the host to be bound to.
     */
    public String getHost() {
        return host;
    }


    /**
     * Get the port input from the text field.
     * @return the port to be bound to
     */
    public int getPort() {
        return port;
    }


    /**
     * Shows the window and block the thread until window
     * is closed.
     */
    public void showAndWait() {
        login_stage.showAndWait();
    }


    /**
     * Closes the window/stage.
     * @implNote Only used by the enter button.
     */
    public void closeWindow() {
        login_stage.close();
    }


    /**
     * Get the boolean value of the user's exit choice.
     * @return True if app will exit.
     * @implNote The return value will determine if the main window will
     * show or just skip to closing the application.
     */
    public boolean getWillExitApp() {
        return willExitApp;
    }
}