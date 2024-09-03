package multithreading;

import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }
        int maxThreads = 25;
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
        List<Future<Integer>> futures = new ArrayList<>();

        long startTs = System.currentTimeMillis(); // start time

        for (String text : texts) {
            Callable<Integer> task = () -> {
                int maxSize = 0;
                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                return maxSize;
            };

            Future<Integer> future = executor.submit(task);
            futures.add(future);
        }

        int globalMaxSize = 0;
        for (int i = 0; i < texts.length; i++) {
            Integer maxSize = futures.get(i).get();
            if (maxSize > globalMaxSize) {
                globalMaxSize = maxSize;
            }
            System.out.println(texts[i].substring(0, 100) + " -> " + maxSize);
        }
        System.out.println(" Максимум среди всех строк -> " + globalMaxSize);

        executor.shutdown();
        long endTs = System.currentTimeMillis(); // end time
        System.out.println("Time: " + (endTs - startTs) + "ms");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}