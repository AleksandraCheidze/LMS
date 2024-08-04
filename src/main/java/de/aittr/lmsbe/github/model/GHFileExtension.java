package de.aittr.lmsbe.github.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * The GithubFileExtension enum represents different file extensions and their associated properties.
 * Each enum value has a language and a flag indicating whether the file is readable or not.
 */
@RequiredArgsConstructor
@Getter
public enum GHFileExtension {

    REACT_JSX("jsx", "jsx", true),
    REACT_TSX("tsx", "tsx", true),
    TS("ts", "typescript", true),
    JS("js", "javascript", true),
    JSX("js", "javascript", true),

    JAVA("java", "java", true),
    GRADLE("gradle", "gradle", true),
    CLASS("class", "java", false),

    XML("xml", "xml", true),
    YAML("yaml", "yaml", true),
    YML("yml", "yml", true),
    PROPERTIES("properties", "properties", true),

    HTTP("http", "http request", true),

    SQL("sql", "sql", true),

    HTML("html", "html", true),
    XHTML("xhtml", "xhtml", true),

    CSS("css", "css", true),
    LESS("less", "less", true),
    SCSS("scss", "scss", true),
    SASS("sass", "sass", true),
    POST_CSS("pcss", "postcss", true),

    FREE_MARKER("ftl", "ftl", true),

    GITIGNORE("gitignore", "gitignore", true),
    DOCKER_FILE("dockerfile", "dockerfile", true),
    DOCKER_IGNORE("dockerignore", "dockerignore", true),

    MD("md", "markdown", true),
    JSON("json", "json", true),

    PDF("pdf", "", false),
    POWER_POINT("pptx", "", false),
    EXCEL("excel", "", false),
    WORD_DOCX("docx", "", false),
    WORD_DOC("doc", "", false),
    CSV("csv", "csv", true),
    EXCEL_XLSX("xlsx", "", false),
    EXCEL_XLS("xls", "", false),

    TXT("txt", "text", true),

    JPEG("jpeg", "jpeg", false),
    JPG("jpg", "jpg", false),
    PNG("png", "png", false),
    ICO("ico", "ico", false),
    GIF("gif", "gif", false),
    TIF("tif", "tif", false),
    TIFF("tiff", "tiff", false),
    WEBP("webp", "webp", false),
    EPS("eps", "eps", false),
    SVG("svg", "svg", false),
    PSD("psd", "psd", false),
    INDD("indd", "indd", false),
    CDR("cdr", "cdr", false),
    RAW("raw", "raw", false),
    BMP("bmp", "bmp", false),
    AVIF("avif", "avif", false),
    TGA("tga", "tga", false),

    UNKNOWN("", "", false);

    /**
     * Represents the file extension of a file.
     */
    private final String extension;

    /**
     * Represents the language used in the software.
     * <p>
     * The variable "language" stores the name of the language as a string.
     * This variable is declared as private and final, which means it cannot be changed
     * once it is assigned a value and can only be accessed within the class it is declared.
     * <p>
     * Example usage:
     * <p>
     * Language language = new Language("Java");
     * String selectedLanguage = language.getLanguage();
     * <p>
     * In the above example, we create a Language object with the name "Java".
     * We can retrieve the selected language using the "getLanguage()" method and store it
     * in a variable called "selectedLanguage".
     */
    private final String language;

    /**
     * Indicates whether the variable is readable or not.
     *
     * @return true if the variable is readable, false otherwise.
     */
    private final boolean isReadable;

    /**
     * Returns the GithubFileExtension based on the file extension of the given fileName.
     * If the fileName is blank, it returns UNKNOWN as the default extension.
     *
     * @param fileName the name of the file including its extension
     * @return the corresponding GithubFileExtension based on the file extension
     */
    public static GHFileExtension languageByExtension(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return UNKNOWN;
        }
        final String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        return Arrays.stream(GHFileExtension.values())
                .filter(githubFileExtension -> extension.equalsIgnoreCase(githubFileExtension.getExtension()))
                .findFirst()
                .orElse(UNKNOWN);
    }

    /**
     * Returns a list of supported image extensions.
     *
     * @return a list of GHFileExtension objects representing the supported image extensions.
     */
    public static List<GHFileExtension> getImageExtension() {
        return List.of(GHFileExtension.JPEG,
                GHFileExtension.JPG,
                GHFileExtension.PNG,
                GHFileExtension.ICO,
                GHFileExtension.GIF,
                GHFileExtension.WEBP,
                GHFileExtension.SVG,
                GHFileExtension.BMP
        );
    }
}
