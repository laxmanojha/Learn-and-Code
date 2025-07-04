package frontend.newsaggregation.util;

public class AppState {
    private static boolean shouldExitToHome = false;

    public static boolean shouldExitToHome() {
        return shouldExitToHome;
    }

    public static void setExitToHome(boolean exit) {
        shouldExitToHome = exit;
    }

    public static void reset() {
        shouldExitToHome = false;
    }
}
