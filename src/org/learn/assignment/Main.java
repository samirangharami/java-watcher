package org.learn.assignment;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class Main {
    static void main() throws IOException, InterruptedException {

        Path dirPath = Paths.get("resources");
        HashMap<String, Integer> sumLogs = new HashMap<>();

        initialWrite(sumLogs, dirPath);
        watchDir(sumLogs, dirPath);
    }

    private static void watchDir(HashMap<String, Integer> sumLogs, Path dirPath) throws IOException, InterruptedException {
        WatchService watcher = FileSystems.getDefault().newWatchService();
        dirPath.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);

        while (true) {
            WatchKey key = watcher.take();

            for (WatchEvent<?> event : key.pollEvents()) {
                Path fileName = (Path) event.context();
                Path fullPath = dirPath.resolve(fileName);

                if (fileName.startsWith("score_") && fileName.endsWith(".txt")) {
                    sumLogs.put(fullPath.toString(), getSum(fullPath));
                    writeToFile(sumLogs);
                }
            }

            boolean valid = key.reset();

            if (!valid) {
                break;
            }
        }
    }

    private static void initialWrite(HashMap<String, Integer> sumLogs, Path dirPath) throws IOException {
        List<Path> files = Files.list(dirPath)
                .filter(path -> {
                    String name = path.getFileName().toString();
                    return name.startsWith("score_") && name.endsWith(".txt");
                }).toList();


        for (Path file : files) {
            sumLogs.put(dirPath.resolve(file).toString(), getSum(file));
        }

        writeToFile(sumLogs);

    }

    private static void writeToFile(HashMap<String, Integer> sumLogs) throws IOException {
        Path out = Paths.get("resources/scores.txt");

        BufferedWriter writer = Files.newBufferedWriter(out, StandardOpenOption.APPEND);

        int totalSum = 0;

        for (Integer value : sumLogs.values()) {
            totalSum += value;
        }

        writer.write(LocalDate.now() + " : " + totalSum);
        writer.newLine();
        writer.flush();
    }

    private static int getSum(Path file) {
        int sum = 0;

        try (Scanner scanner = new Scanner(Files.newBufferedReader(file))) {

            while (scanner.hasNext()) {
                if (scanner.hasNextInt()) {
                    sum += scanner.nextInt();
                } else {
                    scanner.next();
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sum;
    }
}
