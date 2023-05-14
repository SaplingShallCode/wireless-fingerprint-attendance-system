package core;

import javafx.event.ActionEvent;
import utility.Const;
import gui.MainWindow;
import utility.LogHelper;
import utility.LogTypes;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandExecutor {

    private CommandExecutor() {}


    /**
     * Check if an input is a valid command. Return the id of the command.
     * @param input the user input to be checked.
     * @return the id of the command.
     * @see Const.Commands
     */
    private static int checkValidCommand(String input) {
        int id;
        String reference;
        Pattern pattern;
        Matcher input_matcher;

        for (Const.Commands command : Const.Commands.values()) {
            id = command.getId();
            reference = command.getReference();
            pattern = Pattern.compile("^\\b" + reference + "\\b.*");
            input_matcher = pattern.matcher(input);

            if (input_matcher.matches()) {
                LogHelper.debugLog("Is valid command: " + reference);
                return id;
            }
        }
        LogHelper.debugLog("Is invalid command: " + input);
        return 0;
    }


    /**
     * Checks if the server manager object is instantiated or if the server is currently running.
     * @param app the MainWindow object.
     * @param server_manager the ServerManager object
     * @return false if the server is null or not running.
     */
    private static boolean checkValidServer(MainWindow app, ServerManager server_manager) {
        if (server_manager == null) {
            app.sendToConsole(LogHelper.log(
                    "Server is null. Run the server at least once.", LogTypes.INVALID
            ));
            return false;
        }
        else if (server_manager.isClosed()) {
            app.sendToConsole(LogHelper.log(
                    "Server must be running before executing this command.", LogTypes.INVALID
            ));
            return false;
        }
        return true;
    }


    /**
     * Checks if the syntax of the input is valid.
     * @param app the MainWindow object.
     * @param input the user's console input.
     * @param valid_length the valid length if the user's input.
     * @return false if the syntax is invalid.
     */
    private static boolean checkValidSyntax(MainWindow app, String input, int valid_length) {
        if (input.length() < valid_length) {
            app.sendToConsole(LogHelper.log(
                    "Invalid syntax.", LogTypes.INVALID
            ));
            return false;
        }
        return true;
    }


    /**
     * Find the client from the list of all connected clients in the server.
     * @param server_manager the ServerManager object.
     * @param client_to_find the client name provided by the user.
     * @return the client.
     * @throws NullPointerException error when the client is not found or does not exist.
     */
    private static ServerManager.FSClient findClient(ServerManager server_manager, String client_to_find)
    throws NullPointerException {
        ArrayList<ServerManager.FSClient> fsclients = server_manager.getClients();
        for (ServerManager.FSClient client : fsclients) {
            if (client_to_find.equals(client.getClientName())) {
                return client;
            }
        }
        throw new NullPointerException();
    }


    /**
     * Execute the user input if it is a valid command.
     * @param app the MainWindow app.
     * @param input the user's console input.
     * @see Const.Commands
     */
    public static void execute(MainWindow app, String input) {
        int id = checkValidCommand(input);
        ServerManager server_manager = app.getServerManager();

        switch (id) {
            case 0 -> {
                LogHelper.debugLog("Not a valid command");
                app.sendToConsole(LogHelper.log(
                        "Not a recognizable command. See list of available commands.", LogTypes.INVALID
                ));
            }
            case 1 -> {
                LogHelper.debugLog("Case 1: Start server");
                app.start_server(new ActionEvent());
            }
            case 2 -> {
                LogHelper.debugLog("Case 2: Stop server");
                app.stop_server(new ActionEvent());
            }
            case 3 -> {
                LogHelper.debugLog("Case 3: enroll");
                String client_to_find = new StringBuilder(input).substring(7, 15);
                ServerManager.FSClient client;
                int finger_id;

                if (!checkValidServer(app, server_manager) || !checkValidSyntax(app, input, 7)) {
                    break;
                }
                try {
                    client = findClient(server_manager, client_to_find);
                    finger_id = Integer.parseInt(new StringBuilder(input).substring(16));
                    if (finger_id < 1) {
                        throw new NumberFormatException();
                    }
                }
                catch (NumberFormatException nfe) {
                    app.sendToConsole(LogHelper.log(
                            "Invalid id input. Must be an integer NOT less than 1.", LogTypes.INVALID
                    ));
                    break;
                }
                catch (StringIndexOutOfBoundsException sioube) {
                    app.sendToConsole(LogHelper.log(
                            "Invalid id input. Please provide a valid integer.", LogTypes.INVALID
                    ));
                    break;
                }
                catch (NullPointerException npe) {
                    app.sendToConsole(LogHelper.log(
                            "Client does not exist.", LogTypes.INVALID
                    ));
                    break;
                }

                client.sendCommand("enroll");
            }
        }
    }
}
