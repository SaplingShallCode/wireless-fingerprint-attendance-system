package gui;

import server.ServerManager;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import java.io.IOException;


/**
 * The MainWindow class handles the user interface of the server.
 * The JavaFX framework is used to build the user interface.
 */
public class MainWindow extends Application {
    private ServerManager server_manager;


    /**
     * Create a server during the initialization phase of the server.
     *
     * @throws IOException error when opening a socket
     */
    @Override
    public void init() throws IOException {
        server_manager = new ServerManager("0.0.0.0", 62609);
        new Thread(server_manager).start();
    }


    @Override
    public void start(Stage primary_stage) {
        primary_stage.show();
    }


    /**
     * Check if the server is closed.
     * If the server is not closed then remind the user to close the server.
     */
    @Override
    public void stop() {
        if (!server_manager.isClosed()) {
            // TODO: remind the user to close the server first before closing.
            server_manager.stopServer();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
