package de.aittr.lmsbe.github.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.aittr.lmsbe.github.uttils.GHHelper.PATH_SEPARATOR;


@Service
@Slf4j
@RequiredArgsConstructor
public class GHContentService {

    private final GHFileEncodeService ghFileEncodeService;

    public List<GHContent> retrieveContentRecursive(GHRepository repo,
                                                    String path,
                                                    List<GHContent> fileList,
                                                    String branchSHA1) {
        List<GHContent> contents;
        try {
            contents = repo.getDirectoryContent(path, branchSHA1);
        } catch (IOException e) {
            return new ArrayList<>();
        }

        for (GHContent content : contents) {
            if (content.isDirectory()) {
                retrieveContentRecursive(repo, content.getPath(), fileList, branchSHA1);
            } else {
                fileList.add(content);
            }
        }
        return fileList;
    }

    public GHContent retrieveContent(GHRepository repo,
                                     String path,
                                     String branchSHA1) {
        try {
            return repo.getFileContent(path, branchSHA1);
        } catch (IOException e) {
            return null;
        }
    }

    public List<GHContent> retrieveDirectories(GHRepository repo,
                                               String path) {
        try {
            return repo.getDirectoryContent(path)
                    .stream()
                    .filter(GHContent::isDirectory)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public String getFileData(GHRepository repository,
                              String lessonModuleName,
                              String lessonNr,
                              String fileName,
                              String branchSHA1) {
        String lessonPath = lessonModuleName + PATH_SEPARATOR + lessonNr;
        GHContent fileContent = retrieveContent(repository, lessonPath + PATH_SEPARATOR + fileName, branchSHA1);
        return ghFileEncodeService.readFileContentAsString(fileContent, null);
    }
}
