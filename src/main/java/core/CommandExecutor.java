package core;

import gui.EnrollWindow;
import javafx.event.ActionEvent;
import utility.*;
import gui.MainWindow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"SameParameterValue","BooleanMethodIsAlwaysInverted"})
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
     * Find the client from the list of all connected clients in the server.
     * @param server_manager the ServerManager object.
     * @param client_to_find the client name provided by the user.
     * @return the client.
     * @throws NullPointerException error when the client is not found or does not exist.
     */
    private static ServerManager.FSClient findClient(MainWindow app, ServerManager server_manager, String client_to_find)
    throws NullPointerException {
        ArrayList<ServerManager.FSClient> fsclients = server_manager.getClients();
        String client_name;
        for (ServerManager.FSClient client : fsclients) {
            client_name = client.getClientName();
            if (client_to_find.equals(client_name)) {
                app.sendToConsole(LogHelper.log(client_name + " found!", LogTypes.INFO));
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
        if (LogHelper.checkNullText(input)) return; // Check if input is empty.
        int id = checkValidCommand(input);
        ServerManager server_manager = app.getServerManager();

        command_switch: switch (id) {
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
                List<String> input_token = List.of(input.split(" "));

                if (!checkValidServer(app, server_manager))
                    break; // Server must be running to proceed.

                try {
                    String client_to_find = input_token.get(1);
                    ServerManager.FSClient client = findClient(app, server_manager, client_to_find);

                    EnrollWindow enroll_window = app.getEnrollWindow();
                    if (!enroll_window.getIsSubmitted())
                        break; // Must click enroll window submit button to proceed.

                    String first_name = enroll_window.getFirstName();
                    String middle_name = enroll_window.getMiddleName();
                    String last_name =  enroll_window.getLastName();
                    String age = enroll_window.getAge();
                    String gender = enroll_window.getGender();
                    String phone_number = enroll_window.getPhoneNumber();
                    String address = enroll_window.getAddress();
                    int finger_id = enroll_window.getFingerprintId();

                    client.sendCommand("enroll");
                    client.sendCommand(Integer.toString(finger_id));
                    client.sendCommand(first_name);
                    client.sendCommand(middle_name);
                    client.sendCommand(last_name);
                    client.sendCommand(age);
                    client.sendCommand(gender);
                    client.sendCommand(phone_number);
                    client.sendCommand(address);
                }
                catch (NullPointerException npe) {
                    app.sendToConsole(LogHelper.log(
                            "Client does not exist.", LogTypes.ERROR
                    ));
                }
                catch (IndexOutOfBoundsException ibe) {
                    app.sendToConsole(LogHelper.log("Missing arguments.", LogTypes.INVALID));
                }
            }


            case 4 -> {
                LogHelper.debugLog("Case 4: disconnect");
                List<String> input_token = List.of(input.split(" "));

                if (!checkValidServer(app, server_manager))
                    break; // Server must be running to proceed.

                try {
                    String client_to_find = input_token.get(1);
                    ServerManager.FSClient client = findClient(app, server_manager, client_to_find);
                    client.sendCommand("disconnect");
                    client.disconnect();
                }
                catch (NullPointerException npe) {
                    app.sendToConsole(LogHelper.log("Client does not exist.", LogTypes.ERROR));
                }
                catch (IndexOutOfBoundsException ibe) {
                    app.sendToConsole(LogHelper.log("Missing arguments.", LogTypes.INVALID));
                }
            }


            case 5 -> {
                LogHelper.debugLog("Case 5: show all clients info");
                ArrayList<ServerManager.FSClient> clients = server_manager.getClients();

                if (!checkValidServer(app, server_manager)) break; // server must be running to proceed.
                if (clients.size() == 0) {
                    app.sendToConsole(LogHelper.log("No clients found.", LogTypes.ERROR));
                    break; // terminate execution of command if there are no clients connected.
                }

                for (ServerManager.FSClient client : clients) {
                    String client_name = client.getClientName();
                    String client_address = client.getClientSocketAddress();
                    app.sendToConsole(LogHelper.log(
                            "|#| " + client_name + " | " + client_address + "|#|", LogTypes.INFO));
                }
            }


            case 6 -> {
                LogHelper.debugLog("Case 6: reboot client");
                List<String> input_token = List.of(input.split(" "));

                if (!checkValidServer(app, server_manager))
                    break; // Server must be running to proceed.

                try {
                    String client_to_find = input_token.get(1);
                    ServerManager.FSClient client = findClient(app, server_manager, client_to_find);
                    client.sendCommand("reboot");
                }
                catch (NullPointerException npe) {
                    app.sendToConsole(LogHelper.log("Client does not exist.", LogTypes.ERROR));
                }
                catch (IndexOutOfBoundsException ibe) {
                    app.sendToConsole(LogHelper.log("Missing arguments.", LogTypes.INVALID));
                }
            }


            case 7 -> {
                LogHelper.debugLog("Case 7: init db tables");

                DatabaseManager database_manager = new DatabaseManager();
                boolean isSuccessful = database_manager.initTables();
                String db_feedback = ((isSuccessful) ? "Init Database OK" : "Init Database FAIL");
                LogTypes db_feedback_type = ((isSuccessful) ? LogTypes.INFO : LogTypes.ERROR);
                app.sendToConsole(LogHelper.log(db_feedback, db_feedback_type));
            }


            case 8 -> {
                LogHelper.debugLog("Case 8: export ");

                DatabaseManager databaseManager = new DatabaseManager();
                TempExportQueryData export_data = new TempExportQueryData();
                List<String> input_token = List.of(input.split(" "));

                List<String> data;

                try {
                    String export_type = input_token.get(1);
                    boolean validFormat;

                    switch (export_type) {
                        case "date" -> {
                            String date = input_token.get(2);
                            validFormat = export_data.buildDate(date);
                            if (!validFormat) {
                                app.sendToConsole(LogHelper.log("Invalid date format. {yyyy-mm-dd}", LogTypes.INVALID));
                                break command_switch;
                            }
                            data = databaseManager.queryAttendanceByDate(export_data);

                            if (data == null)
                                throw new NullPointerException();

                            String filename = Exporter.buildAttendanceCSV(date, data);
                            app.sendToConsole(LogHelper.log("Export: " + filename, LogTypes.INFO));
                        }

                        case "event" -> {
                            String event_name = input_token.get(2);
                            export_data.buildEventName(event_name);
                            data = databaseManager.queryAttendanceByEventName(export_data);

                            if (data == null)
                                throw new NullPointerException();

                            String filename = Exporter.buildAttendanceCSV(event_name, data);
                            app.sendToConsole(LogHelper.log("Export: " + filename, LogTypes.INFO));
                        }
                        case "all_users" -> {/* TODO: export all users  */ }
                        case "all_attendance" -> {/* TODO: export all attendance data */}
                        default -> {
                            app.sendToConsole(LogHelper.log("Invalid syntax.", LogTypes.INVALID));
                            break command_switch;
                        }
                    }
                }
                catch (IndexOutOfBoundsException ibe) {
                    app.sendToConsole(LogHelper.log("Missing arguments.", LogTypes.INVALID));
                }
                catch (IOException ioe) {
                    app.sendToConsole(LogHelper.log("An IO Error occurred when exporting.", LogTypes.ERROR));
                }
                catch (NullPointerException npe) {
                    app.sendToConsole(LogHelper.log("Data is null. Check if database tables exist.", LogTypes.ERROR));
                }
            }


            case 9 -> {
                LogHelper.debugLog("Case 9: event see ");
                EventData event_data = app.getEventData();

                String current_event_name = event_data.getCurrentEventName();
                String current_event_location = event_data.getCurrentEventLocation();

                app.sendToConsole(LogHelper.log("Current Event Name: " + current_event_name, LogTypes.INFO));
                app.sendToConsole(LogHelper.log("Current Event Location: " + current_event_location, LogTypes.INFO));

            }


            case 10 -> {
                LogHelper.debugLog("Case 10: event new ");
                EventData event_data = app.getEventData();
                List<String> input_token = List.of(input.split(" "));

                try {
                    String new_name = input_token.get(2);
                    String new_loc = input_token.get(3);
                    event_data.setCurrentEventName(new_name);
                    event_data.setCurrentEventLocation(new_loc);

                    app.sendToConsole(LogHelper.log("Event data updated.", LogTypes.INFO));
                }
                catch (IndexOutOfBoundsException iobe) {
                    app.sendToConsole(LogHelper.log("Missing arguments.", LogTypes.INVALID));
                }
            }
        }
    }
}
