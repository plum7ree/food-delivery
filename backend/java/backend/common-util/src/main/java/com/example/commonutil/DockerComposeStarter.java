package com.example.commonutil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class DockerComposeStarter {

    private final File composeFile;
    private boolean shutdownHookAdded = false;

    public DockerComposeStarter(String dockerComposeFilePath) {
        this.composeFile = new File(dockerComposeFilePath);
        if (!this.composeFile.exists()) {
            throw new IllegalArgumentException("Docker Compose file not found: " + dockerComposeFilePath);
        }
        addShutdownHook();
    }

    private void addShutdownHook() {
        if (!shutdownHookAdded) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutdown hook triggered. Stopping all services...");
                try {
                    stopAllServices();
                } catch (Exception e) {
                    System.err.println("Error stopping services during shutdown: " + e.getMessage());
                }
            }));
            shutdownHookAdded = true;
        }
    }

    public void startAllServices() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(
            "docker-compose",
            "-f", composeFile.getAbsolutePath(),
            "up"
        );

        Process process = processBuilder.start();

        Pattern pattern = Pattern.compile(".*"); // 모든 로그 출력
        CompletableFuture<Boolean> stdoutFuture = readLogsAsync(process.getInputStream(), "STDOUT", pattern);
        CompletableFuture<Boolean> stderrFuture = readLogsAsync(process.getErrorStream(), "STDERR", pattern);

        CompletableFuture<Boolean> logFound = CompletableFuture.anyOf(stdoutFuture, stderrFuture)
            .thenApply(result -> (Boolean) result);

        try {
            boolean found = logFound.get(5, TimeUnit.MINUTES);
            if (!found) {
                throw new RuntimeException("Timeout waiting for all services to start");
            }
            System.out.println("All services started successfully.");
        } catch (Exception e) {
            process.destroyForcibly();
            throw e;
        }
    }

    public void startServiceAndWaitForLog(String serviceName, String logPattern, long timeout, TimeUnit unit) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(
            "docker-compose",
            "-f", composeFile.getAbsolutePath(),
            "up",
//            "-d", // remove -d to show stdout.
            serviceName
        );

        Process process = processBuilder.start();

        Pattern pattern = Pattern.compile(logPattern);
        CompletableFuture<Boolean> stdoutFuture = readLogsAsync(process.getInputStream(), "STDOUT", pattern);
        CompletableFuture<Boolean> stderrFuture = readLogsAsync(process.getErrorStream(), "STDERR", pattern);

        CompletableFuture<Boolean> logFound = CompletableFuture.anyOf(stdoutFuture, stderrFuture)
            .thenApply(result -> (Boolean) result);

        try {

            boolean found = logFound.get(timeout, unit);
            if (!found) {
                throw new RuntimeException("Failed to find expected log message matching pattern: " + logPattern);
            }
            System.out.println("logFound process waits done");
        } catch (Exception e) {
            process.destroyForcibly();
            throw e;
        }
    }

    private CompletableFuture<Boolean> readLogsAsync(java.io.InputStream inputStream, String streamName, Pattern pattern) {
        return CompletableFuture.supplyAsync(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(streamName + ": " + line);
                    if (pattern.matcher(line).find()) {
                        return true;
                    }
                }
            } catch (Exception e) {
                System.err.println("Error reading " + streamName + ": " + e.getMessage());
            }
            return false;
        });
    }

    public void stopAllServices() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(
            "docker-compose",
            "-f", composeFile.getAbsolutePath(),
            "down"
        );
        Process process = processBuilder.start();
        boolean completed = process.waitFor(5, TimeUnit.MINUTES);
        if (!completed) {
            throw new RuntimeException("Timeout waiting for services to stop");
        }
    }
}