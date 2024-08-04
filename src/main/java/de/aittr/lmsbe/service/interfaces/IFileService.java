package de.aittr.lmsbe.service.interfaces;

import com.amazonaws.services.s3.model.ObjectMetadata;
import de.aittr.lmsbe.exception.BadRequestException;
import de.aittr.lmsbe.exception.RestException;
import de.aittr.lmsbe.github.model.GHLessonType;
import de.aittr.lmsbe.model.Cohort;
import de.aittr.lmsbe.model.User;
import de.aittr.lmsbe.utils.FileValidateResult;

import java.util.List;


public interface IFileService {

    /**
     * Uploads a file to Amazon S3.
     *
     * @param link           the link to the file to be uploaded
     * @param downloadToken  the download token for the file
     * @param bucket         the S3 bucket name
     * @param fileName       the name of the file in the S3 bucket
     * @param objectMetadata metadata for the file
     */
    void uploadToS3(String link, String downloadToken, String bucket, String fileName, ObjectMetadata objectMetadata);

    /**
     * Downloads a file from a given URL.
     *
     * @param fileUrl     the URL of the file to be downloaded
     * @param accessToken the access token for authorization
     * @param filePath    the local path where the file will be saved
     * @throws RuntimeException if an I/O error occurs during the download process or if the response code is not HTTP_OK.
     */
    void downloadFile(String fileUrl, String accessToken, String filePath);

    /**
     * Creates an alias for an existing S3 object.
     *
     * @param bucketName the name of the S3 bucket
     * @param sourceKey  the key of the source object
     * @param aliasKey   the key of the alias
     */
    void createAlias(String bucketName, String sourceKey, String aliasKey);

    /**
     * Retrieves a list of keys from an S3 bucket that match a given prefix.
     *
     * @param bucketName the name of the S3 bucket
     * @param prefix     the prefix to match
     * @return a list of keys that match the prefix
     */
    List<String> getKeysByPrefix(String bucketName, String prefix);

    /**
     * Retrieves presigned links for S3 objects based on the given parameters and user authorization.
     *
     * @param cohortAlias the alias of the cohort
     * @param module      the module name
     * @param type        the type of video
     * @param lessonsNr   the lesson number
     * @param user        the user requesting the links
     * @return a list of presigned links
     * @throws BadRequestException if the {@code lessonsNr} parameter is not a positive integer.
     * @throws RestException       if the user does not have access to the specified cohort, resulting in a forbidden access error.
     */
    List<String> getPresignedLinksByPrefixByAuthUser(String cohortAlias,
                                                     String module,
                                                     String type,
                                                     String lessonsNr,
                                                     User user);


    /**
     * Checks if file is allowed for a given user and cohort.
     *
     * @param user   the user for whom the video permission is being checked
     * @param cohort the cohort to which the user belongs
     * @return true if file is allowed for the user, false otherwise
     */
    boolean isFileAllowedForUser(User user, Cohort cohort);

    /**
     * Checks if a file exists in the specified path.
     *
     * @param pathToFile the path to the file
     * @return true if the file exists, false otherwise
     */
    boolean isFileExists(final String pathToFile);

    /**
     * Validates the file for the specified parameters.
     *
     * @param moduleName         the module name
     * @param lessonNr           the lesson number
     * @param currentUser        the current user
     * @param ghLessonType       the GitHub lesson type
     * @param existingCohortRepo the existing cohort repository
     * @return the file validation result
     */
    FileValidateResult fileValidateResult(String moduleName,
                                          String lessonNr,
                                          User currentUser,
                                          GHLessonType ghLessonType,
                                          Cohort existingCohortRepo);

    /**
     * Builds the S3 file name based on the specified parameters.
     *
     * @param cohort    the cohort
     * @param module    the module name
     * @param type      the type of video
     * @param lessonsNr the lesson number
     * @return the S3 file name
     */
    String buildS3FileName(Cohort cohort, String module, String type, String lessonsNr);

    /**
     * Retrieves presigned links for S3 objects that match a given prefix.
     *
     * @param bucketName the name of the S3 bucket
     * @param prefix     the prefix to match
     * @return a list of presigned links
     */
    List<String> getPresignedLinksByPrefix(String bucketName, String prefix);

    /**
     * Retrieves a presigned link for an S3 object by its key.
     *
     * @param bucketName the name of the S3 bucket
     * @param key        the key of the S3 object
     * @return the presigned link
     */
    String getPresignedLinkByKey(String bucketName, String key);

}
