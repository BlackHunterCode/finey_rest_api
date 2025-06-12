package br.com.blackhunter.hunter_wallet.rest_api.core.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LogUtil {

    private static final char[] SPINNER_FRAMES = {'|', '/', '-', '\\'};
    private static boolean running = false;
    private static ExecutorService executor;

    public static void startSpinner(String message) {
        running = true;
        executor = Executors.newSingleThreadExecutor();

        executor.submit(() -> {
            int frameIndex = 0;
            while (running) {
                System.out.print("\r[" + SPINNER_FRAMES[frameIndex] + "] " + message);
                frameIndex = (frameIndex + 1) % SPINNER_FRAMES.length;
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    public static void stopSpinner(String message, boolean success) {
        running = false;
        if (executor != null) {
            executor.shutdown();
        }

        String statusEmoji = success ? "[✅]" : "[❌]";
        System.out.print("\r" + statusEmoji + " " + message + "\n");
    }
}