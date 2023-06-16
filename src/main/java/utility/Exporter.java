package utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Utility class that handles Database to CSV (Comma Separated Values) exporting.
 */
public class Exporter {

    /**
     * Build the CSV File. The File will be createed in the same directory as the program.
     * @param csv_filename the name of the file.
     * @param data the list of String data that will be appended to the file.
     * @throws IOException if an I/O error occurs.
     */
    public static void buildAttendanceCSV(String csv_filename, List<String> data) throws IOException {
        int duplicateIndex = 0;
        String csv_fullfilename = csv_filename + ".csv";
        String current_directory = System.getProperty("user.dir");

        File csv_file = new File(current_directory, csv_fullfilename);
        boolean isFileCreated = csv_file.createNewFile();

        // Check for duplicate files.
        while (!isFileCreated) {
            ++duplicateIndex;
            csv_fullfilename = String.format("%s_(%d).csv", csv_filename, duplicateIndex);
            csv_file = new File(current_directory, csv_fullfilename);
            isFileCreated = csv_file.createNewFile();
        }

        // append each data to the file.
        BufferedWriter csv_writer = new BufferedWriter(new FileWriter(csv_file));

        for (String line : data) {
            csv_writer.newLine();
            csv_writer.write(line);
        }

        csv_writer.close();
    }
}
