package me.matl114.matlib.algorithms.algorithm;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Map;

/**
 * Utility class for file and resource operations.
 * This class provides methods for file creation, copying, reading, and JSON parsing
 * operations for both regular files and classpath resources.
 */
public class FileUtils {

    /**
     * Gets or creates a file at the specified path.
     * If the parent directory doesn't exist, it will be created.
     * If the file doesn't exist, it will be created.
     *
     * @param path The path to the file
     * @return The File object
     * @throws IOException if the file or directory cannot be created
     */
    public static File getOrCreateFile(String path) throws IOException {
        File file = new File(path);
        return getOrCreateFile(file);
    }

    /**
     * Gets or creates a file.
     * If the parent directory doesn't exist, it will be created.
     * If the file doesn't exist, it will be created.
     *
     * @param file The file to get or create
     * @return The File object
     * @throws IOException if the file or directory cannot be created
     */
    public static File getOrCreateFile(File file) throws IOException {
        if (!file.getParentFile().exists()) {
            Files.createDirectories(file.getParentFile().toPath());
        }
        if (!file.exists()) {
            if (file.createNewFile()) {
                return file;
            } else {
                throw new IOException(file.toPath().toString() + " create failed");
            }
        } else {
            return file;
        }
    }

    /**
     * Ensures that the parent directory of the specified file exists.
     * Creates the parent directory if it doesn't exist.
     *
     * @param file The file whose parent directory should be ensured
     * @throws IOException if the parent directory cannot be created
     */
    public static void ensureParentDir(File file) throws IOException {
        if (!file.getParentFile().exists()) {
            Files.createDirectories(file.getParentFile().toPath());
        }
    }

