package de.aittr.lmsbe.github.service;


import de.aittr.lmsbe.exception.RestException;
import de.aittr.lmsbe.github.model.GHUploadFile;
import de.aittr.lmsbe.github.uttils.GHBranchHandlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTreeBuilder;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static de.aittr.lmsbe.github.uttils.GHHelper.*;

/**
 * Implementation of the GithubBranchHandler interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GHBranchHandlerServiceImpl implements GHBranchHandlerService {

    @Value("${git.organization}")
    private String organizationName;

    private final GitHub githubConnector;
    private final GhValidationService ghUploadValidator;

    @Override
    public GHRepository getRepo(final String repo) throws IOException {
        return githubConnector.getOrganization(organizationName).getRepository(repo);
    }

    @Override
    public GHBranch getDefaultBranch(final GHRepository repo) throws IOException {
        return getBranch(repo, repo.getDefaultBranch());
    }

    @Override
    public GHBranch getBranch(final GHRepository repo,
                              final String branchName) throws IOException {
        return repo.getBranch(branchName);
    }

    @Override
    public GHTreeBuilder addFilesToTree(final GHRepository repo,
                                        final GHBranch currentUserBranch,
                                        final String fileRepoPath,
                                        final List<GHUploadFile> files) throws IOException {
        final GHTreeBuilder treeBuilder = repo.createTree().baseTree(currentUserBranch.getSHA1());
        for (GHUploadFile ghFile : files) {
            addSingleFileToTree(treeBuilder, fileRepoPath, ghFile);
        }
        return treeBuilder;
    }

    @Override
    public void addSingleFileToTree(final GHTreeBuilder treeBuilder,
                                    final String path,
                                    final GHUploadFile ghFile) throws IOException {
        final MultipartFile file = ghFile.getFile();
        ghUploadValidator.validateFileSize(file.getSize());
        final String fileDestination = ghFile.getFileDestination();
        if (!isDestinationIgnored(fileDestination) && !isFileIgnored(file.getOriginalFilename())) {
            final String filePath = path + PATH_SEPARATOR + fileDestination;
            byte[] bytes = file.getBytes();
            treeBuilder.add(filePath, bytes, true);
        }
    }

    @Override
    public GHBranch createBranchFromMainBranch(final GHRepository repo,
                                               final String branchName) throws IOException {
        final Map<String, GHBranch> branchList = repo.getBranches();
        final GHBranch mainBranch = branchList.get(repo.getDefaultBranch());

        if (mainBranch == null) {
            throw new RestException(HttpStatus.BAD_REQUEST, "Default branch is undefined. Repository: " + repo.getName());
        }

        if (branchList.containsKey(branchName)) {
            log.debug("Branch already exists: {}", branchName);
        } else {
            repo.createRef("refs/heads/" + branchName, mainBranch.getSHA1());
            log.debug("New branch created: {}", branchName);
        }
        return repo.getBranch(branchName);
    }
}
