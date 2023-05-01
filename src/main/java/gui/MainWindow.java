package gui;

import javafx.scene.layout.BorderPane;
import server.Commands;
import server.ServerManager;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;


/**
 * The MainWindow class handles the user interface of the server.
 * The JavaFX framework is used to build the user interface.
 */
public class MainWindow extends Application {
    private final ObservableList<String> commands_list = FXCollections.observableArrayList();
    private final ObservableList<String> clients_list = FXCollections.observableArrayList();
    private ServerManager server_manager;
    private LoginWindow login_window;
    private Stage primary_stage;


    /**
     * The WindowVariables are a collection of constants that will be used
     * by the MainWindow class.
     */
    private enum WindowVariables {
        MIN_HEIGHT(600),
        MIN_WIDTH(800),
        WINDOW_TITLE("Wireless Fingerprint-based Attendance Logger Server by NameGroup"),
        STYLESHEET_PATH("css/styles.css");

        private int value;
        private String text;

        /**
         * Overloaded constructor for Integer type.
         * @param value Integer value.
         */
        WindowVariables(int value) {
            this.value = value;
        }

        /**
         * Overloaded constructor for String type.
         * @param text String value.
         */
        WindowVariables(String text) {
            this.text = text;
        }

        public int getValue() {
            return value;
        }

        public String getText() {
            return text;
        }
    }


    /**
     * Initialize all resources needed for the application.
     */
    @Override
    public void init() {
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
        login_window.showAndWait();

        primary_stage = stage;
        initUI();
        primary_stage.show();

    }


    /**
     * Check if the server is closed.
     * If the server is not closed then remind the user to close the server.
     */
    @Override
    public void stop() {
        // TODO: remind the user to close the server first before closing.
    }


    private void initUI() {
        // ----- Layout ----- //
        BorderPane root = new BorderPane();

        // ----- Column 1 ----- //
        VBox col1 = new VBox();
        root.setLeft(col1);
        Label commands_listlabel = new Label("List of available commands:");
        ListView<String> commands_listview = new ListView<>(commands_list);
        commands_listview.setFocusTraversable(false);

        Label clients_listlabel = new Label("Connected clients:");
        ListView<String> clients_listview = new ListView<>(clients_list);
        clients_listview.setFocusTraversable(false);
        col1.getChildren().addAll(
                commands_listlabel,
                commands_listview,
                clients_listlabel,
                clients_listview
        );

        // ----- Column 2 ----- //
        VBox col2 = new VBox();
        root.setCenter(col2);

        Label log_label = new Label("Console");
        TextArea log_view = new TextArea();
        log_view.setFocusTraversable(false);
        log_view.setEditable(false);
        log_view.setMouseTransparent(true);

        HBox command_group = new HBox();
        TextField command_field = new TextField();
        command_field.setOnKeyPressed( event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendToConsole(log_view, command_field);
            };
        });

        Button command_button = new Button("Enter");
        command_button.setOnAction( event -> {
            sendToConsole(log_view, command_field);
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

        // ----- Stage and Scene ----- //
        Scene scene = new Scene(root);
        scene.getStylesheets().add(WindowVariables.STYLESHEET_PATH.getText());
        command_field.requestFocus();
        primary_stage.setHeight(WindowVariables.MIN_HEIGHT.getValue());
        primary_stage.setWidth(WindowVariables.MIN_WIDTH.getValue());
        primary_stage.setMinHeight(WindowVariables.MIN_HEIGHT.getValue());
        primary_stage.setMinWidth(WindowVariables.MIN_WIDTH.getValue());
        primary_stage.setTitle(WindowVariables.WINDOW_TITLE.getText());
        primary_stage.setScene(scene);
    }


    private void sendToConsole(TextArea log_view, TextField command_field) {
        String text = command_field.getText();
        if (!text.equals("")) {
            log_view.appendText(text + "\n");
            command_field.clear();
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


        /**
         * Instantiate a LoginWindow object.
         *
         * @param stage the stage object. should be undecorated.
         */
        LoginWindow (Stage stage) {
            this.login_stage = stage;
            this.login_stage.setResizable(false);
        }


        /**
         * Initialize the UI of the window.
         */
        public void initUI() {
            // ----- Layout ----- //
            VBox root = new VBox();

            // ----- Row 1 ----- //
            HBox row1 = new HBox();
            root.getChildren().add(row1);
            Label host_label = new Label("Bind to host:");
            TextField host_textfield = new TextField("0.0.0.0");
            row1.getChildren().addAll(host_label, host_textfield);

            // ----- Row 2 ----- //
            HBox row2 = new HBox();
            root.getChildren().add(row2);
            Label port_label = new Label("Bind to port: ");
            TextField port_textfield = new TextField("62609");
            row2.getChildren().addAll(port_label, port_textfield);

            // ----- Scene ----- //
            HBox row3 = new HBox();
            row3.setAlignment(Pos.CENTER);
            root.getChildren().add(row3);
            Button enter_button = new Button("Enter");
            row3.getChildren().addAll(enter_button);
            enter_button.setOnAction(e -> {
                // Assign the text inputs from the text fields to the respective variables.
                host = host_textfield.getText();
                port = Integer.parseInt(port_textfield.getText());
                closeWindow();
            });


            // ----- Scene ----- //
            Scene scene = new Scene(root); // set the main layout of the scene.
            login_stage.setScene(scene); // set the main scene.
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
         * Show the window/stage while blocking the current thread.
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
