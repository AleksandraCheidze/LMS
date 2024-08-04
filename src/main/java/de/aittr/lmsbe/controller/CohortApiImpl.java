package de.aittr.lmsbe.controller;

import de.aittr.lmsbe.controller.api.CohortApi;
import de.aittr.lmsbe.dto.cohort.*;
import de.aittr.lmsbe.security.details.AuthenticatedUser;
import de.aittr.lmsbe.service.cohort.CohortService;
import de.aittr.lmsbe.service.cohort.search.CohortFilter;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Andrej Reutow
 * created on 11.11.2023
 */
@RestController
@RequiredArgsConstructor
public class CohortApiImpl implements CohortApi {

    private final CohortService cohortService;
    private final CohortFilter cohortFilter;

    @Override
    public List<CohortDto> getAllCohorts() {
        return cohortService.getAll();
    }

    @Override
    public CohortStudentDto getStudentByCohort(long cohortId) {
        return cohortService.getStudentByCohortId(cohortId);
    }

    @Override
    public List<StudentCohortDto> getAllStudentsCohort() {
        return cohortService.getAllStudentsCohort();
    }

    @Override
    public List<TeacherCohortDto> getAllTeachersCohort(@AuthenticationPrincipal
                                                       @Parameter(hidden = true)
                                                       AuthenticatedUser currentUser,
                                                       @RequestParam("from")
                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                       LocalDate from,
                                                       @RequestParam("to")
                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                       LocalDate to) {
        ControllerUtil.DatesResult dateRangeResult = ControllerUtil.calculateDatesResult(from, to);
        return cohortService.getAllTeachersCohort(currentUser.getUser(), dateRangeResult.from, dateRangeResult.to);
    }


    @Override
    public List<StudentCohortDto> filterGroupsByTerm(String scope, String term, boolean searchPrimary) {
        return cohortFilter.filterByScope(scope, term, searchPrimary);
    }
    @Override
    public CohortRepoDto createCohort(CohortDto cohortDto) {
        return cohortService.createCohort(cohortDto);
    }

    @Override
    public CohortRepoDto updateCohort(Long id,  CohortDto cohortDto) {
        return cohortService.updateCohort(id, cohortDto);
    }
}
