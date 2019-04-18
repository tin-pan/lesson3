package org.level.up.json.impl;

import org.level.up.json.JsonDeserialised;
import org.level.up.json.JsonSerializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonStorage {

    static Map<String, JsonSerializer> serializerMap;

    static {
        try{
            loadClasses();
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    private static void loadClasses() throws IOException {
        Collection<Class<?>> classes = scanDirectory();

        serializerMap = classes.stream()
                .filter(clazz -> {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    return Arrays.stream(interfaces)
                            .anyMatch(c -> c == JsonSerializer.class);
                })
                .collect(Collectors.toMap(clazz -> clazz.getSimpleName(), clazz -> newInstance(clazz)));


    }

    private static JsonSerializer<?> newInstance(Class<?> clazz) {
        try {
            return (JsonSerializer) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Collection<Class<?>> scanDirectory() throws IOException {
        return Files.walk(Paths.get("src/main/java/org/level/up/java"))
                .map(Path::toFile)
                .filter(File::isFile)
                .map(File::toString)
                .map(filename -> filename.replace("src/main/java/", "").replace("/", "").replace("\\", "").replace(".java", ""))
                .map(JsonStorage::loadClass)
                .collect(Collectors.toList());
    }

    private static Class<?> loadClass(String fullName) {
        try {
            return Class.forName(fullName);
        } catch (ClassNotFoundException exc) {
            throw new RuntimeException(exc);
        }
    }
}
