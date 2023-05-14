package gui;

import javafx.application.Platform;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import java.io.IOException;
import java.util.ArrayList;
import core.CommandExecutor;
import core.ServerManager;
import utility.Const;
import utility.LogHelper;
import utility.LogTypes;


/**
 * The MainWindow class handles the user interface of the server.
 * The JavaFX framework is used to build the user interface.
 */
@SuppressWarnings({
        "InnerClassMayBeStatic",
        "FieldCanBeLocal",
        "unused"
})
public class MainWindow extends Application {
    private final ObservableList<String> commands_list = FXCollections.observableArrayList();
    private final ObservableList<String> clients_list = FXCollections.observableArrayList();
    private ServerManager server_manager;
    private LoginWindow login_window;
    private Stage primary_stage;

    // components
    private Label commands_listlabel;
    private ListView<String> commands_listview;
    private Label clients_listlabel;
    private ListView<String> clients_listview;
    private Label server_label;
    private Button start_server_button;
    private Button stop_server_button;
    private Label log_label;
    private TextArea log_view;
    private TextField command_field;
    private Button command_button;


    /**
     * Initialize all resources needed for the application.
     * @see Const.Commands for the commands being added.
     */
    @Override
    public void init() {
        Platform.setImplicitExit(true); // close app when all windows are closed.
        for (Const.Commands command: Const.Commands.values()) {
            commands_list.add(command.getSyntax() + " : " + command.getDescription());
        }
    }


    /**
     * The start method always runs after the init method.
     *
     * @param stage Provided by JavaFX application.
     * @see LoginWindow for the login window implementation.
     */
    @Override
    public void start(Stage stage) {
        login_window = new LoginWindow(new Stage());
        login_window.initUI();
        login_window.handleClose(); // implement the close event of the login window
        login_window.showAndWait(); // blocking method

        primary_stage = stage;
        initUI();
        // handle the MainWindow close event
        primary_stage.setOnCloseRequest(event -> {
            try {
                // if the server is not closed then remind user to close.
                // NOTE: if server_manager is null then a NullPointerException is thrown.
                if (!server_manager.isClosed()) {
                    event.consume();
                    sendToConsole(LogHelper.log(
                            "The server is still running. Please close it first before exiting the app.",
                            LogTypes.WARNING
                            ));
                    Alert alert_window = new Alert(Alert.AlertType.WARNING);
                    alert_window.setTitle("Warning");
                    alert_window.setHeaderText("Server is still open...");
                    alert_window.setContentText("Please close the server before exiting the app.");
                    alert_window.showAndWait();
                }
                else {
                    Platform.exit();
                }
            }
            catch (NullPointerException npe) {
                Platform.exit();
            }
        });
        // if the user clicks the 'X' button on the login window then this
        // if-block will be skipped. the user must click the 'Enter' button
        // to continue to the main app.
        if (!login_window.getWillExitApp()) {
            primary_stage.show();
        }
    }


    /**
     * Check if the server is closed.
     */
    @Override
    public void stop() {
        LogHelper.debugLog("Successfully close the app.");
    }


