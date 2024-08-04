package de.aittr.lmsbe.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import de.aittr.lmsbe.exception.BadRequestException;
import de.aittr.lmsbe.exception.RestException;
import de.aittr.lmsbe.github.model.GHLessonType;
import de.aittr.lmsbe.model.Cohort;
import de.aittr.lmsbe.model.User;
import de.aittr.lmsbe.service.cohort.CohortService;
import de.aittr.lmsbe.service.file_rules.HighLevelRoleRule;
import de.aittr.lmsbe.service.file_rules.StudentRoleRule;
import de.aittr.lmsbe.service.interfaces.IFileService;
import de.aittr.lmsbe.utils.FileValidateResult;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static de.aittr.lmsbe.github.model.GHLessonType.LESSON;
import static de.aittr.lmsbe.utils.LessonNumberUtils.extractNumericPart;
import static de.aittr.lmsbe.utils.LessonNumberUtils.isLessonNumberValid;

@Profile("!dev")
@Service
@RequiredArgsConstructor
@Slf4j
public class FileService implements IFileService {

    public static final String USER_METADATA_ALIAS_FOR = "alias-for";
    private static final int CONNECT_TIMEOUT = 50_000;
    private static final int READ_TIMEOUT = 50_000;
    private static final long ONE_MINUTE_IN_MILLIS = 60000;

    private final AmazonS3 s3Service;
    private final CohortService cohortService;

    @Value("${video-expiration-minutes}")
    private int videoExpirationMinutes;

    @Value("${lesson-video-bucket-name}")
    private String lessonVideoBucketName;
    @Value("${default-video-type}")
    private String defaultVideoType;
    @Value("${default-video-cohort-prefix}")
    private String defaultVideoCohortPrefix;

