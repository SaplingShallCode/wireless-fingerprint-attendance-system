package gui;

import javafx.application.Platform;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.Duration;
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
        clients_listview.setCellFactory(param -> new ClientCell()); // See nested ClientCell class below.
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


    public EnrollWindow getEnrollWindow() {
        return new EnrollWindow(new Stage());
    }


    /**
     * The ClientCell is a custom cell used by the clients_list_view object
     * which adds two buttons. One for enrolling and disconnecting from a
     * client.
     */
    private class ClientCell extends ListCell<String> {
        private final Label item_name;
        private final Button enroll_button;
        private final Button disconnect_button;
        private final GridPane grid;
        private final Tooltip enroll_tooltip;
        private final Tooltip disconnect_tooltip;

        public ClientCell() {
            super();

            enroll_tooltip = new Tooltip("Enroll");
            enroll_tooltip.setShowDelay(Duration.ZERO);
            disconnect_tooltip = new Tooltip("Disconnect");
            disconnect_tooltip.setShowDelay(Duration.ZERO);

            enroll_button = new Button("E");
            enroll_button.setOnAction(this::executeEnroll);
            enroll_button.setTooltip(enroll_tooltip);
            enroll_button.setMaxWidth(Double.MAX_VALUE);

            disconnect_button = new Button("X");
            disconnect_button.setTooltip(disconnect_tooltip);
            disconnect_button.setMaxWidth(Double.MAX_VALUE);

            item_name = new Label();
            item_name.setMaxWidth(Double.MAX_VALUE);

            GridPane.setHgrow(item_name, Priority.ALWAYS);
            grid = new GridPane();
            grid.add(item_name, 0, 0);
            grid.add(enroll_button, 1, 0);
            grid.add(disconnect_button, 2, 0);
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            setEditable(false);
            setGraphic(null);
            if (item != null) {
                item_name.setText(item);
                setGraphic(grid);
            }
        }

        private void executeEnroll(ActionEvent event) {
            String command = "enroll " + item_name;
            sendToConsole(LogHelper.log(command, LogTypes.CONSOLE));
            CommandExecutor.execute(MainWindow.this, command);
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