    /**
     * initialize the UI of the application.
     */
    private void initUI() {
        // ----- Column 1 ----- //
        VBox col1 = new VBox();

        commands_listlabel = new Label("List of available commands:");
        commands_listview = new ListView<>(commands_list);
        commands_listview.setFocusTraversable(false);

        clients_listlabel = new Label("Connected clients:");
        clients_listview = new ListView<>(clients_list);
        clients_listview.setFocusTraversable(false);

        HBox server_group = new HBox();
        server_label = new Label("Server");

        start_server_button = new Button("Start");
        start_server_button.setMaxWidth(Double.MAX_VALUE);
        start_server_button.setOnAction(this::start_server);

        stop_server_button = new Button("Stop");
        stop_server_button.setMaxWidth(Double.MAX_VALUE);
        stop_server_button.setDisable(true);
        stop_server_button.setOnAction(this::stop_server);

        server_group.getChildren().addAll(
                start_server_button,
                stop_server_button
        );

        col1.getChildren().addAll(
                commands_listlabel,
                commands_listview,
                clients_listlabel,
                clients_listview,
                server_label,
                server_group
        );

        HBox.setHgrow(start_server_button, Priority.ALWAYS);
        HBox.setHgrow(stop_server_button, Priority.ALWAYS);

        // ----- Column 2 ----- //
        VBox col2 = new VBox();

        log_label = new Label("Console");
        log_view = new TextArea();
        log_view.setMaxHeight(Double.MAX_VALUE);
        log_view.setFont(Font.font("Consolas"));
        log_view.setFocusTraversable(false);
        log_view.setEditable(false);
        log_view.appendText("Wireless-Fingerprint-Based-Attendance-Logger-Server by NameGroup.\n\n");

        HBox command_group = new HBox();
        command_field = new TextField();
        command_field.setMaxWidth(Double.MAX_VALUE);
        command_field.setOnKeyPressed( event -> {
            // when user presses the Enter button on keyboard
            if (event.getCode() == KeyCode.ENTER) {
                String input = command_field.getText();
                sendToConsole(LogHelper.log(input, LogTypes.CONSOLE));
                CommandExecutor.execute(this, input);
                command_field.clear();
            }
        });

        command_button = new Button("Enter");
        command_button.setOnAction( event -> {
            String input = command_field.getText();
            sendToConsole(LogHelper.log(input, LogTypes.CONSOLE));
            CommandExecutor.execute(this, input);
            command_field.clear();
        });

        command_group.getChildren().addAll(
                command_field,
                command_button
        );
        col2.getChildren().addAll(
                log_label,
                log_view,
                command_group
        );

        VBox.setVgrow(log_view, Priority.ALWAYS);
        HBox.setHgrow(command_field, Priority.ALWAYS);

        // ----- Layout ----- //
        BorderPane root = new BorderPane();
        root.setLeft(col1);
        root.setCenter(col2);

        BorderPane.setMargin(col1, new Insets(Const.WindowSizes.BORDERPANE_MARGIN.getValue()));
        BorderPane.setMargin(col2, new Insets(Const.WindowSizes.BORDERPANE_MARGIN.getValue()));

        // ----- Stage and Scene ----- //
        Scene scene = new Scene(root);
        command_field.requestFocus();
        scene.getStylesheets().add(Const.StringValues.STYLESHEET_PATH.getValue());

        primary_stage.setHeight(Const.WindowSizes.MIN_HEIGHT.getValue());
        primary_stage.setWidth(Const.WindowSizes.MIN_WIDTH.getValue());
        primary_stage.setMinHeight(Const.WindowSizes.MIN_HEIGHT.getValue());
        primary_stage.setMinWidth(Const.WindowSizes.MIN_WIDTH.getValue());
        primary_stage.setTitle(Const.StringValues.WINDOW_TITLE.getValue());
        primary_stage.setScene(scene);
    }


    /**
     * Calls the ServerManager to start the server.
     * @param event the event fired by the app.
     * @see ServerManager for its methods.
     */
    public void start_server(ActionEvent event) {
        try {
            server_manager = new ServerManager(
                    this,
                    login_window.getHost(),
                    login_window.getPort()
            );
            new Thread(server_manager).start();
        }
        catch (IOException ioe) {
            sendToConsole(LogHelper.log(
                    "Error opening socket. The server may already be running in another process."
                    , LogTypes.ERROR
                    )
            );
        }
        catch (IllegalArgumentException iae) {
            sendToConsole(LogHelper.log(
                    "Invalid hostname or port. The hostname is either null or the port is out of range.",
                    LogTypes.ERROR
                    )
            );
        }
        start_server_button.setDisable(true);
        stop_server_button.setDisable(false);
    }

    /**
     * Calls the ServerManager to stop the server.
     * @param event the event fired by the app.
     * @see ServerManager for its methods.
     */
    public void stop_server(ActionEvent event) {
        try {
            if (server_manager == null) {
                sendToConsole(LogHelper.log("Server is null. Start the server first.", LogTypes.ERROR));
                throw new IOException();
            }
            server_manager.stopServer();
        }
        catch (IOException ioe) {
            sendToConsole(LogHelper.log("Error when closing server.", LogTypes.ERROR));
        }
        stop_server_button.setDisable(true);
        start_server_button.setDisable(false);
    }


    public ServerManager getServerManager() {
        return server_manager;
    }


    /**
     * Append text to the console. This method is run in a thread to
     * avoid concurrency errors.
     *
     * @param text text to be appended.
     * @implNote send to console's text parameter should be returned by
     * LogHelper's log function.
     * @see LogHelper for console logging.
     */
    public void sendToConsole(String text) {
        if (text != null) {
            Platform.runLater(
                    () -> log_view.appendText(text + "\n")
            );
        }
    }


    /**
     * Update the list of clients that connected to the server.
     * @param fsclients the list of clients to be displayed.
     * @see ServerManager for the list of clients in the fsclients array list.
     */
    public void updateClientsList(ArrayList<ServerManager.FSClient> fsclients) {
        Platform.runLater(
                () -> {
                    clients_list.clear();
                    for (ServerManager.FSClient client : fsclients) {
                        clients_list.add(client.getClientName());
                    }
                });
    }


    /**
     * The LoginWindow is a nested class that will appear during the initialization
     * phase of the program. it will ask the user what host and port should the
     * server be bound to.
     */
    private class LoginWindow {
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


    /**
     * Launch the application.
     *
     * @implNote Refer to the Launcher class when trying to run the program.
     * @param args terminal arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
