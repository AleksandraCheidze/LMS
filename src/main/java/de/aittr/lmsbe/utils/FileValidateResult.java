package de.aittr.lmsbe.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class FileValidateResult {
    private final boolean isFileExists;
    private final boolean isFileAllowed;
    private final boolean validPath;
}
