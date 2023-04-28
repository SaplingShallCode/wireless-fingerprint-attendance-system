import gui.MainWindow;

/**
 * The Main Launcher of the Application.
 * The Launcher is required to deal with the module system trouble
 * that JavaFX and Maven is making.
 *
 * @implNote Use this class for launching the program.
 */
public class Launcher {
    public static void main(String[] args) {
        MainWindow.main(args);
    }
}
