package de.aittr.lmsbe.github.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.aittr.lmsbe.dto.LessonDto;
import de.aittr.lmsbe.exception.RestException;
import de.aittr.lmsbe.github.dto.GHJsonMeta;
import de.aittr.lmsbe.github.dto.LessonCode;
import de.aittr.lmsbe.github.dto.LessonMeta;
import de.aittr.lmsbe.github.model.GHLessonType;
import de.aittr.lmsbe.github.model.GHUploadFile;
import de.aittr.lmsbe.github.model.MyGHContent;
import de.aittr.lmsbe.github.uttils.GHBranchHandlerService;
import de.aittr.lmsbe.github.uttils.GHHelper;
import de.aittr.lmsbe.model.Cohort;
import de.aittr.lmsbe.model.User;
import de.aittr.lmsbe.service.LessonService;
import de.aittr.lmsbe.service.UsersService;
import de.aittr.lmsbe.service.cohort.CohortService;
import de.aittr.lmsbe.service.interfaces.IFileService;
import de.aittr.lmsbe.utils.FileValidateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTreeBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import static de.aittr.lmsbe.github.model.GHFileExtension.MD;
import static de.aittr.lmsbe.github.model.GHLessonType.LESSON;
import static de.aittr.lmsbe.github.uttils.GHHelper.PATH_SEPARATOR;
import static de.aittr.lmsbe.github.uttils.GHNameGenerator.generateHwBranchName;
import static de.aittr.lmsbe.github.uttils.GHNameGenerator.mapToUserFullName;

@RequiredArgsConstructor
@Service
@Slf4j
public class GHServiceImpl implements GHService {

    private static final String METADATA_FILE_NAME = "metadata.json";
    private static final String PLAN_FILENAME = "plan.md";
    private static final String FILE_NAME = "theory.md";
    private static final String HOMEWORK_FILE = "homework.md";
    private static final String CODE_DIRECTORY = "code";
    static final String HOMEWORK_SOLUTION_PATH = "hw_solution";
    private static final String ERROR_TEMPLATE = "Error encountered: {}";

    private final CohortService cohortService;
    private final IFileService fileService;
    private final UsersService userService;

    private final GHContentService ghContentService;
    private final GHBranchHandlerService ghBranchHandlerService;
    private final GHCommitHandlerService ghCommitHandlerService;
    private final GhValidationService ghValidationService;
    private final GHZipService ghZipService;
    private final GHFileEncodeService ghFileEncodeService;
    private final GHHelper ghHelper;
    private final LessonService lessonService;

