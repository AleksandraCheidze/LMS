package de.aittr.lmsbe.service;

import de.aittr.lmsbe.dto.cohort.CohortDto;
import de.aittr.lmsbe.repository.CohortsRepository;
import de.aittr.lmsbe.service.cohort.CohortService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Andrej Reutow
 * created on 11.11.2023
 */
@ExtendWith(MockitoExtension.class)
class CohortServiceTest {

    @Mock
    private CohortsRepository cohortsRepository;

    @InjectMocks
    private CohortService subject;

    @Test
    void test_getAllCohorts() {
        Mockito.when(cohortsRepository.findAll()).thenReturn(Collections.emptyList());

        List<CohortDto> result = subject.getAll();

        assertEquals(0, result.size());
        Mockito.verify(cohortsRepository).findAll();
    }
}
