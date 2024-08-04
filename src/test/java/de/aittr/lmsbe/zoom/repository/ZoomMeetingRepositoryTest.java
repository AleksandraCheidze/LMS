package de.aittr.lmsbe.zoom.repository;

import de.aittr.lmsbe.model.User;
import de.aittr.lmsbe.zoom.entity.MeetingType;
import de.aittr.lmsbe.zoom.entity.ZoomMeeting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests for ZoomMeetingRepository")
@DataJpaTest
class ZoomMeetingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ZoomMeetingRepository repository;

    private User teacher;
    private ZoomMeeting meeting1;
    private ZoomMeeting meeting2;

    private LocalDateTime from;
    private LocalDateTime to;

    @BeforeEach
    void setUp() {
        entityManager.clear();

        from = LocalDateTime.of(
                LocalDate.of(2024, 1, 1),
                LocalTime.MIN);
        to = LocalDateTime.of(
                LocalDate.of(2024, 1, 31),
                LocalTime.of(23, 59, 59));

        this.teacher = User.builder()
                .email("teacher@lms.com")
                .role(User.Role.TEACHER)
                .state(User.State.CONFIRMED)
                .firstName("John")
                .lastName("Doe")
                .build();
        this.meeting1 = ZoomMeeting.builder()
                .uuid("UUID1")
                .meetingId("MeetingID1")
                .user(teacher)
                .hostEmail("host@lms.com")
                .meetingType(MeetingType.INTERN)
                .build();
        this.meeting2 = ZoomMeeting.builder()
                .uuid("UUID2")
                .meetingId("MeetingID2")
                .user(teacher)
                .hostEmail("host@lms.com")
                .meetingType(MeetingType.INTERN)
                .build();
    }

    /**
     * --------#--------#-------- Meetings dates<br>
     * --------#--------#-------- Search between<br>
     */
    @Test
    @DisplayName("Find all meetings for user between two exact dates should return all the meetings on these dates")
    void testFindAllForUserAndDates_returnsAllMeetingsOnTheseDates() {

        // Given
        meeting1.setMeetingTime(from);
        meeting2.setMeetingTime(to);

        entityManager.persistAndFlush(teacher);
        entityManager.persistAndFlush(meeting1);
        entityManager.persistAndFlush(meeting2);

        // When
        List<ZoomMeeting> meetings = repository.findAllForUserAndDates(
                teacher,
                from,
                to,
                List.of(User.Role.TEACHER)
        );

        // Then
        assertThat(meetings)
                .hasSize(2)
                .containsExactlyInAnyOrder(meeting1, meeting2);
    }


    /**
     * -----------#---#---------- Meetings dates<br>
     * --------#--------#-------- Search between<br>
     */
    @Test
    @DisplayName("Find all meetings for user between two dates should return all the meetings during this period")
    void testFindAllForUserAndDates_returnsAllMeetingsDuringThisPeriod() {

        // Given
        meeting1.setMeetingTime(from.plusDays(1));
        meeting2.setMeetingTime(to.minusDays(1));

        entityManager.persistAndFlush(teacher);
        entityManager.persistAndFlush(meeting1);
        entityManager.persistAndFlush(meeting2);

        // When
        List<ZoomMeeting> meetings = repository.findAllForUserAndDates(
                teacher,
                from,
                to,
                List.of(User.Role.TEACHER)
        );

        // Then
        assertThat(meetings)
                .hasSize(2)
                .containsExactlyInAnyOrder(meeting1, meeting2);
    }

    /**
     * ------------#----------#-- Meetings dates<br>
     * --------#--------#-------- Search between<br>
     */
    @Test
    @DisplayName("Find all meetings for user between two dates where only one meeting is during this period should return this one meeting")
    void testFindAllForUserAndDates_returnsOneMeetingDuringThisPeriod() {

        // Given
        meeting1.setMeetingTime(from.plusDays(1));
        meeting2.setMeetingTime(to.plusDays(1));

        entityManager.persistAndFlush(teacher);
        entityManager.persistAndFlush(meeting1);
        entityManager.persistAndFlush(meeting2);

        // When
        List<ZoomMeeting> meetings = repository.findAllForUserAndDates(
                teacher,
                from,
                to,
                List.of(User.Role.TEACHER)
        );

        // Then
        assertThat(meetings)
                .hasSize(1)
                .contains(meeting1);
    }

    /**
     * ---#-------------#-------- Meetings dates<br>
     * --------#--------#-------- Search between<br>
     */
    @Test
    @DisplayName("Find all meetings for user between two dates where only the last meeting is during this period should return just the last meeting")
    void testFindAllForUserAndDates_returnsJustLastMeetingDuringThisPeriod() {

        // Given
        meeting1.setMeetingTime(from.minusDays(1));
        meeting2.setMeetingTime(to);

        entityManager.persistAndFlush(teacher);
        entityManager.persistAndFlush(meeting1);
        entityManager.persistAndFlush(meeting2);

        // When
        List<ZoomMeeting> meetings = repository.findAllForUserAndDates(
                teacher,
                from,
                to,
                List.of(User.Role.TEACHER)
        );

        // Then
        assertThat(meetings)
                .hasSize(1)
                .contains(meeting2);
    }

    /**
     * ---#------------------#--- Meetings dates<br>
     * --------#--------#-------- Search between<br>
     */
    @Test
    @DisplayName("Find all meetings for user between two dates where no meetings are during this period should return empty")
    void testFindAllForUserAndDates_whenNoMeetingsDuringThisPeriod_returnsEmpty() {

        // Given
        meeting1.setMeetingTime(from.minusDays(1));
        meeting2.setMeetingTime(to.plusDays(1));

        entityManager.persistAndFlush(teacher);
        entityManager.persistAndFlush(meeting1);
        entityManager.persistAndFlush(meeting2);

        // When
        List<ZoomMeeting> meetings = repository.findAllForUserAndDates(
                teacher,
                from,
                to,
                List.of(User.Role.TEACHER)
        );

        // Then
        assertThat(meetings).isEmpty();
    }
}
