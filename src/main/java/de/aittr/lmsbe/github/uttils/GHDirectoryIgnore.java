package de.aittr.lmsbe.github.uttils;

import java.util.List;

/**
 * Utility class that provides a list of paths that should be ignored in a file system or directory structure.
 * These paths are common files and folders that are typically excluded from version control, build systems,
 * and other development processes.
 */
public class GHDirectoryIgnore {

    private GHDirectoryIgnore() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * A list of paths that should be ignored in a file system or directory structure.
     * These paths are common files and folders that are typically excluded from version control, build systems,
     * and other development processes.
     * <p>
     * The IGNORE_PATHS list includes the following paths:
     * - .idea
     * - .git
     * - .vs
     * - .vscode
     * - .gradle
     * - .mvn
     * - node_modules
     * - out
     * - target
     * - DS_Store
     * - Thumbs.db
     * - dist
     * - build
     * - coverage
     * - public
     * - cache
     * <p>
     * Usage:
     * List<String> ignoredPaths = IGNORE_PATHS;
     * <p>
     * Note: This list is not exhaustive and can be customized as per project requirements.
     * It serves as a starting point for excluding commonly unnecessary files and directories.
     */
    public static final List<String> IGNORE_PATHS = List.of(".idea",
            ".git",
            ".vs",
            ".vscode",
            ".gradle",
            ".vscode",
            ".mvn",
            "node_modules",
            "out",
            "target",
            "DS_Store",
            "Thumbs.db",
            "dist",
            "node_modules",
            "out",
            "build",
            "out",
            "coverage",
            "public",
            "cache"
    );
}
