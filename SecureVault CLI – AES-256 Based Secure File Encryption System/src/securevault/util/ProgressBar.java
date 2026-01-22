package securevault.util;

public class ProgressBar {

    private static final int BAR_LENGTH = 30;
    private static boolean shownOnce = false;

    public static void update(String label, long processed, long total) {
        if (total <= 0) return;

        shownOnce = true;

        int percent = (int) Math.min(100, (processed * 100) / total);
        int filled = (int) ((percent * BAR_LENGTH) / 100);

        StringBuilder bar = new StringBuilder();
        bar.append("\r").append(label).append(": [");

        for (int i = 0; i < BAR_LENGTH; i++) {
            bar.append(i < filled ? "#" : " ");
        }

        bar.append("] ").append(percent).append("%");

        System.out.print(bar.toString());
        System.out.flush();

        if (percent >= 100) {
            System.out.println();
            shownOnce = false;
        }
    }

    // Force bar display for very small files
    public static void forceComplete(String label) {
        if (!shownOnce) {
            update(label, 1, 1);
        }
    }
}
