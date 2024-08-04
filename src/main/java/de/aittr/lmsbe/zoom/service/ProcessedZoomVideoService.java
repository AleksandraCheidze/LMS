package de.aittr.lmsbe.zoom.service;

import de.aittr.lmsbe.zoom.entity.ProcessedZoomVideo;
import de.aittr.lmsbe.zoom.repository.ProcessedZoomVideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessedZoomVideoService {

    private final ProcessedZoomVideoRepository processedZoomVideoRepository;

    public List<ProcessedZoomVideo> saveProcessedZoomVideos(List<ProcessedZoomVideo> processedZoomVideos) {
        return processedZoomVideoRepository.saveAll(processedZoomVideos);
    }
}
