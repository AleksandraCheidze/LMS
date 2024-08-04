package de.aittr.lmsbe.github.service;

import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * GHCommitHandlerService is a service class that provides methods for creating and pushing commits to a GitHub repository.
 */
@Service
@Slf4j
public class GHCommitHandlerService {

    /**
     * Creates a new commit and pushes it to a specified repository and branch.
     *
     * @param repo The GitHub repository to push the commit to.
     * @param authorName The name of the commit author.
     * @param authorEmail The email address of the commit author.
     * @param tree The tree object for the commit.
     * @param branch The branch to update with the new commit.
     * @param message The commit message.
     * @throws IOException If there is an error while creating or pushing the commit.
     */
    public void createAndPushCommit(final GHRepository repo,
                                    final String authorName,
                                    final String authorEmail,
                                    final GHTree tree,
                                    final GHBranch branch,
                                    final String message) throws IOException {

        final GHCommit ghCommit = repo.createCommit()
                .author(authorName, authorEmail, new Date())
                .parent(branch.getSHA1())
                .tree(tree.getSha())
                .message(message)
                .create();

        final String selectedBranchName = branch.getName();
        final GHRef existingBranchRef = repo.getRef("refs/heads/" + selectedBranchName);
        existingBranchRef.updateTo(ghCommit.getSHA1());

        log.debug("New commit created and pushed:" +
                        "\ncommit SHA1: '{}'" +
                        "\ntree sha: '{}'" +
                        "\nbranch: '{}'" +
                        "\nauthor: '{}'" +
                        "\nemail: '{}'" +
                        "\ncommit message: '{}'" +
                        "\nURL: '{}'",
                ghCommit.getSHA1(),
                tree.getSha(),
                selectedBranchName,
                authorName,
                authorEmail,
                message,
                ghCommit.getHtmlUrl());
    }

    /**
     * Creates and pushes a homework commit to a GitHub repository.
     *
     * @param repo The GitHub repository to push the commit to
     * @param userName The username associated with the commit
     * @param userEmail The email associated with the commit
     * @param tree The tree representing the commit
     * @param branch The branch to push the commit to
     * @throws IOException If an I/O error occurs during the commit process
     */
    public void createAndPushHwCommit(final GHRepository repo,
                                      final String userName,
                                      final String userEmail,
                                      final GHTree tree,
                                      final GHBranch branch) throws IOException {
        final String selectedBranchName = branch.getName();
        final String commitMessage = generateHwCommitMessage(selectedBranchName);
        createAndPushCommit(repo, userName, userEmail, tree, branch, commitMessage);
    }

    /**
     * Generates a commit message for a homework submission.
     *
     * @param branchName the branch name associated with the homework
     * @return the generated commit message
     */
    private String generateHwCommitMessage(final String branchName) {
        final String formattedTime = LocalDateTime.now().format(ofPattern("yyyy-MM-dd HH:mm:ss"));
        final String commitMessage = String.format("(HW)-user: '%s', commit time: '%s'", branchName, formattedTime);
        log.debug("Commit message will be: {}", commitMessage);
        return commitMessage;
    }
}
