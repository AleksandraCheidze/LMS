package de.aittr.lmsbe.github.uttils;

import de.aittr.lmsbe.github.model.GHFileExtension;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This class tests the GithubFileExtension class, specifically the languageByExtension method.
 * The languageByExtension method is used to determine the corresponding language of a given file extension.
 */
class GHFileExtensionTest {

    /**
     * Test that the languageByExtension method correctly identifies 'java' files.
     */
    @Test
    void testLanguageByExtensionWithJavaFile() {
        // Arrange
        String fileName = "Sample.java";

        // Act
        GHFileExtension result = GHFileExtension.languageByExtension(fileName);

        // Assert
        assertEquals(GHFileExtension.JAVA, result);
    }

    /**
     * Test that the languageByExtension method correctly identifies 'jpeg' files.
     */
    @Test
    void testLanguageByExtensionWithJpegFile() {
        // Arrange
        String fileName = "image.jpeg";

        // Act
        GHFileExtension result = GHFileExtension.languageByExtension(fileName);

        // Assert
        assertEquals(GHFileExtension.JPEG, result);
    }

    /**
     * Test that the languageByExtension method correctly identifies files with unknown or undefined extensions.
     */
    @Test
    void testLanguageByExtensionWithUnknownExtension() {
        // Arrange
        String fileName = "file.unknown";

        // Act
        GHFileExtension result = GHFileExtension.languageByExtension(fileName);

        // Assert
        assertEquals(GHFileExtension.UNKNOWN, result);
    }

    /**
     * Test that the languageByExtension method correctly identifies files without any extensions.
     */
    @Test
    void testLanguageByExtensionWithoutExtension() {
        // Arrange
        String fileName = "file";

        // Act
        GHFileExtension result = GHFileExtension.languageByExtension(fileName);

        // Assert
        assertEquals(GHFileExtension.UNKNOWN, result);
    }
}
