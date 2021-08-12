package pbouda.jjf;

public abstract class Output {

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String BLUE = "\u001B[34m";

    private static final String PURPLE = "\u001B[35m";
    private static final String SUCCESS = "[" + GREEN + "SUCCESS" + RESET + "] ";
    private static final String INFO = "[" + BLUE + "INFO" + RESET + "] ";
    private static final String WARN = "[" + PURPLE + "WARN" + RESET + "] ";
    private static final String ERROR = "[" + RED + "ERROR" + RESET + "] ";

    public static void error(String message, boolean exit) {
        System.out.println(ERROR + message + " ");
        if (exit) {
            System.exit(1);
        }
    }

    public static void error(String message) {
        error(message, false);
    }

    public static void success(String message) {
        System.out.println(SUCCESS + message + " ");
    }

    public static void info(String message) {
        System.out.println(INFO + message + " ");
    }
}
