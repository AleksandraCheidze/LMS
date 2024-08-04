package de.aittr.lmsbe.github.service;

import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHContent;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static de.aittr.lmsbe.github.uttils.GHHelper.PATH_SEPARATOR;

/**
 * This class represents a service for zipping GHContent objects into a byte array.
 */
@Service
@RequiredArgsConstructor
public class GHZipService {

    /**
     * This variable represents the base64 encoding scheme.
     */
    public static final String BASE64_ENCODING = "base64";

    private final GhDownloadService ghDownloadService;


    /**
     * Zips a list of GHContent objects into a byte array.
     *
     * @param fileList the list of GHContent objects to be zipped
     * @return a ByteArrayOutputStream containing the zipped file
     * @throws IOException if an I/O error occurs during the zip operation
     */
    public ByteArrayOutputStream zipFilesToByteArray(List<GHContent> fileList) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(baos)) {
            for (GHContent fileContent : fileList) {
                if (fileContent == null || fileContent.getPath() == null) {
                    continue;
                }
                createZipEntry(zipOut, fileContent);
            }
            return baos;
        }
    }

    /**
     * Creates a new entry in the ZipOutputStream for the given GHContent file.
     *
     * @param zipOut      the ZipOutputStream to add the entry to
     * @param fileContent the GHContent object representing the file to be added
     * @throws IOException if an I/O error occurs while adding the entry to the ZipOutputStream
     */
    private void createZipEntry(ZipOutputStream zipOut, GHContent fileContent) throws IOException {
        String path = fileContent.getPath();
        String modifiedPath = modifyZipDirPath(path);
        zipOut.putNextEntry(new ZipEntry(modifiedPath));

        processFileContent(zipOut, fileContent);
        zipOut.closeEntry();
    }

    /**
     * Processes the content of a file and writes it to a ZipOutputStream.
     *
     * @param zipOut      the ZipOutputStream to write the file content to
     * @param fileContent the GHContent object representing the file
     * @throws IOException if an I/O error occurs during the file processing
     */
    private void processFileContent(ZipOutputStream zipOut, GHContent fileContent) throws IOException {
        if (BASE64_ENCODING.equals(fileContent.getEncoding())) {
            try (InputStream inputStream = fileContent.read()) {
                byte[] bytes = new byte[512];
                int length;
                while ((length = inputStream.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
            }
        } else {
            byte[] fileBytes = ghDownloadService.downloadFile(fileContent.getDownloadUrl());
            zipOut.write(fileBytes);
        }
    }

    /**
     * Modifies a given path by removing the first three parts.
     *
     * @param path the original path to modify
     * @return the modified path with the first three parts removed
     */
    private String modifyZipDirPath(String path) {
        final String[] parts = path.split(PATH_SEPARATOR);
        if (parts.length > 3) {
            String[] newPathParts = Arrays.copyOfRange(parts, 3, parts.length);
            return String.join(PATH_SEPARATOR, newPathParts);
        } else {
            return path;
        }
    }
}
