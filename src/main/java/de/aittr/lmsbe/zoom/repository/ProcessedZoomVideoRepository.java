package de.aittr.lmsbe.zoom.repository;

import de.aittr.lmsbe.zoom.entity.ProcessedZoomVideo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedZoomVideoRepository extends JpaRepository<ProcessedZoomVideo, Long> {
}