    @SneakyThrows
    @Async
    @Override
    public void uploadToS3(String link, String downloadToken, String bucket, String fileName, ObjectMetadata objectMetadata) {
        String temporaryFileName = UUID.randomUUID().toString();
        log.info("Downloading file from url:" + link);
        downloadFile(link, downloadToken, temporaryFileName);
        log.info("Downloaded file from url:" + link);
        File file = new File(temporaryFileName);
        log.info("Uploading file: " + fileName + " to the bucket: " + bucket);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileName, file);
        putObjectRequest.setMetadata(objectMetadata);
        PutObjectResult result = s3Service.putObject(putObjectRequest);
        log.info("Result of uploading file " + fileName + ": " + result.getContentMd5());
        Files.delete(Path.of(file.getPath()));
    }


    @Override
    public void downloadFile(String fileUrl, String accessToken, String filePath) {

        try {
            URL url = new URL(fileUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);

            // Set basic authorization header
            String authHeaderValue = "Bearer " + accessToken;
            conn.setRequestProperty("Authorization", authHeaderValue);

            // Open connection and check response code
            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Create input stream from connection
                InputStream inputStream = new BufferedInputStream(conn.getInputStream());

                // Create output stream to save the file
                FileOutputStream fileOutputStream = new FileOutputStream(filePath);

                // Read data from input stream and write to output stream
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }

                // Close streams
                fileOutputStream.close();
                inputStream.close();

                log.info("File " + fileUrl + " downloaded successfully.");
            } else {
                throw new RuntimeException("Failed to download file " + fileUrl + "Response code: " + responseCode);
            }

            // Disconnect the connection
            conn.disconnect();
        } catch (IOException e) {
            throw new RuntimeException("Failed to download file " + fileUrl, e);
        }
    }

    @Override
    public void createAlias(String bucketName, String sourceKey, String aliasKey) {
        // Create an empty metadata file for the alias
        ObjectMetadata aliasMetadata = new ObjectMetadata();

        // Set the metadata of the alias file to match the original object
        aliasMetadata.setUserMetadata(Map.of(USER_METADATA_ALIAS_FOR, sourceKey));

        // Create the alias object with the empty content and assigned metadata
        PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucketName,
                aliasKey,
                new ByteArrayInputStream(new byte[0]),
                aliasMetadata
        );
        s3Service.putObject(putObjectRequest);
    }


    @Override
    public List<String> getKeysByPrefix(String bucketName, String prefix) {
        ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(prefix);

        ListObjectsV2Result response = s3Service.listObjectsV2(listObjectsRequest);

        List<String> keys = new ArrayList<>();

        for (S3ObjectSummary objectSummary : response.getObjectSummaries()) {
            keys.add(objectSummary.getKey());
        }
        return keys;
    }


    @Override
    public List<String> getPresignedLinksByPrefixByAuthUser(String cohortAlias,
                                                            String module,
                                                            String type,
                                                            String lessonsNr,
                                                            User user) {

        if (!(lessonsNr.matches("\\d+")) || Integer.parseInt(lessonsNr) < 0) {
            throw new BadRequestException("Invalid request parameters: < " + lessonsNr + " >");
        }

        final Cohort selectedCohort = cohortService.getCohortByRepoNameOrThrow(cohortAlias);

        if (isFileAllowedForUser(user, selectedCohort)) {
            final String prefix = buildS3FileName(selectedCohort, module, type, lessonsNr);
            return getPresignedLinksByPrefix(lessonVideoBucketName, prefix);
        }

        String errorMsg = String.format("User with email <%s> does not have access to the video cohort with alias <%s>. Access denied.",
                user.getEmail(), selectedCohort.getName());
        throw new RestException(HttpStatus.FORBIDDEN, errorMsg);
    }


    @Override
    public boolean isFileAllowedForUser(final User user, final Cohort cohort) {
        log.debug("Checking video permissions...");

        if (user == null || cohort == null) {
            log.warn("Invalid input: user or cohort was null.");
            return false;
        }

        User.Role userRole = user.getRole();
        boolean result = new HighLevelRoleRule().test(user) || new StudentRoleRule().test(user, cohort);

        final String logPrefix = String.format("User: %s, Role: %s, Cohort: %s - ", user.getEmail(), userRole, cohort.getName());
        log.debug("{} Permission: {}", logPrefix, result ? "granted" : "denied");

        return result;
    }


    @Override
    public boolean isFileExists( String pathToFile) {
        log.debug("Checking existence of a file at location: {}", pathToFile);
        ListObjectsV2Request request = new ListObjectsV2Request()
                .withBucketName(lessonVideoBucketName)
                .withPrefix(pathToFile)
                .withMaxKeys(1);

        List<S3ObjectSummary> objects = s3Service.listObjectsV2(request).getObjectSummaries();
        boolean fileExists = !objects.isEmpty();
        if (fileExists) {
            log.debug("File found at location: {}. Number of matching objects: {}", pathToFile, objects.size());
        } else {
            log.debug("File not found at location: {}", pathToFile);
        }
        return fileExists;
    }


    @Override
    public FileValidateResult fileValidateResult(String moduleName,
                                                 String lessonNr,
                                                 User currentUser,
                                                 GHLessonType ghLessonType,
                                                 Cohort existingCohortRepo) {
        if (currentUser == null) {
            return new FileValidateResult(false, false, false);
        }
        final boolean isVideoAllowed = isFileAllowedForUser(currentUser, existingCohortRepo);
        final boolean validLessonNumber = isLessonNumberValid(lessonNr);
        boolean isFileExists = false;
        if (isVideoAllowed && validLessonNumber) {
            final String lessonNrReq = extractNumericPart(lessonNr);
            final String type = LESSON.equals(ghLessonType) ? "" : ghLessonType.getPath();
            log.debug("Validating video, Lesson number: {}, Github Lesson Type: {}, Module Name: {}, Cohort Repo {}",
                    lessonNrReq, ghLessonType, moduleName, existingCohortRepo);
            final String s3FilePath = buildS3FileName(existingCohortRepo, moduleName, type, lessonNrReq);
            isFileExists = isFileExists(s3FilePath);
        }
        log.debug("Validation complete. File exists: {}, Video allowed: {}, Valid lesson number: {}",
                isFileExists, isVideoAllowed, validLessonNumber);
        return new FileValidateResult(isFileExists, isVideoAllowed, validLessonNumber);
    }

    @Override
    public String buildS3FileName(Cohort cohort,
                                  String module,
                                  String type,
                                  String lessonsNr) {
        final String modifiedFileName = cohort.getName().replaceAll("(?i)cohort", "").trim();
        final String videoType = Strings.isEmpty(type) ? defaultVideoType : type;
        return defaultVideoCohortPrefix + modifiedFileName + "/" + module + "/" + videoType + "/" + lessonsNr + "/";
    }


    @Override
    public List<String> getPresignedLinksByPrefix(String bucketName, String prefix) {
        ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(prefix);

        ListObjectsV2Result response = s3Service.listObjectsV2(listObjectsRequest);

        List<String> sharedLinks = new ArrayList<>();

        for (S3ObjectSummary objectSummary : response.getObjectSummaries()) {
            String objectKey = objectSummary.getKey();
            if (objectKey.endsWith("/")) {
                continue;
            }
            if (isAliasObject(bucketName, objectKey)) {
                String originalFileLink = getPresignedLinkByKey(bucketName, objectKey);
                sharedLinks.add(originalFileLink);
            } else {
                GeneratePresignedUrlRequest presignedUrlRequest =
                        new GeneratePresignedUrlRequest(bucketName, objectKey);
                presignedUrlRequest.setExpiration(addMinutesToDate(videoExpirationMinutes, new Date()));
                URL presignedUrl = s3Service.generatePresignedUrl(presignedUrlRequest);
                sharedLinks.add(presignedUrl.toString());
            }
        }
        return sharedLinks;
    }

    /**
     * Checks if the specified object in the given S3 bucket is an alias.
     *
     * @param bucketName The name of the S3 bucket.
     * @param objectKey  The key of the object to check.
     * @return {@code true} if the object is an alias, {@code false} otherwise.
     * @throws NullPointerException if {@code bucketName} or {@code objectKey} is {@code null}.
     * @throws AmazonS3Exception    if an error occurs while interacting with Amazon S3.
     */
    private boolean isAliasObject(String bucketName, String objectKey) {
        try {
            ObjectMetadata objectMetadata = s3Service.getObjectMetadata(bucketName, objectKey);
            return objectMetadata.getUserMetadata().containsKey(USER_METADATA_ALIAS_FOR);
        } catch (AmazonS3Exception e) {
            return false;
        }
    }


    @Override
    public String getPresignedLinkByKey(String bucketName, String key) {
        ObjectMetadata objectMetadata = s3Service.getObjectMetadata(bucketName, key);
        if (objectMetadata.getUserMetadata().containsKey(USER_METADATA_ALIAS_FOR)) {
            key = objectMetadata.getUserMetaDataOf(USER_METADATA_ALIAS_FOR);
        }
        GeneratePresignedUrlRequest presignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, key);
        presignedUrlRequest.setExpiration(addMinutesToDate(videoExpirationMinutes, new Date()));
        return s3Service.generatePresignedUrl(presignedUrlRequest).toString();
    }

    /**
     * Adds the specified number of minutes to the given date.
     *
     * @param minutes    The number of minutes to add.
     * @param beforeTime The date to which the minutes will be added.
     * @return A new {@code Date} object representing the date and time after adding the specified minutes.
     */
    public static Date addMinutesToDate(int minutes, Date beforeTime) {
        long curTimeInMs = beforeTime.getTime();
        return new Date(curTimeInMs
                + (minutes * ONE_MINUTE_IN_MILLIS));
    }
}