    @Override
    public List<String> getLessonModuls(final String cohort) {

        findCohortByRepoName(cohort);

        try {
            final var repository = ghBranchHandlerService.getRepo(cohort);
            return ghContentService.retrieveDirectories(repository, Strings.EMPTY).stream()
                    .map(GHContent::getName)
                    .filter(name -> !name.contains("consultation"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("", e);
            throw new RestException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public List<String> getLessons(final String cohort,
                                   final String moduleName,
                                   final GHLessonType ghLessonType) {

        findCohortByRepoName(cohort);

        try {
            final var repository = ghBranchHandlerService.getRepo(cohort);
            final var pathToLessons = moduleName + (LESSON.equals(ghLessonType) ? "" : PATH_SEPARATOR + ghLessonType.getPath());
            return ghContentService.retrieveDirectories(repository, pathToLessons)
                    .stream()
                    .map(GHContent::getName)
                    .filter(name -> (name.startsWith(ghLessonType.getPathsSuffix()) && name.matches(".*\\d+$")))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            final var errorMsg = "An exception occurred while retrieving lessons for the specified cohort and module. " +
                    "Please ensure that the cohort, module name, and lesson type are correct and try again.";
            log.error(errorMsg + "Error detail message: '{}'", e.getMessage());
            throw new RestException(HttpStatus.BAD_REQUEST, errorMsg);
        }
    }

    @Override
    public LessonMeta getlessonMeta(final String cohortAlias,
                                    final String moduleName,
                                    final String lessonNr,
                                    final User currentUser,
                                    final GHLessonType ghLessonType) {
        log.debug("Fetching lesson meta. Cohort: {}, Module Name: {}, Lesson Number: {}, User: {}, Github Lesson Type: {}",
                cohortAlias, moduleName, lessonNr, currentUser, ghLessonType);

        final Cohort existingCohortRepo = findCohortByRepoName(cohortAlias);
        LessonDto lessonDto = null;
        if (currentUser != null) {
            final var loggedInUser = userService.getUserByEmailOrThrow(currentUser.getEmail());
            final var userPrimaryCohort = loggedInUser.getPrimaryCohort();
            final var userCohorts = loggedInUser.getCohorts();
            final var userRole = loggedInUser.getRole();
            final boolean allAllowed = User.Role.ADMIN.equals(userRole) || User.Role.TEACHER.equals(userRole);
            log.debug("Access check - Email: {}, Primary Cohort: {}, Cohorts: {}, Role: {}, All allowed: {}",
                    currentUser.getEmail(), userPrimaryCohort, userCohorts, userRole, allAllowed);
            if (!allAllowed && (!userCohorts.contains(existingCohortRepo) && !existingCohortRepo.equals(userPrimaryCohort))) {
                String message = String.format("User %s denied access to cohort %s", currentUser.getEmail(), existingCohortRepo);
                log.warn(message);
                throw new RestException(HttpStatus.FORBIDDEN, message);
            }
            if (allAllowed) {
                lessonDto = lessonService.getLectureLesson(cohortAlias, moduleName, lessonNr, ghLessonType.getLessonType()).orElse(null);
            }
        }

        final var pathToLessons = moduleName + (LESSON.equals(ghLessonType) ? "" : PATH_SEPARATOR + ghLessonType.getPath());
        try {
            log.debug("Fetching repo from Github for Cohort: {}", cohortAlias);
            final GHRepository repo = ghBranchHandlerService.getRepo(cohortAlias);
            final GHBranch mainBranch = ghBranchHandlerService.getDefaultBranch(repo);
            final String mainBranchSHA = mainBranch.getSHA1();

            log.debug("Fetching files by lesson path: {}, lesson number: {}  for User: {}",
                    pathToLessons, lessonNr, currentUser);

            final var planData = ghContentService.getFileData(repo, pathToLessons, lessonNr, PLAN_FILENAME, mainBranchSHA);
            final var theoryData = ghContentService.getFileData(repo, pathToLessons, lessonNr, FILE_NAME, mainBranchSHA);
            final var homeworkData = ghContentService.getFileData(repo, pathToLessons, lessonNr, HOMEWORK_FILE, mainBranchSHA);
            final var ghLessonCode = getCode(repo, pathToLessons, lessonNr, mainBranchSHA);

            final List<LessonCode> lessonCode = ghLessonCode.stream()
                    .map(MyGHContent::getLessonCode)
                    .collect(Collectors.toList());
            final List<GHContent> ghContentList = ghLessonCode.stream()
                    .map(MyGHContent::getGhContent)
                    .collect(Collectors.toList());

            final var baos = ghZipService.zipFilesToByteArray(ghContentList);
            final var archiveData = Base64.getEncoder().encodeToString(baos.toByteArray());

            log.debug("\n\tplandData found:\t{}\n theoryData found:\t{}\n homeworkData found:\t{}\n lessonCode found:\t{}",
                    planData != null, theoryData != null, homeworkData != null, lessonCode.size());

            final FileValidateResult fileValidationResult = fileService.fileValidateResult(moduleName,
                    lessonNr, currentUser,
                    ghLessonType, existingCohortRepo);
            boolean isFileExist = fileValidationResult.isFileAllowed() && fileValidationResult.isFileExists();

            return new LessonMeta(planData,
                    theoryData,
                    homeworkData,
                    lessonCode,
                    isFileExist,
                    fileValidationResult.isFileAllowed(),
                    archiveData,
                    lessonDto);
        } catch (IOException e) {
            log.error("Error fetching lesson meta. Exception Message: {}", e.getMessage(), e);
            throw new RestException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public LessonCode deleteRepoFile(final String cohort, String filePath, User currentUser) {
        try {
            final GHRepository repo = ghBranchHandlerService.getRepo(cohort);
            final String userFullName = currentUser.getFirstName().trim() + " " + currentUser.getLastName().trim();
            final String userEmail = currentUser.getEmail();
            String userBranchName = (userFullName + "-" + userEmail).replace(" ", "_");
            final GHBranch branch = ghBranchHandlerService.getBranch(repo, userBranchName);
            if (branch == null) {
                throw new RestException(HttpStatus.NOT_FOUND,
                        "Branch by user '" + userFullName + "', user email: '" + userEmail + "' not found");
            }
            GHContent ghContent = ghContentService.retrieveContent(repo, filePath, branch.getSHA1());
            String commitMsg = MessageFormat.format("deleted at: {0} by {1}",
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), currentUser.getEmail());
            if (ghContent == null) {
                throw new RestException(HttpStatus.NOT_FOUND, "File by path '" + filePath + "' not found");
            }
            ghContent.delete(commitMsg, userBranchName);
            return new LessonCode(ghContent.getPath(),
                    ghContent.getName(),
                    null,
                    null,
                    false,
                    null);
        } catch (IOException e) {
            log.error(ERROR_TEMPLATE, e.getMessage());
            throw new RestException(HttpStatus.BAD_REQUEST, "Failed to delete the file: " + filePath + ". Please check the file path and try again later.");
        }
    }

    public void addFilesToLesson(final String cohort,
                                 final String moduleName,
                                 final String lessonNr,
                                 final User currentUser,
                                 final List<GHUploadFile> files) {
        ghValidationService.validateMaxUploadSize(files.size());

        try {
            final GHRepository repository = ghBranchHandlerService.getRepo(cohort);
            final String branchName = generateHwBranchName(currentUser);
            final String fileRepoPath = moduleName + PATH_SEPARATOR + lessonNr + PATH_SEPARATOR + HOMEWORK_SOLUTION_PATH;

            final GHBranch currentUserBranch = ghBranchHandlerService.createBranchFromMainBranch(repository, branchName);
            final org.kohsuke.github.GHTreeBuilder homeworkTree = processUpload(repository, currentUserBranch, fileRepoPath, files);

            ghCommitHandlerService.createAndPushHwCommit(repository,
                    mapToUserFullName(currentUser),
                    currentUser.getEmail(),
                    homeworkTree.create(),
                    currentUserBranch);

        } catch (IOException e) {
            log.error(ERROR_TEMPLATE, e.getMessage());
        }
    }

    private GHTreeBuilder processUpload(final GHRepository repository,
                                        final GHBranch branch,
                                        final String fileRepoPath,
                                        final List<GHUploadFile> files) throws IOException {
        return ghBranchHandlerService.addFilesToTree(repository, branch, fileRepoPath, files);
    }

    public LessonMeta getHomeWorkFiles(final String cohort,
                                       final String moduleName,
                                       final String lessonNr,
                                       final User currentUser) {
        try {
            String userFullName = currentUser.getFirstName().trim() + " " + currentUser.getLastName().trim();
            String currentUserEmail = currentUser.getEmail();
            String userBranchName = (userFullName + "-" + currentUserEmail).replace(" ", "_");

            final GHRepository repo = ghBranchHandlerService.getRepo(cohort);
            final GHBranch branch = ghBranchHandlerService.getBranch(repo, userBranchName);

            final String pathToLessons = moduleName + PATH_SEPARATOR + lessonNr;

            final var ghLessonCode = getCodeFromCodeDir(repo, pathToLessons, HOMEWORK_SOLUTION_PATH, branch.getSHA1());
            final List<LessonCode> lessonCode = ghLessonCode.stream()
                    .map(MyGHContent::getLessonCode)
                    .collect(Collectors.toList());
            final List<GHContent> ghContentList = ghLessonCode.stream()
                    .map(MyGHContent::getGhContent)
                    .collect(Collectors.toList());

            ByteArrayOutputStream baos = ghZipService.zipFilesToByteArray(ghContentList);
            String archiveData = Base64.getEncoder().encodeToString(baos.toByteArray());

            return new LessonMeta(null,
                    null,
                    null,
                    lessonCode,
                    false,
                    false,
                    archiveData,
                    null);
        } catch (org.kohsuke.github.GHFileNotFoundException e) {
            log.warn(ERROR_TEMPLATE, e.getMessage());
            return null;
        } catch (IOException e) {
            log.warn(ERROR_TEMPLATE, e.getMessage());
            throw new RestException(HttpStatus.BAD_REQUEST, "Error encountered, please try again later");
        }
    }

    private Cohort findCohortByRepoName(final String cohort) {
        return cohortService.getCohortByRepoNameOrThrow(cohort);
    }

    private List<MyGHContent> getCode(final GHRepository repository,
                                      final String lessonModuleName,
                                      final String lessonNr,
                                      final String branchSHA1) {

        final var lessonPath = lessonModuleName + PATH_SEPARATOR + lessonNr;
        final var metaJson = ghContentService.retrieveContent(repository, lessonPath + PATH_SEPARATOR + METADATA_FILE_NAME, branchSHA1);
        if (metaJson != null && metaJson.isFile()) {
            return getCodeFromMetaJson(repository, lessonPath, metaJson, branchSHA1);
        } else {
            return getCodeFromCodeDir(repository, lessonPath, CODE_DIRECTORY, branchSHA1);
        }
    }

    private List<MyGHContent> getCodeFromCodeDir(final GHRepository repository,
                                                 final String lessonPath,
                                                 final String pathToCode,
                                                 final String branchSHA1) {
        final var codePath = lessonPath + PATH_SEPARATOR + pathToCode;
        return ghContentService.retrieveContentRecursive(repository, codePath, new ArrayList<>(), branchSHA1)
                .stream().map(ghContent -> new MyGHContent(ghContent, ghHelper.createLessonCode(ghContent)))
                .collect(Collectors.toList());
    }

    private List<MyGHContent> getCodeFromMetaJson(final GHRepository repository,
                                                  final String lessonPath,
                                                  final GHContent metaJson,
                                                  final String branchSHA1) {

        final var metaFileContent = ghFileEncodeService.readFileContentAsString(metaJson, MD);
        final var objectMapper = new ObjectMapper();
        GHJsonMeta meta;
        try {
            meta = objectMapper.readValue(metaFileContent, GHJsonMeta.class);
        } catch (JsonProcessingException e) {
            log.error("Error occurred while processing JSON, Error message: '{}'", e.getMessage());
            return new ArrayList<>();
        }

        final List<MyGHContent> lessonCodes = new ArrayList<>();
        for (String codeFileName : meta.getCode()) {
            final var pathToCode = lessonPath + PATH_SEPARATOR + codeFileName;
            final var codeContent = ghContentService.retrieveContent(repository, pathToCode, branchSHA1);
            LessonCode lessonCode = ghHelper.createLessonCode(codeContent, pathToCode, codeFileName);
            lessonCodes.add(new MyGHContent(codeContent, lessonCode));
        }

        return lessonCodes;
    }
}
