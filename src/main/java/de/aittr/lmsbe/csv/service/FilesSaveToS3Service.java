package de.aittr.lmsbe.csv.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class FilesSaveToS3Service {

    private final AmazonS3 amazonS3;
    private final CsvFilesProcessingService csvFilesProcessingService;

    @Value("${csv.bucketNameForCsvFiles}")
    String bucketNameForCsvFiles;


    public String saveCsvFileToS3(MultipartFile file) throws IOException {
        csvFilesProcessingService.parseCsvFile(file);
        InputStream inputStream = file.getInputStream();
        String dateTimeOfUploading = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString();
        String generatedFileName = dateTimeOfUploading + "_" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        PutObjectRequest request = new PutObjectRequest(bucketNameForCsvFiles,
                generatedFileName, inputStream, metadata);
        amazonS3.putObject(request);
        return amazonS3.getUrl(bucketNameForCsvFiles,
                generatedFileName).toString();
    }

    public String saveValidationReportTOS3(PDDocument document, String documentName, String bucketName) throws IOException {

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            document.save(byteArrayOutputStream);
            byte[] pdfBytes = byteArrayOutputStream.toByteArray();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(pdfBytes);
            String documentContentType = documentName.substring(documentName.lastIndexOf(".") + 1);

            String dateTimeOfCreating = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString();
            String generatedReportName = dateTimeOfCreating + "_" + documentName;
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(documentContentType);
            PutObjectRequest request = new PutObjectRequest(bucketName,
                    generatedReportName, byteArrayInputStream, metadata);

            amazonS3.putObject(request);
            return amazonS3.getUrl(bucketName, generatedReportName).toString();
        }
    }
}
