package de.aittr.lmsbe.csv.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvFilesProcessingService {

    public List<String[]> parseCsvFile(MultipartFile file) {

        List<String[]> csvData = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parsed = line.split(";");
                csvData.add(parsed);
            }
        } catch (IOException e) {
            log.error("Error while reading file", e);
        }
        return csvData;
    }

    public String calculatedMd5(byte[] bytes) {
        return DigestUtils.md5DigestAsHex(bytes);
    }

}
