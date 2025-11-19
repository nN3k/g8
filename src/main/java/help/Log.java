package help;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public abstract class Log {
    //TODO: Save log as Txt and override on change
    private static ArrayList<String> log = new ArrayList();

    public static void logAdd(String s) {
        log.add(java.time.LocalTime.now() + ": " + s);
    }
    public static ArrayList<String> getLog() {
        return log;
    }
    public static void logSave() throws IOException {
        String fileName = "LOG.txt";
        File file = new File(fileName);
        if (file.exists()) {
            synchronized (System.out) {
                System.out.println("Press enter to overwrite log or enter a new log file name:\n");
            }
            synchronized (System.in) {
                Scanner scanner = new Scanner(System.in);
                String newLog = scanner.nextLine();
                if (!newLog.equals("")) {
                    fileName = newLog;
                }
            }
        }
        FileWriter fileWriter = new FileWriter(fileName);
        BufferedWriter writer = new BufferedWriter(fileWriter);
        for (String s : log) {
            writer.write(s + "\n");
            writer.flush();
        }
        writer.close();

    }
    public static void showLog() {
        System.out.println("This is the log------------------------");
        for (String s : log) {
            System.out.println(s + "\n");
        }
    }
}
