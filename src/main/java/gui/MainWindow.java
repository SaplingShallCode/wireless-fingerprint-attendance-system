package gui;

import server.ServerManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The MainWindow class handles the user interface of the server.
 * The JavaFX framework is used to build the user interface.
 */
public class MainWindow extends Application {
    private ServerManager server_manager;

    @Override
    public void start(Stage primary_stage) {
        primary_stage.show();
    }


    @Override
    public void stop() {
        server_manager.stopServer();
    }
}
