package de.aittr.lmsbe.zoom.service;

import de.aittr.lmsbe.dto.UserDto;
import de.aittr.lmsbe.model.User;
import de.aittr.lmsbe.zoom.dto.ZoomMeetingDto;
import de.aittr.lmsbe.zoom.entity.ZoomMeeting;
import de.aittr.lmsbe.zoom.repository.ZoomMeetingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static de.aittr.lmsbe.model.User.Role.ADMIN;
import static de.aittr.lmsbe.model.User.Role.TEACHER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Andrej Reutow
 * created on 26.04.2024
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testing getMeetingsByUser")
class ZoomMeetingServiceTest {

    @Mock
    private ZoomMeetingRepository zoomMeetingRepository;

    @InjectMocks
    private ZoomMeetingService underTest;


    @Nested
    class GetMeetingsByUser {

        private User currentUser;
        private ZoomMeeting mockZoomMeeting;

        @BeforeEach
        void setUp() {
            currentUser = new User();
            mockZoomMeeting = mock(ZoomMeeting.class);
        }

        /**
         * Stub the ZoomMeetingRepository to return the specified list of meetings when the
         * findAllForUserAndDates method is called with specific parameters.
         *
         * @param meetings The list of ZoomMeeting objects that should be returned by the repository
         */
        private void stubZoomMeetingRepository(List<ZoomMeeting> meetings) {
            when(zoomMeetingRepository.findAllForUserAndDates(eq(currentUser),
                    any(LocalDateTime.class), any(LocalDateTime.class), eq(List.of(TEACHER, ADMIN))))
                    .thenReturn(meetings);
        }

        /**
         * Verifies that the ZoomMeetingRepository is called with the correct parameters when getting meetings by user and date range.
         *
         * @param dateFrom The start date of the date range.
         * @param dateTo   The end date of the date range.
         */
        private void verifyZoomMeetingRepository(LocalDate dateFrom, LocalDate dateTo) {
            LocalDateTime from = LocalDateTime.of(dateFrom, LocalTime.MIN);
            LocalDateTime to = LocalDateTime.of(dateTo, LocalTime.MAX);

            verify(zoomMeetingRepository).findAllForUserAndDates(currentUser, from, to, List.of(TEACHER, ADMIN));
        }

        @Test
        @DisplayName("when both from and to dates are null")
        void testBothDatesNull() {
            stubZoomMeetingRepository(Collections.emptyList());

            List<ZoomMeetingDto> result = underTest.getMeetingsByUser(currentUser, null, null);

            assertThat(result).isEmpty();
            verifyZoomMeetingRepository(LocalDate.now().withDayOfMonth(1), LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));
        }

        @Test
        @DisplayName("when repository returns an empty list")
        void testEmptyList() {
            stubZoomMeetingRepository(Collections.emptyList());
            LocalDate now = LocalDate.now();

            List<ZoomMeetingDto> result = underTest.getMeetingsByUser(currentUser, now, now);

            assertThat(result).isEmpty();
            verifyZoomMeetingRepository(now, now);
        }

        @Test
        @DisplayName("when repository returns a non-empty list")
        void testNonEmptyList() {
            stubZoomMeetingRepository(List.of(mockZoomMeeting));
            LocalDate now = LocalDate.now();

            List<ZoomMeetingDto> result = underTest.getMeetingsByUser(currentUser, now, now);

            assertThat(result).isNotEmpty();
            verifyZoomMeetingRepository(now, now);
        }

        @Test
        @DisplayName("when from date is null and to date is specified")
        void testFromDateNull() {
            LocalDate toDate = LocalDate.now().plusDays(1);
            stubZoomMeetingRepository(List.of(mockZoomMeeting));

            List<ZoomMeetingDto> result = underTest.getMeetingsByUser(currentUser, null, toDate);

            assertThat(result).isNotEmpty();
            verifyZoomMeetingRepository(LocalDate.now().withDayOfMonth(1), toDate);
        }

        @Test
        @DisplayName("when to date is null and from date is specified")
        void testToDateNull() {
            LocalDate fromDate = LocalDate.now().minusDays(1);
            stubZoomMeetingRepository(List.of(mockZoomMeeting));

            List<ZoomMeetingDto> result = underTest.getMeetingsByUser(currentUser, fromDate, null);

            assertThat(result).isNotEmpty();
            verifyZoomMeetingRepository(fromDate, fromDate.withDayOfMonth(fromDate.lengthOfMonth()));
        }

        @Test
        @DisplayName("when both dates are specified")
        void testBothDatesSpecified() {
            LocalDate fromDate = LocalDate.now().minusDays(1);
            LocalDate toDate = LocalDate.now().plusDays(1);
            stubZoomMeetingRepository(List.of(mockZoomMeeting));

            List<ZoomMeetingDto> result = underTest.getMeetingsByUser(currentUser, fromDate, toDate);

            assertThat(result).isNotEmpty();
            verifyZoomMeetingRepository(fromDate, toDate);
        }

        @Test
        @DisplayName("when repository returns a ZoomMeeting with specific properties")
        void testSpecificZoomMeetingReturned() {
            currentUser.setCohorts(new HashSet<>());
            currentUser.setRole(TEACHER);
            currentUser.setState(User.State.CONFIRMED);

            LocalDate fromDate = LocalDate.now().minusDays(1);
            LocalDate toDate = LocalDate.now().plusDays(1);
            when(mockZoomMeeting.getHostEmail()).thenReturn("host@domain.com");
            when(mockZoomMeeting.getUuid()).thenReturn("UUID");
            when(mockZoomMeeting.getUser()).thenReturn(currentUser);
            stubZoomMeetingRepository(List.of(mockZoomMeeting));

            List<ZoomMeetingDto> result = underTest.getMeetingsByUser(currentUser, fromDate, toDate);

            assertThat(result).isNotEmpty();
            ZoomMeetingDto dto = result.get(0);
            assertThat(dto.getHostEmail()).isEqualTo("host@domain.com");
            assertThat(dto.getUuid()).isEqualTo("UUID");
            assertThat(dto.getUser()).isEqualTo(UserDto.from(currentUser));
            verifyZoomMeetingRepository(fromDate, toDate);
        }
    }
}
