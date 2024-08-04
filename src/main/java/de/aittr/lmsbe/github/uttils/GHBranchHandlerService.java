package de.aittr.lmsbe.github.uttils;

import de.aittr.lmsbe.exception.RestException;
import de.aittr.lmsbe.github.model.GHUploadFile;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTreeBuilder;

import java.io.IOException;
import java.util.List;

/**
 * This interface provides methods to handle branches in a GitHub repository.
 */
public interface GHBranchHandlerService {

    /**
     * Retrieves the GHRepository object for the specified repository.
     *
     * @param repo the name of the repository
     * @return the GHRepository object associated with the specified repository
     * @throws IOException if an I/O error occurs while retrieving the repository
     */
    GHRepository getRepo(String repo) throws IOException;

    /**
     * Retrieves the default branch of a given repository.
     *
     * @param repo the GitHub repository to get the default branch from
     * @return the default branch of the repository
     * @throws IOException if an I/O error occurs while retrieving the default branch
     */
    GHBranch getDefaultBranch(GHRepository repo) throws IOException;

    /**
     * Retrieves the specified branch from the given repository.
     *
     * @param repo       The repository from which to retrieve the branch.
     * @param branchName The name of the branch to retrieve.
     * @return The requested branch.
     * @throws IOException If an error occurs while retrieving the branch.
     */
    GHBranch getBranch(GHRepository repo,
                       String branchName) throws IOException;

    /**
     * Adds the given list of files to a tree in the specified repository and branch.
     * The files are added to the specified path within the tree.
     *
     * @param repo              The GHRepository object representing the repository.
     * @param currentUserBranch The GHBranch object representing the current branch.
     * @param path              The path within the tree to which the files will be added.
     * @param files             The list of GhUploadFile objects representing the files to be added.
     * @return A GHTreeBuilder object representing the updated tree.
     * @throws IOException If an I/O error occurs while creating the tree.
     */
    GHTreeBuilder addFilesToTree(GHRepository repo,
                                 GHBranch currentUserBranch,
                                 String fileRepoPath,
                                 List<GHUploadFile> files) throws IOException;

    /**
     * Adds a single file to a tree in the specified repository.
     *
     * @param treeBuilder The GHTreeBuilder object representing the tree to add the file to.
     * @param path        The path within the tree to add the file to.
     * @param ghFile      The GhUploadFile object representing the file to be added.
     * @throws IOException If an I/O error occurs while adding the file to the tree.
     */
    void addSingleFileToTree(GHTreeBuilder treeBuilder,
                             String path,
                             GHUploadFile ghFile) throws IOException;

    /**
     * Creates a new branch from the main branch in a given repository.
     *
     * @param repo       the GHRepository object representing the repository
     * @param branchName the name of the new branch
     * @return the newly created branch
     * @throws IOException   if an I/O error occurs
     * @throws RestException if the default branch is undefined in the repository
     */
    GHBranch createBranchFromMainBranch(GHRepository repo,
                                        String branchName) throws IOException;
}
