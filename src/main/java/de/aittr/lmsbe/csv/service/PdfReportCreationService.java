package de.aittr.lmsbe.csv.service;

import de.aittr.lmsbe.csv.helpers.FinalReportParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfReportCreationService {

    public PDDocument createPdfReportOfValidation(List<CsvValidationResult> validationResultList) {

        List<String> validationResult = getValidationResult(validationResultList);
        PDDocument document = new PDDocument();

        try {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            try {
                float margin = 30;
                float yPosition = page.getMediaBox().getHeight() - margin;
                float lineHeight = 15;
                int linesOnPage = 50;
                int currentLine = 0;
                int fontSize = 10;
                PDFont font = PDType1Font.COURIER;

                contentStream.beginText();
                contentStream.setFont(font, 20);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Report of validation");
                contentStream.endText();

                yPosition -= 30;

                for (String result : validationResult) {
                    if (currentLine >= linesOnPage) {
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        contentStream.close();
                        contentStream = new PDPageContentStream(document, page);
                        yPosition = page.getMediaBox().getHeight() - margin;
                        currentLine = 0;
                    }

                    if (yPosition - fontSize < page.getMediaBox().getLowerLeftY()) {
                        yPosition = page.getMediaBox().getUpperRightY() - margin;
                    }

                    contentStream.beginText();
                    contentStream.setFont(font, fontSize);
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText(result);
                    contentStream.endText();

                    yPosition -= lineHeight;
                    currentLine++;
                }
            } finally {
                contentStream.close();
            }

        } catch (IOException e) {
            log.error("Error while creating pdf-report of validation", e);
        }
        return document;
    }

    private List<String> getValidationResult(List<CsvValidationResult> validationResultList) {
        List<String> validationResult = new ArrayList<>();
        for (CsvValidationResult csvValidationResult : validationResultList) {
            int lineNr = csvValidationResult.getLineNr();
            List<CsvFieldError> errors = csvValidationResult.getErrors();
            if (errors.isEmpty()) {
                validationResult.add(String.format("Line: %d, has no errors", lineNr));
                continue;
            }

            for (int i = 0; i < errors.size(); i++) {
                CsvFieldError csvFieldError = errors.get(i);
                int errorNr = (i + 1);
                validationResult.add(String.format("Line: %d, error: %d column %s, error: %s",
                        lineNr, errorNr, csvFieldError.getColumnName(), csvFieldError.getMessage()));
            }
        }
        return validationResult;
    }

    public PDDocument createReportOfImport(FinalReportParams reportParams,
                                           String task,
                                           String result) {
        PDDocument document = new PDDocument();


        try {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                float margin = 30;
                float yPosition = page.getMediaBox().getHeight() - margin;

                contentStream.beginText();
                contentStream.setFont(PDType1Font.COURIER, 20);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Report of import");
                contentStream.endText();

                yPosition -= 30;

                contentStream.beginText();
                contentStream.setFont(PDType1Font.COURIER, 12);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("File name: " + reportParams.getFileName());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Cloud file name: " + reportParams.getCloudFileName());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Validation report: " + reportParams.getCloudValidationFileName());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("File size: " + reportParams.getFileSize() + " bytes");
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("MD5: " + reportParams.getFileMD5());
                contentStream.newLineAtOffset(0, -20);
                contentStream.endText();

                yPosition -= 57;
                contentStream.moveTo(margin, yPosition - 30);
                contentStream.lineTo(page.getMediaBox().getWidth() - margin, yPosition - 30);
                contentStream.stroke();

                yPosition -= 50;
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Import date: " + reportParams.getUploadDate().toLocalDate());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Import time: " + reportParams.getUploadDate().toLocalTime().truncatedTo(ChronoUnit.SECONDS));
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Added by user: " + reportParams.getUploadUserEmail());
                contentStream.endText();

                yPosition -= 20;
                contentStream.moveTo(margin, yPosition - 30);
                contentStream.lineTo(page.getMediaBox().getWidth() - margin, yPosition - 30);
                contentStream.stroke();

                yPosition -= 50;
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Errors number: " + reportParams.getErrorCounter());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Number of lines in file except title: " + reportParams.getNumberOfLines());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Task: " + task);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Added users number: " + reportParams.getAddedUsers());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Result: " + result);
                contentStream.endText();
            }

        } catch (IOException e) {
            log.error("Error while creating pdf-report of import", e);
        }

        return document;
    }
}
