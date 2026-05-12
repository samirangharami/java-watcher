package org.learn.assignment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    static void main() throws IOException {
        Path dirPath = Paths.get("resources");

        Files.list(dirPath)
                .filter(path -> {
                    String name = path.getFileName().toString();
                    return name.startsWith("score_") && name.endsWith(".txt");
                })
                .forEach(path -> {
                    try (Scanner scanner = new Scanner(Files.newBufferedReader(path))) {

                        int sum = 0;

                        while (scanner.hasNext()) {
                            if (scanner.hasNextInt()) {
                                sum += scanner.nextInt();
                            } else {
                                scanner.next();
                            }
                        }

                        System.out.println(sum);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

    }
}
