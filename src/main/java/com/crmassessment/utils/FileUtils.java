package com.crmassessment.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;

/**
 * Verifies file downloads landed in the configured download directory
 * (see DriverFactory's Chrome download.default_directory preference,
 * driven by ConfigReader.getDownloadDir()). Selenium has no direct signal
 * for "download finished", so this polls the directory instead.
 */
public class FileUtils {

    private FileUtils() {
        // Utility class - no instances
    }

    /**
     * Polls the given directory until a file whose name contains
     * {@code fileNameContains} appears and is no longer being written to
     * (Chrome names in-progress downloads with a ".crdownload" suffix).
     */
    public static Optional<Path> waitForDownloadedFile(String downloadDir, String fileNameContains, int timeoutSeconds) {
        File dir = new File(downloadDir);
        long deadline = System.currentTimeMillis() + (timeoutSeconds * 1000L);

        while (System.currentTimeMillis() < deadline) {
            File[] matches = dir.listFiles((d, name) ->
                    name.contains(fileNameContains) && !name.endsWith(".crdownload"));
            if (matches != null && matches.length > 0) {
                return Optional.of(matches[0].toPath());
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return Optional.empty();
    }

    public static boolean haveSameContent(Path fileA, Path fileB) {
        try {
            return Files.readAllBytes(fileA).length > 0
                    && java.util.Arrays.equals(Files.readAllBytes(fileA), Files.readAllBytes(fileB));
        } catch (IOException e) {
            throw new RuntimeException("Failed to compare files: " + fileA + " vs " + fileB, e);
        }
    }

    /** Removes every file directly inside a directory (not subdirectories) - used to reset the download dir between test runs. */
    public static void clearDirectory(String directoryPath) {
        File dir = new File(directoryPath);
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        java.util.Arrays.stream(files)
                .filter(File::isFile)
                .sorted(Comparator.comparing(File::getName))
                .forEach(File::delete);
    }
}