    /**
     * Copies a file from one location to another.
     * The destination file will be created if it doesn't exist, or overwritten if it does.
     *
     * @param from The source file
     * @param to The destination path
     * @throws IOException if the source file doesn't exist or the copy operation fails
     */
    public static void copyFile(File from, String to) throws IOException {
        if (from.exists()) {
            File toFile = new File(to);
            ensureParentDir(toFile);
            Files.copy(from.toPath(), toFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } else {
            throw new IOException(from + " does not exist");
        }
    }

    /**
     * Copies a resource from the classpath to a file location.
     * The destination file will be created if it doesn't exist, or overwritten if it does.
     *
     * @param resource The classpath resource path (without leading slash)
     * @param to The destination file path
     * @throws IOException if the resource doesn't exist or the copy operation fails
     */
    public static void copyFile(String resource, String to) throws IOException {
        copyFile(FileUtils.class, resource, to);
    }

    /**
     * Copies a resource from the classpath to a file location.
     * The destination file will be created if it doesn't exist, or overwritten if it does.
     *
     * @param clazz The classpath in jar
     * @param resource The classpath resource path (without leading slash)
     * @param to The destination file path
     * @throws IOException if the resource doesn't exist or the copy operation fails
     */
    public static void copyFile(Class<?> clazz, String resource, String to) throws IOException {
        File toFile = new File(to);
        ensureParentDir(toFile);
        Files.copy(clazz.getResourceAsStream("/" + resource), toFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Recursively copies a folder from the classpath to a destination directory.
     * This method handles both regular directories and JAR file resources.
     *
     * @param from The classpath resource directory path
     * @param toPath The destination directory path
     * @throws IOException if the source directory doesn't exist or the copy operation fails
     */
    public static void copyFolderRecursively(String from, String toPath) throws IOException {
        copyFolderRecursively(FileUtils.class, from, toPath);
    }

    /**
     * Recursively copies a folder from the classpath to a destination directory.
     * This method handles both regular directories and JAR file resources.
     *
     * @param clazz class in the jar file
     * @param from The classpath resource directory path
     * @param toPath The destination directory path
     * @throws IOException if the source directory doesn't exist or the copy operation fails
     */
    public static void copyFolderRecursively(Class<?> clazz, String from, String toPath) throws IOException {
        ClassLoader classLoader = clazz.getClassLoader();
        URI uri = null;
        try {
            uri = classLoader.getResource(from).toURI();
        } catch (URISyntaxException e) {
            throw new IOException(e.getMessage());
        } catch (NullPointerException e) {
            throw new IOException(e.getMessage());
        }

        if (uri == null) {
            throw new IOException("something is wrong directory or files missing");
        }

        /** jar case */
        URL jar = clazz.getProtectionDomain().getCodeSource().getLocation();
        // jar.toString() begins with file:
        // i want to trim it out...
        Path jarFile = Paths.get(
                URLDecoder.decode(jar.toString(), StandardCharsets.UTF_8).substring("file:".length()));
        FileSystem fs = FileSystems.newFileSystem(jarFile, Map.of());
        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(fs.getPath(from));
        Path to = new File(toPath).toPath();
        for (Path p : directoryStream) {
            InputStream is = clazz.getResourceAsStream("/" + p.toString());
            Path target = to.resolve(p.toString());

            if (!Files.exists(target)) {
                if (!Files.exists(target.getParent())) {
                    Files.createDirectories(target.getParent());
                }
                Files.copy(is, target);
            }
        }
    }

    /**
     * Recursively deletes a directory and all its contents.
     *
     * @param folder The directory to delete
     * @return true if the deletion was successful, false otherwise
     */
    public static boolean deleteDirectory(File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();

            if (files != null) {
                for (File file : files) {
                    // Recursive call to delete files and subfolders
                    if (!deleteDirectory(file)) {
                        return false;
                    }
                }
            }
        }

        // Delete the folder itself
        return folder.delete();
    }

    /**
     * Gets an InputStream for a classpath resource.
     *
     * @param resource The classpath resource path (without leading slash)
     * @return An InputStream for the resource, or null if the resource doesn't exist
     */
    public static InputStream readResource(String resource) {
        return FileUtils.class.getResourceAsStream("/" + resource);
    }

    /**
     * Gets an InputStream for a classpath resource.
     *
     * @param clazz The classpath in jar file
     * @param resource The classpath resource path (without leading slash)
     * @return An InputStream for the resource, or null if the resource doesn't exist
     */
    public static InputStream readResource(Class<?> clazz, String resource) {
        return clazz.getResourceAsStream("/" + resource);
    }

    /**
     * Gets an InputStream for a file at the specified path.
     *
     * @param path The file path
     * @return An InputStream for the file
     * @throws RuntimeException if the file doesn't exist or cannot be opened
     */
    public static InputStream readFile(String path) {
        File file = new File(path);
        return readFile(file);
    }

    /**
     * Gets an InputStream for a file.
     *
     * @param file The file to read
     * @return An InputStream for the file
     * @throws RuntimeException if the file doesn't exist or cannot be opened
     */
    public static InputStream readFile(File file) {
        if (!isAFile(file)) {
            throw new RuntimeException("File does not exists: " + file);
        }
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException fil) {
            throw new RuntimeException(fil);
        }
    }

    /**
     * Reads a classpath resource as a string using UTF-8 encoding.
     *
     * @param resource The classpath resource path (without leading slash)
     * @return The content of the resource as a string
     * @throws RuntimeException if the resource cannot be read
     */
    public static String readResourceString(String resource) {
        try (var inputStream = readResource(resource)) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if a file exists and is a regular file.
     *
     * @param file The file to check
     * @return true if the file exists and is a regular file, false otherwise
     */
    public static boolean isAFile(File file) {
        return file.exists() && file.isFile();
    }

    /**
     * Checks if a file exists and is a directory.
     *
     * @param file The file to check
     * @return true if the file exists and is a directory, false otherwise
     */
    public static boolean isAFolder(File file) {
        return file.exists() && file.isDirectory();
    }

    /**
     * Reads a classpath resource and parses it as JSON.
     *
     * @param resource The classpath resource path (without leading slash)
     * @return The parsed JSON element
     * @throws RuntimeException if the resource cannot be read or parsed
     */
    public static JsonElement readResourceJson(String resource) {
        try (InputStream is = readResource(resource)) {
            JsonReader reader = new JsonReader(new InputStreamReader(is));
            return JsonParser.parseReader(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads a file as a string using UTF-8 encoding.
     *
     * @param path The file path
     * @return The content of the file as a string
     * @throws RuntimeException if the file cannot be read
     */
    public static String readFileString(String path) {
        return readFileString(new File(path));
    }

    /**
     * Reads a file as a string using UTF-8 encoding.
     *
     * @param str The file to read
     * @return The content of the file as a string
     * @throws RuntimeException if the file cannot be read
     */
    public static String readFileString(File str) {
        try (var inputStream = readFile(str)) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads a file and parses it as JSON.
     *
     * @param str The file path
     * @return The parsed JSON element
     * @throws RuntimeException if the file cannot be read or parsed
     */
    public static JsonElement readFileJson(String str) {
        return readFileJson(new File(str));
    }

    /**
     * Reads a file and parses it as JSON.
     *
     * @param str The file to read
     * @return The parsed JSON element
     * @throws RuntimeException if the file cannot be read or parsed
     */
    public static JsonElement readFileJson(File str) {
        try (InputStream is = readFile(str)) {
            JsonReader reader = new JsonReader(new InputStreamReader(is));
            return JsonParser.parseReader(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
