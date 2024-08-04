package de.aittr.lmsbe.controller;

import de.aittr.lmsbe.controller.api.VideoApi;
import de.aittr.lmsbe.security.details.AuthenticatedUser;
import de.aittr.lmsbe.service.interfaces.IFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class VideoController implements VideoApi {


    private final IFileService fileService;

    @Override
    public List<String> getVideos(@RequestParam String cohort,
                                  @RequestParam String module,
                                  @RequestParam(required = false) String type,
                                  @RequestParam @Valid @Min(1) String lessonsNr,
                                  AuthenticatedUser currentUser) {


        return fileService.getPresignedLinksByPrefixByAuthUser(cohort, module, type, lessonsNr, currentUser.getUser());
    }
}
