package de.aittr.lmsbe.controller;

import de.aittr.lmsbe.dto.cohort.CohortDto;
import de.aittr.lmsbe.service.cohort.CohortService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CohortApiImplTest {

    @InjectMocks
    private CohortApiImpl subject;

    @Mock
    private CohortService cohortService;

    @Test
    void getAllCohorts_ReturnsListOfCohorts() {
        List<CohortDto> cohorts = Arrays.asList(
                new CohortDto(1L, "Cohort 1", "cohort_1", "c1"),
                new CohortDto(2L, "Cohort 2", "cohort_2", "c2")
        );

        when(cohortService.getAll()).thenReturn(cohorts);

        List<CohortDto> result = subject.getAllCohorts();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(cohorts);
    }
}
