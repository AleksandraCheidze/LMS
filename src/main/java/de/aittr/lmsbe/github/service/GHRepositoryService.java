package de.aittr.lmsbe.github.service;

import de.aittr.lmsbe.exception.ConflictException;
import de.aittr.lmsbe.exception.NotFoundException;
import de.aittr.lmsbe.exception.RestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHCreateRepositoryBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class GHRepositoryService {

    private final GitHub githubConnector;

    @Value("${git.template-owner}")
    private String templateOwner;

    @Value("${git.template-repo}")
    private String templateRepo;

    @Transactional
    public GHRepository createRepositoryFromTemplate(String cohortVersion, boolean isPrivate) {

        GHRepository templateRepoObject = getRepository(templateOwner, templateRepo);
        if (repositoryExists(templateOwner, cohortVersion)) {
            throw new ConflictException("Repository with name " + cohortVersion + " already exists on GitHub");
        }
        if (!templateRepoObject.isTemplate()) {
            throw new RestException(HttpStatus.NOT_FOUND, "The provided repository is not a template repository.");
        }
        try {
            GHCreateRepositoryBuilder builder = githubConnector.createRepository(cohortVersion)
                    .fromTemplateRepository(templateOwner, templateRepo)
                    .private_(isPrivate);

            GHRepository newRepo = builder.create();

            log.debug("Created a new repository '{}' based on template '{}/{}' with visibility '{}'",
                    cohortVersion, templateOwner, templateRepo, isPrivate ? "private" : "public");
            return newRepo;
        } catch (IOException e) {
            log.error("Failed to create repository '{}'", cohortVersion, e);
            throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create repository: " + e.getMessage());
        }
    }

    @Transactional
    public GHRepository updateCohortVersion(String currentRepoName, String newCohortVersion) {
        GHRepository repository = getRepository(templateOwner, currentRepoName);

        if (repositoryExists(templateOwner, newCohortVersion)) {
            throw new ConflictException("Repository with name " + newCohortVersion + " already exists on GitHub");
        }
        try {
            repository.renameTo(newCohortVersion);
            log.debug("Renamed repository to '{}'", newCohortVersion);
            return repository;
        } catch (IOException e) {
            log.error("Failed to rename repository '{}'", currentRepoName, e);
            throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to rename repository: " + e.getMessage());
        }
    }

    public GHRepository getRepository(String owner, String repoName) {
        try {
            return githubConnector.getRepository(owner + "/" + repoName);
        } catch (IOException e) {
            log.error("Repository not found: {}/{}", owner, repoName, e);
            throw new RestException(HttpStatus.NOT_FOUND, "Repository not found: " + owner + "/" + repoName);
        }

    }

    private boolean repositoryExists(String owner, String repoName) {
        try {
            githubConnector.getRepository(owner + "/" + repoName);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}


