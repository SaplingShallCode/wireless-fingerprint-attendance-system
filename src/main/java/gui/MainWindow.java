package gui;

import server.ServerManager;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.geometry.Pos;


/**
 * The MainWindow class handles the user interface of the server.
 * The JavaFX framework is used to build the user interface.
 */
public class MainWindow extends Application {
    private ServerManager server_manager;
    private LoginWindow login_window;
    private Stage primary_stage;


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
