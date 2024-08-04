package de.aittr.lmsbe.zoom.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import de.aittr.lmsbe.service.interfaces.IFileService;
import de.aittr.lmsbe.zoom.dto.RecordingCompletedDto;
import de.aittr.lmsbe.zoom.entity.ProcessedZoomVideo;
import de.aittr.lmsbe.zoom.model.LessonTopicObject;
import de.aittr.lmsbe.zoom.model.json.Payload;
import de.aittr.lmsbe.zoom.model.json.RecordingFile;
import de.aittr.lmsbe.zoom.model.json.ZoomObjectData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static de.aittr.lmsbe.utils.AppUtils.*;
import static de.aittr.lmsbe.zoom.utils.S3FileNameUtils.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ZoomEventProcessingService {

    private static final String MP_4 = ".mp4";
    private static final String PART = "_part";

    private final IFileService fileService;
    private final JsObjectService jsObjectService;
    private final ZoomMeetingService zoomMeetingService;

    @Value("${lesson-video-bucket-name}")
    private String lessonVideoBucketName;

    public void processRecordingCompleteEvent(RecordingCompletedDto recordingCompletedDto) {
        Payload payload = recordingCompletedDto.getPayload();
        ZoomObjectData zoomObjectData = payload.getZoomObjectData();
        log.debug("Processing recording complete event for '{}', '{}'", zoomObjectData.getTopic(), zoomObjectData.getUuid());
        String downloadToken = recordingCompletedDto.getDownloadToken();

        List<RecordingFile> files = payload.getZoomObjectData().getRecordingFiles().stream()
                .filter(recordingFile -> recordingFile.getFileExtension().equalsIgnoreCase("MP4"))
                .collect(Collectors.toList());
        LessonTopicObject lessonTopicObject = jsObjectService.topicToLessonTopicObject(zoomObjectData.getTopic());

        if (isValidLessonTopicObject(lessonTopicObject)) {
            log.debug("Uploading files '{}', topic: '{}' , for cohorts: '{}'",
                    files.size(), zoomObjectData.getTopic(), lessonTopicObject.getCohort());
            uploadVideoBatchAndCreateAliases(lessonTopicObject, zoomObjectData, files, downloadToken);
        } else {
            log.warn("Invalid lesson topic '{}' Uploading files for invalid object.", zoomObjectData.getTopic());
            uploadFilesForInvalidLessonTopicObject(files, downloadToken, zoomObjectData);
        }
    }

    private void uploadFilesForInvalidLessonTopicObject(List<RecordingFile> files,
                                                        String downloadToken,
                                                        ZoomObjectData zoomObjectData) {
        String fileNamePrefix = createFileNamePrefixForS3StorageInvalidTopic(
                zoomObjectData.getStartTime(),
                zoomObjectData.getHostEmail(),
                zoomObjectData.getId(),
                zoomObjectData.getTopic()
        );

        List<ProcessedZoomVideo> zoomMeetings = new ArrayList<>();
        for (int part = 0; part < files.size(); part++) {
            RecordingFile recordingFile = files.get(part);
            String fileNameOnS3 = fileNamePrefix + (files.size() > 1 ? PART + (part + 1) : Strings.EMPTY) + MP_4;

            ObjectMetadata objectMetadata = objectToS3ObjectMetadataForInvalidFile(zoomObjectData, recordingFile);

            String downloadUrl = recordingFile.getDownloadUrl();
            fileService.uploadToS3(downloadUrl, downloadToken, lessonVideoBucketName, fileNameOnS3, objectMetadata);

            zoomMeetings.add(getProcessedZoomVideo(zoomObjectData, 0, recordingFile, fileNameOnS3, false));
        }
        zoomMeetingService.saveInvalidMeeting(zoomObjectData, zoomMeetings);
    }

    private void uploadVideoBatchAndCreateAliases(LessonTopicObject lessonTopicObject,
                                                  ZoomObjectData zoomObjectData,
                                                  List<RecordingFile> files,
                                                  String downloadToken) {
        List<ProcessedZoomVideo> processedZoomVideos = new ArrayList<>();
        List<String> firstCohortVideoFileNames = new LinkedList<>();
        String fileNamePrefix = createFileNamePrefixForS3StorageValidTopic(lessonTopicObject, zoomObjectData.getStartTime());

        List<String> objectCohorts = lessonTopicObject.getCohort();
        for (int cohortPart = 0; cohortPart < objectCohorts.size(); cohortPart++) {
            String directoryPrefixForS3Storage = getDirectoryPrefixForValidTopic(lessonTopicObject, zoomObjectData, cohortPart);

            for (int part = 0; part < files.size(); part++) {
                final RecordingFile recordingFile = files.get(part);
                String partOfFileSuffix = files.size() > 1 ? PART + (part + 1) : Strings.EMPTY;
                String fileNameOnS3 = directoryPrefixForS3Storage + fileNamePrefix + partOfFileSuffix + MP_4;
                final String currentCohort = objectCohorts.get(cohortPart);

                if (cohortPart == 0) {
                    // Upload the video for the first cohort
                    String downloadUrl = files.get(part).getDownloadUrl();
                    log.info("Uploading files for cohort: {}", currentCohort);
                    final ObjectMetadata objectMetadata = objectToS3ObjectMetadataForValidFile(zoomObjectData, lessonTopicObject);
                    fileService.uploadToS3(downloadUrl, downloadToken, lessonVideoBucketName, fileNameOnS3, objectMetadata);
                    firstCohortVideoFileNames.add(fileNameOnS3);
                    processedZoomVideos.add(getProcessedZoomVideo(zoomObjectData, (part + 1), recordingFile, fileNameOnS3, true));
                } else {
                    // Create alias for subsequent cohorts
                    log.info("Video is for several groups. Uploading alias files for cohort: {}", currentCohort);
                    fileService.createAlias(lessonVideoBucketName, getFirstCohortFileName(firstCohortVideoFileNames, part), fileNameOnS3);
                    processedZoomVideos.add(getProcessedZoomVideo(zoomObjectData, (part + 1), recordingFile, fileNameOnS3, true));
                }
            }
        }
        zoomMeetingService.saveExternMeeting(zoomObjectData, processedZoomVideos, lessonTopicObject);
    }

    private ProcessedZoomVideo getProcessedZoomVideo(final ZoomObjectData zoomObjectData,
                                                     int part,
                                                     final RecordingFile recordingFile,
                                                     final String fileNameOnS3,
                                                     boolean isValid) {
        log.debug("Create ProcessedZoomVideo for zoomObjectData {}", zoomObjectData);
        log.debug("Create ProcessedZoomVideo for recordingFile: {}", recordingFile);
        log.debug("Create ProcessedZoomVideo with part: '{}', flag is valid: '{}', s3 file name: '{}'", part, isValid, fileNameOnS3);
        ProcessedZoomVideo processedZoomVideo = new ProcessedZoomVideo(
                zoomObjectData.getUuid(),
                part,
                recordingFile.getId(),
                recordingFile.getRecordingStart(),
                recordingFile.getRecordingEnd(),
                zoomObjectData.getShareUrl(),
                zoomObjectData.getPassword(),
                zoomObjectData.getHostEmail(),
                lessonVideoBucketName,
                fileNameOnS3,
                isValid
        );
        log.info("Processed zoom video created: {}", processedZoomVideo);
        return processedZoomVideo;
    }
}
