package utility;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;


/**
 * The LogHelper auto-formats the logged text to something more detailed.
 * It includes the specific date and time the text was logged into an output
 * and also what type of text it is supposed to represent.
 */
@SuppressWarnings("ConstantConditions")
public class LogHelper {

    /**
     * Get the Date and Time at during this specific instant. The Date and Time is
     * returned in a specific format.
     * @return the Date and Time in the format of 'Month/Day/Year, Hr:Min:Sec AM/PM'.
     */
    private static String getDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM);
        return LocalDateTime.now().format(dtf);
    }

    /**
     * Check if the text is empty. Check is needed to avoid spam entering.
     * @param text the text to be checked.
     * @return true if the string is empty.
     */
    public static boolean checkNullText(String text) {
        return text.matches("^\\s*$");
    }

    /**
     * Format a text into a valid informative log text.
     * @param text the text to be logged.
     * @param type the classification of the text.
     * @return a formatted text to be used for logging.
     */
    public static Text log(String text, LogTypes type) {
        String log_text;
        Text rich_text;
        if (!checkNullText(text)) {
            log_text = String.format(
                    "[%s][%s]: %s\n", // ---> [datetime][logtype]: <text>
                    getDateTime(),
                    type.getType(),
                    text
            );
            rich_text = new Text(log_text);
            rich_text.setFont(Const.CONSOLAS);

            switch (type) {
                case ERROR -> rich_text.setFill(Color.RED);
                case INFO -> rich_text.setFill(Color.DODGERBLUE);
                case WARNING, INVALID -> rich_text.setFill(Color.ORANGE);
                default -> rich_text.setFill(Color.WHITE);
            }
            return rich_text;
        }
        return null;
    }

    /**
     * Log the text and display it into the Standard Output (System.out).
     * This method is intended to be used for debugging.
     * @param text the text to be logged.
     */
    public static void debugLog(String text) {
        String debug_str = log(text, LogTypes.DEBUG).getText();
        if (debug_str != null) System.out.print(debug_str);
    }
}