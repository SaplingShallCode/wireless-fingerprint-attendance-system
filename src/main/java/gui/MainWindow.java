package gui;

import core.EventData;
import javafx.application.Platform;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
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
    private Label console_label;
    private TextFlow console_output;
    private TextField command_field;
    private Button command_button;

    private EventData event_data;


    /**
     * Initialize some resources needed for the application.
     * @see Const.Commands for the commands being added.
     */
    @Override
    public void init() {
        Platform.setImplicitExit(true); // close app when all windows are closed.
        for (Const.Commands command: Const.Commands.values()) {
            commands_list.add(command.getSyntax() + " : " + command.getDescription());
        }
        event_data = new EventData();
        event_data.setCurrentEventName("Unspecified");
        event_data.setCurrentEventLocation("Unspecified");
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
        login_window.showAndWait(); // blocking method

        primary_stage = stage;
        initUI();
        // handle the MainWindow close event
        // if the server is not closed then remind user to close.
        // NOTE: if server_manager is null then a NullPointerException is thrown.
        primary_stage.setOnCloseRequest(event -> {
            try {
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
                    // Adding icon to the alert window
                    Stage alert_window_stage = (Stage) alert_window.getDialogPane().getScene().getWindow();
                    for (Const.Icons i : Const.Icons.values()) {
                        alert_window_stage.getIcons().add(i.getIconImage());
                    }
                    // Adding a stylesheet to the alert window
                    DialogPane alert_window_dialog_pane = alert_window.getDialogPane();
                    alert_window_dialog_pane.getStyleClass().add("alert-window");
                    alert_window_dialog_pane.getStylesheets().add(Const.StringValues.STYLESHEET_PATH.getValue());
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
        commands_listlabel.getStyleClass().add("commands-list-label");
        commands_listview = new ListView<>(commands_list);
        commands_listview.getStyleClass().add("commands-list");
        commands_listview.setFocusTraversable(false);

        clients_listlabel = new Label("Connected clients:");
        clients_listlabel.getStyleClass().add("clients-list-label");
        clients_listview = new ListView<>(clients_list);
        clients_listview.getStyleClass().add("clients-list");
        clients_listview.setCellFactory(param -> new ClientCell()); // See nested ClientCell class below.
        clients_listview.setFocusTraversable(false);

        HBox server_group = new HBox();
        server_group.setSpacing(10);
        server_label = new Label("Server");

        start_server_button = new Button("Start");
        start_server_button.getStyleClass().add("start-server-button");
        start_server_button.setMaxWidth(Double.MAX_VALUE);
        start_server_button.setOnAction(this::start_server);

        stop_server_button = new Button("Stop");
        stop_server_button.getStyleClass().add("stop-server-button");
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

        // fill the remaining spaces on the gui by stretching the components.
        HBox.setHgrow(start_server_button, Priority.ALWAYS);
        HBox.setHgrow(stop_server_button, Priority.ALWAYS);

        // ----- Column 2 ----- //
        VBox col2 = new VBox();

        ScrollPane console_container = new ScrollPane();
        console_container.getStyleClass().add("console-container");
        console_container.setFitToWidth(false);
        console_container.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);  // show console scrollbar when needed
        console_container.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);     // Always show console scrollbar
        console_container.setPannable(false);

        console_label = new Label("Console");
        console_output = new TextFlow();
        console_output.getStyleClass().add("console-output");
        console_output.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        console_output.setFocusTraversable(false);
        console_output.getChildren().addListener((ListChangeListener<Node>) observable -> {
            console_output.layout();        // update the layout of the text nodes.
            console_container.layout();     // update the layout of the console output node.
            console_container.setVvalue(1); // Auto scroll to bottom of the console.
        });
        console_container.setContent(console_output);

        // initialize console text
        Text namegroup_Text = new Text("Wireless-Fingerprint-Based-Attendance-Logger-Server by NameGroup.\n\n");
        namegroup_Text.setFont(Const.CONSOLAS);
        namegroup_Text.setFill(Color.WHITE);
        console_output.getChildren().add(namegroup_Text);

        command_field = new TextField();
        command_field.getStyleClass().add("command-field");
        command_field.setMaxWidth(Double.MAX_VALUE);
        command_field.setOnKeyPressed( event -> {
            // When user presses the Enter button on keyboard, send the input to the command executor.
            if (event.getCode() == KeyCode.ENTER) {
                String input = command_field.getText();
                sendToConsole(LogHelper.log(input, LogTypes.CONSOLE));
                CommandExecutor.execute(this, input);
                command_field.clear();
            }
        });

        col2.getChildren().addAll(
                console_label,
                console_container,
                command_field
        );

        // fill the remaining spaces on the gui by stretching the components.
        VBox.setVgrow(console_container, Priority.ALWAYS);
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
        for (Const.Icons i : Const.Icons.values()) {
            primary_stage.getIcons().add(i.getIconImage());
        }
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
     * @param rich_text formatted text to be appended.
     * @implNote send to console's text parameter should be returned by
     * LogHelper's log function.
     * @see LogHelper for console logging.
     */
    public void sendToConsole(Text rich_text) {
        if (rich_text != null) {
            Platform.runLater(() -> console_output.getChildren().add(rich_text));
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


    public EventData getEventData() {
        return event_data;
    }


    /**
     * @implNote Used mainly by the CommandExecutor class
     * @return a new EnrollWindow instance.
     * @see CommandExecutor
     */
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
        private final ImageView enroll_icon;
        private final ImageView disconnect_icon;

        public ClientCell() {
            super();

            enroll_icon = new ImageView(Const.ENROLL_ICON);
            enroll_icon.setFitWidth(16);
            enroll_icon.setFitHeight(16);
            disconnect_icon = new ImageView(Const.DISCONNECT_ICON);
            disconnect_icon.setFitWidth(16);
            disconnect_icon.setFitHeight(16);

            enroll_tooltip = new Tooltip("Enroll");
            enroll_tooltip.setShowDelay(Duration.ZERO);
            disconnect_tooltip = new Tooltip("Disconnect");
            disconnect_tooltip.setShowDelay(Duration.ZERO);

            enroll_button = new Button();
            enroll_button.setGraphic(enroll_icon);
            enroll_button.setOnAction(this::executeEnroll);
            enroll_button.setTooltip(enroll_tooltip);
            enroll_button.setMaxWidth(Double.MAX_VALUE);

            disconnect_button = new Button();
            disconnect_button.setGraphic(disconnect_icon);
            disconnect_button.setOnAction(this::executeDisconnect);
            disconnect_button.setTooltip(disconnect_tooltip);
            disconnect_button.setMaxWidth(Double.MAX_VALUE);

            item_name = new Label(); // the Client name
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


        /**
         * Call the CommandExecutor to execute the enroll command.
         * @param event the event fired by the enroll button.
         */
        private void executeEnroll(ActionEvent event) {
            String command = "enroll " + item_name.getText(); // item name == client name
            sendToConsole(LogHelper.log(command, LogTypes.CONSOLE));
            CommandExecutor.execute(MainWindow.this, command);
        }


        /**
         * Call the CommandExecutor to execute the enroll comand.
         * @param event the event fired by the disconnect button.
         */
        private void executeDisconnect(ActionEvent event) {
            String command = "disconnect " + item_name.getText(); // item name == client name
            sendToConsole(LogHelper.log(command, LogTypes.CONSOLE));
            CommandExecutor.execute(MainWindow.this, command);
        }
    }



    /**
     * Launch the application.
     * @implNote Refer to the Launcher class when trying to run the program.
     * @param args terminal arguments.
     */

    public static void main(String[] args) {
        launch(args);
    }
}
