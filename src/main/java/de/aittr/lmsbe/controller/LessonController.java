package de.aittr.lmsbe.controller;

import de.aittr.lmsbe.controller.api.LessonApi;
import de.aittr.lmsbe.dto.LessonDto;
import de.aittr.lmsbe.model.LessonModul;
import de.aittr.lmsbe.model.LessonType;
import de.aittr.lmsbe.security.details.AuthenticatedUser;
import de.aittr.lmsbe.service.LessonService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static de.aittr.lmsbe.controller.ControllerUtil.*;

@RestController
@RequiredArgsConstructor
public class LessonController implements LessonApi {

    private final LessonService lessonService;

    @Override
    public LessonDto getLesson(@PathVariable
                               String groupName,
                               @PathVariable
                               String moduleName,
                               @PathVariable
                               String typeName,
                               @PathVariable
                               Integer lessonNr) {
        return lessonService.getLessonOrThrow(groupName, moduleName, typeName, lessonNr);
    }

    @Override
    public List<LessonDto> getLessonsByUserBetweenDate(@AuthenticationPrincipal
                                                       @Parameter(hidden = true)
                                                       AuthenticatedUser currentUser,
                                                       @PathVariable
                                                       long cohortId,
                                                       @RequestParam("from")
                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                       LocalDate from,
                                                       @RequestParam("to")
                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                       LocalDate to,
                                                       @RequestParam(value = "modules")
                                                       List<String> modules,
                                                       @RequestParam(value = "types")
                                                       List<String> types) {
        final List<LessonModul> lessonModules = getLessonModules(modules);
        final List<LessonType> lessonTypes = getLessonTypesByNames(types);
        final DatesResult result = calculateDatesResult(from, to);

        return lessonService.getAllLessons(currentUser.getUser(),
                cohortId,
                lessonModules,
                lessonTypes,
                result.from,
                LocalDateTime.of(to, LocalTime.MAX));
    }

    @Override
    public List<LessonDto> getLessonsByAuthUserBetweenDate(@PathVariable
                                                           long teacherId,
                                                           @PathVariable
                                                           long cohortId,
                                                           @RequestParam("from")
                                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                           LocalDate from,
                                                           @RequestParam("to")
                                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                           LocalDate to,
                                                           @RequestParam(value = "modules")
                                                           List<String> modules,
                                                           @RequestParam(value = "types")
                                                           List<String> types) {
        final List<LessonModul> lessonModules = getLessonModules(modules);
        final List<LessonType> lessonTypes = getLessonTypesByNames(types);
        final DatesResult result = calculateDatesResult(from, to);

        return lessonService.getAllLessons(teacherId,
                cohortId,
                lessonModules,
                lessonTypes,
                result.from,
                result.to);
    }
}
