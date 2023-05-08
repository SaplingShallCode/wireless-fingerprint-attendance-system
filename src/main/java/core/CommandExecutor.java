package core;

import javafx.event.ActionEvent;
import gui.GuiConstants;
import gui.MainWindow;
import utility.LogHelper;
import utility.LogTypes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandExecutor {

    private CommandExecutor() {}


    /**
     * Check if an input is a valid command. Return the id of the command.
     * @param input the user input to be checked.
     * @return the id of the command.
     * @see GuiConstants.Commands
     */
    private static int checkValidCommand(String input) {
        int id;
        String reference;
        Pattern pattern;
        Matcher input_matcher;

        for (GuiConstants.Commands command : GuiConstants.Commands.values()) {
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
     * Execute the user input if it is a valid command.
     * @param app the MainWindow app.
     * @param input the user's console input.
     * @see GuiConstants.Commands
     */
    public static void execute(MainWindow app, String input) {
        int id = checkValidCommand(input);

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
            case 3 -> LogHelper.debugLog("Case 3: enroll");
        }
    }
}
