package elevator;

public class ThreadUtils {

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
            ignored.printStackTrace();
        }
    }
}
