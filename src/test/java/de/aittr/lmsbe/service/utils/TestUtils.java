package de.aittr.lmsbe.service.utils;

import lombok.SneakyThrows;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtils {

    @SneakyThrows
    public static String readFileToString(String filePath) {
        ClassLoader classLoader = TestUtils.class.getClassLoader();
        URI uri = classLoader.getResource(filePath).toURI();
        Path path = Paths.get(uri);
        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes);
    }
}
