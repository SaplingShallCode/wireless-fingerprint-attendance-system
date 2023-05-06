package utility;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class LogHelper {

    private static String getTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM);
        return LocalDateTime.now().format(dtf);
    }

    private static boolean checkNullText(String msg) {
        return msg.equals("");
    }

    public static String log(String text, LogTypes type) {
        String log_text;
        if (!checkNullText(text)) {
            log_text = String.format(
                    "[%s][%s]: %s",
                    getTime(),
                    type.getType(),
                    text
            );
            return log_text;
        }
        return null;
    }

    public static void debugLog(String text) {
        System.out.println(log(text, LogTypes.DEBUG));
    }
}
