package edu.kit.kastel.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * A helper class wrapping {@link Files#readAllLines(Path)} for convenient usage.
 * @author Programmieren-Team
 */
public final class FileHelper {
    private static final String ERROR_INVALID_PATH = "Error, an invalid path has been passed.";

    private FileHelper() {
    }

    /**
     * Returns all lines of a file specified by the given path.
     * @param path the path to the file to read
     * @return all lines of the specified file
     * @throws IllegalArgumentException if an invalid path has been passed
     */
    public static List<String> readAllLines(String path) {
        try {
            return Files.readAllLines(Path.of(path));
        } catch (IOException e) {
            throw new IllegalArgumentException(ERROR_INVALID_PATH);
        }
    }
}
