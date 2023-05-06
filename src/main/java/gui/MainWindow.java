package gui;

import server.Commands;
import server.ServerManager;
import javafx.application.Platform;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import utility.LogHelper;
import utility.LogTypes;

import java.io.IOException;


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
     */
    @Override
    public void init() {
        Platform.setImplicitExit(true); // close app when all windows are closed.
        for (Commands command: Commands.values()) {
            commands_list.add(command.getSyntax() + " - " + command.getDescription());
        }
    }


    /**
     * The start method always runs after the init method.
     *
     * @param stage Provided by JavaFX application.
     */
    @Override
    public void start(Stage stage) {
        login_window = new LoginWindow(new Stage());
        login_window.initUI();
        login_window.handleClose();
        login_window.showAndWait();

        primary_stage = stage;
        initUI();
        // handle the MainWindow close event
        primary_stage.setOnCloseRequest(event -> {
            try {
                // if the server is not closed then remind user to close.
                // NOTE: if server_manager is null then a NullPointerException is thrown.
                if (!server_manager.isClosed()) {
                    event.consume();
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
        primary_stage.show();
    }


    /**
     * Check if the server is closed.
     */
    @Override
    public void stop() {
        System.out.println("Successfully close app.");
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
        start_server_button.setOnAction(event -> {
            try {
                server_manager = new ServerManager(
                        this,
                        login_window.getHost(),
                        login_window.getPort()
                );
                new Thread(server_manager).start();
            }
            catch (IOException ioe) {
                sendToConsole(LogHelper.log("Error opening socket.", LogTypes.ERROR));
            }
            start_server_button.setDisable(true);
            stop_server_button.setDisable(false);
        });

        stop_server_button = new Button("Stop");
        stop_server_button.setMaxWidth(Double.MAX_VALUE);
        stop_server_button.setDisable(true);
        stop_server_button.setOnAction(event -> {
            try {
                server_manager.stopServer();
            }
            catch (IOException ioe) {
                sendToConsole(LogHelper.log("Error when closing server.", LogTypes.ERROR));
            }
            stop_server_button.setDisable(true);
            start_server_button.setDisable(false);
        });

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

        HBox command_group = new HBox();
        command_field = new TextField();
        command_field.setMaxWidth(Double.MAX_VALUE);
        command_field.setOnKeyPressed( event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendToConsole(LogHelper.log(command_field.getText(), LogTypes.CONSOLE));
                command_field.clear();
            }
        });

        command_button = new Button("Enter");
        command_button.setOnAction( event -> {
            sendToConsole(LogHelper.log(command_field.getText(), LogTypes.CONSOLE));
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

        BorderPane.setMargin(col1, new Insets(10));
        BorderPane.setMargin(col2, new Insets(10));

        // ----- Stage and Scene ----- //
        Scene scene = new Scene(root);
        command_field.requestFocus();
        scene.getStylesheets().add(GuiConstants.StringValues.STYLESHEET_PATH.getValue());

        primary_stage.setHeight(GuiConstants.WindowSizes.MIN_HEIGHT.getValue());
        primary_stage.setWidth(GuiConstants.WindowSizes.MIN_WIDTH.getValue());
        primary_stage.setMinHeight(GuiConstants.WindowSizes.MIN_HEIGHT.getValue());
        primary_stage.setMinWidth(GuiConstants.WindowSizes.MIN_WIDTH.getValue());
        primary_stage.setTitle(GuiConstants.StringValues.WINDOW_TITLE.getValue());
        primary_stage.setScene(scene);
    }


    /**
     * Append text to the console. This method is run in a thread to
     * avoid concurrency errors.
     *
     * @param text text to be appended.
     */
    public void sendToConsole(String text) {
        if (text != null) {
            Platform.runLater(() ->
                log_view.appendText(text + "\n")
            );
        }
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

        // components
        Label host_label;
        TextField host_textfield;
        Label port_label;
        TextField port_textfield;

        /**
         * Instantiate a LoginWindow object.
         *
         * @param stage the stage object. should be undecorated.
         */
        LoginWindow (Stage stage) {
            login_stage = stage;
            login_stage.setAlwaysOnTop(true);
            login_stage.setResizable(false);
        }


        /**
         * Initialize the UI of the window.
         */
        public void initUI() {
            // ----- Row 1 ----- //
            HBox row1 = new HBox();
            row1.setAlignment(Pos.CENTER);

            host_label = new Label("Bind to host:");
            host_label.setPrefWidth(GuiConstants.LoginWindowSizes.LABEL_WIDTH.getValue());

            host_textfield = new TextField("0.0.0.0");
            host_textfield.setPrefWidth(GuiConstants.LoginWindowSizes.TEXTFIELD_WIDTH.getValue());
            row1.getChildren().addAll(host_label, host_textfield);

            // ----- Row 2 ----- //
            HBox row2 = new HBox();
            row2.setAlignment(Pos.CENTER);

            port_label = new Label("Bind to port: ");
            port_label.setPrefWidth(GuiConstants.LoginWindowSizes.LABEL_WIDTH.getValue());

            port_textfield = new TextField("62609");
            port_textfield.setPrefWidth(GuiConstants.LoginWindowSizes.TEXTFIELD_WIDTH.getValue());
            row2.getChildren().addAll(port_label, port_textfield);

            // ----- Row 3 ----- //
            HBox row3 = new HBox();
            row3.setAlignment(Pos.CENTER);

            Button enter_button = new Button("Enter");
            enter_button.setMaxWidth(Double.MAX_VALUE);
            row3.getChildren().addAll(enter_button);
            enter_button.setOnAction(e -> {
                // Assign the text inputs from the text fields to the respective variables.
                host = host_textfield.getText();
                port = Integer.parseInt(port_textfield.getText());
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
            BorderPane.setMargin(semi_root, new Insets(10));

            // ----- Scene ----- //
            Scene scene = new Scene(root); // set the main layout of the scene.
            login_stage.setWidth(GuiConstants.LoginWindowSizes.PRIMARY_WIDTH.getValue());
            login_stage.setHeight(GuiConstants.LoginWindowSizes.PRIMARY_HEIGHT.getValue());
            login_stage.setTitle(GuiConstants.StringValues.LOGIN_WINDOW_TITLE.getValue());
            login_stage.setScene(scene); // set the main scene.
        }


        /**
         * Handle the LoginWindow close event.
         */
        public void handleClose() {
            login_stage.setOnCloseRequest(event ->
                Platform.exit()
            );
        }


        /**
         * Get the host input from the text field.
         *
         * @return the host to be bound to.
         */
        public String getHost() {
            return host;
        }


        /**
         * Get the port input from the text field.
         *
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
         */
        public void closeWindow() {
            login_stage.close();
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
