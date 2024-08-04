package de.aittr.lmsbe.service.cohort;

import de.aittr.lmsbe.dto.cohort.CohortStats;
import de.aittr.lmsbe.model.Cohort;
import de.aittr.lmsbe.model.Lesson;
import de.aittr.lmsbe.model.LessonType;
import de.aittr.lmsbe.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CohortUtilsTest {

    @Mock
    private Cohort cohort;

    @Mock
    private User teacher;

    @Nested
    @DisplayName("Testing getTotalPrimaryStudentsInCohort")
    class TotalPrimaryStudentsInCohortTest {

        @Test
        @DisplayName("when there are students with the cohort as the primary cohort")
        void shouldReturnTheCorrectNumberOfPrimaryCohortStudents() {
            User student1 = new User();
            student1.setRole(User.Role.STUDENT);
            student1.setPrimaryCohort(cohort);

            User student2 = new User();
            student2.setRole(User.Role.STUDENT);
            student2.setPrimaryCohort(cohort);

            Set<User> users = new HashSet<>();
            users.add(student1);
            users.add(student2);

            int totalPrimaryStudentsInCohort = CohortUtils.getTotalPrimaryStudentsInCohort(cohort, users);

            assertEquals(2, totalPrimaryStudentsInCohort);
        }

        @Test
        @DisplayName("when there are students that do not have the cohort as the primary cohort")
        void shouldReturnZeroForStudentsNotInPrimaryCohort() {
            Cohort differentCohort = new Cohort();

            User student1 = new User();
            student1.setRole(User.Role.STUDENT);
            student1.setPrimaryCohort(differentCohort);

            Set<User> users = new HashSet<>();
            users.add(student1);

            int totalPrimaryStudentsInCohort = CohortUtils.getTotalPrimaryStudentsInCohort(cohort, users);

            assertEquals(0, totalPrimaryStudentsInCohort);
        }

        @Test
        @DisplayName("when there are non-students that have the cohort as the primary cohort")
        void shouldReturnZeroForNonStudentsInCohort() {
            User teacher = new User();
            teacher.setRole(User.Role.TEACHER);
            teacher.setPrimaryCohort(cohort);

            Set<User> users = new HashSet<>();
            users.add(teacher);

            int totalPrimaryStudentsInCohort = CohortUtils.getTotalPrimaryStudentsInCohort(cohort, users);

            assertEquals(0, totalPrimaryStudentsInCohort);
        }
    }

    @Nested
    @DisplayName("Testing getTeacherLessons")
    class TeacherLessonsTest {

        @Test
        @DisplayName("when teacher has lessons 30 minutes later than the given criteria")
        void shouldReturnLessonsForTeacherWithOffsetLessonTime() {
            Lesson lessonMock = mock(Lesson.class);
            when(lessonMock.getTeacher()).thenReturn(teacher);
            LocalDateTime lessonTime = LocalDateTime.now();
            when(lessonMock.getLessonTime()).thenReturn(lessonTime.plusMinutes(30));
            when(lessonMock.getTeacher()).thenReturn(teacher);

            List<Lesson> lessons = new ArrayList<>();
            lessons.add(lessonMock);

            when(cohort.getLessons()).thenReturn(lessons);

            List<Lesson> lessonsTeacher = CohortUtils.getTeacherLessons(teacher, lessonTime, lessonTime.plusHours(1), cohort);
            assertEquals(1, lessonsTeacher.size());
        }


        @Test
        @DisplayName("when the lessonTime is null")
        void shouldHandleNullLessonTime() {
            Lesson lessonMock = mock(Lesson.class);
            LocalDateTime lessonTime = LocalDateTime.now();
            when(lessonMock.getLessonTime()).thenReturn(null);

            List<Lesson> lessons = new ArrayList<>();
            lessons.add(lessonMock);

            when(cohort.getLessons()).thenReturn(lessons);

            List<Lesson> lessonsTeacher = CohortUtils.getTeacherLessons(teacher, lessonTime, lessonTime.plusHours(1), cohort);
            assertEquals(0, lessonsTeacher.size());
        }

        @Test
        @DisplayName("when teacher has lessons in the given criteria")
        void shouldReturnLessonsForTeacher() {
            Lesson lessonMock = mock(Lesson.class);
            when(lessonMock.getTeacher()).thenReturn(teacher);
            LocalDateTime lessonTime = LocalDateTime.now();
            when(lessonMock.getLessonTime()).thenReturn(lessonTime);
            when(lessonMock.getTeacher()).thenReturn(teacher);

            List<Lesson> lessons = new ArrayList<>();
            lessons.add(lessonMock);

            when(cohort.getLessons()).thenReturn(lessons);

            List<Lesson> lessonsTeacher = CohortUtils.getTeacherLessons(teacher, lessonTime, lessonTime.plusHours(1), cohort);
            assertEquals(1, lessonsTeacher.size());
        }


        @Test
        @DisplayName("when teacher has lessons 1 hour later than the given criteria")
        void shouldReturnLessonsForTeacherWithExtendedLessonTime() {
            Lesson lessonMock = mock(Lesson.class);
            when(lessonMock.getTeacher()).thenReturn(teacher);
            LocalDateTime lessonTime = LocalDateTime.now();
            when(lessonMock.getLessonTime()).thenReturn(lessonTime.plusHours(1));
            when(lessonMock.getTeacher()).thenReturn(teacher);

            List<Lesson> lessons = new ArrayList<>();
            lessons.add(lessonMock);

            when(cohort.getLessons()).thenReturn(lessons);

            List<Lesson> lessonsTeacher = CohortUtils.getTeacherLessons(teacher, lessonTime, lessonTime.plusHours(1), cohort);
            assertEquals(1, lessonsTeacher.size());
        }

        @Test
        @DisplayName("when teacher has lessons before the given criteria")
        void shouldReturnNoLessonsForTeacherBeforePeriod() {
            Lesson lessonMock = mock(Lesson.class);
            when(lessonMock.getTeacher()).thenReturn(teacher);
            LocalDateTime lessonTime = LocalDateTime.now();
            when(lessonMock.getLessonTime()).thenReturn(lessonTime.minusHours(1));

            List<Lesson> lessons = new ArrayList<>();
            lessons.add(lessonMock);

            when(cohort.getLessons()).thenReturn(lessons);

            List<Lesson> lessonsTeacher = CohortUtils.getTeacherLessons(teacher, lessonTime, lessonTime.plusHours(1), cohort);
            assertEquals(0, lessonsTeacher.size());
        }

        @Test
        @DisplayName("when teacher has lessons after the given criteria")
        void shouldReturnNoLessonsForTeacherAfterPeriod() {
            Lesson lessonMock = mock(Lesson.class);
            when(lessonMock.getTeacher()).thenReturn(teacher);
            LocalDateTime lessonTime = LocalDateTime.now();
            when(lessonMock.getLessonTime()).thenReturn(lessonTime.plusHours(2));

            List<Lesson> lessons = new ArrayList<>();
            lessons.add(lessonMock);

            when(cohort.getLessons()).thenReturn(lessons);

            List<Lesson> lessonsTeacher = CohortUtils.getTeacherLessons(teacher, lessonTime, lessonTime.plusHours(1), cohort);
            assertEquals(0, lessonsTeacher.size());
        }

        @Test
        @DisplayName("when Cohort is null")
        void shouldHandleNullCohort() {
            assertDoesNotThrow(() ->
                    CohortUtils.getTeacherLessons(teacher, LocalDateTime.now(), LocalDateTime.now().plusHours(1), null)
            );

            List<Lesson> result = CohortUtils.getTeacherLessons(teacher, LocalDateTime.now(), LocalDateTime.now().plusHours(1), null);

            assertEquals(0, result.size());
        }

        @Test
        @DisplayName("when Cohort lessons is null")
        void shouldHandleNullCohortLessons() {
            when(cohort.getLessons()).thenReturn(null);
            assertDoesNotThrow(() ->
                    CohortUtils.getTeacherLessons(teacher, LocalDateTime.now(), LocalDateTime.now().plusHours(1), cohort)
            );

            List<Lesson> result = CohortUtils.getTeacherLessons(teacher, LocalDateTime.now(), LocalDateTime.now().plusHours(1), cohort);

            assertEquals(0, result.size());
        }

        @Test
        @DisplayName("when Cohort lessons is empty")
        void shouldHandleCohortLessonsIsEmpty() {
            when(cohort.getLessons()).thenReturn(null);
            assertDoesNotThrow(() ->
                    CohortUtils.getTeacherLessons(teacher, LocalDateTime.now(), LocalDateTime.now().plusHours(1), cohort)
            );

            List<Lesson> result = CohortUtils.getTeacherLessons(teacher, LocalDateTime.now(), LocalDateTime.now().plusHours(1), cohort);

            assertEquals(0, result.size());
        }

        @Test
        @DisplayName("when teacher has no lessons in the given criteria")
        void shouldReturnNoLessonsForTeacher() {
            LocalDateTime lessonTime = LocalDateTime.now();

            Lesson lessonMock = mock(Lesson.class);
            when(lessonMock.getTeacher()).thenReturn(teacher);
            when(lessonMock.getLessonTime()).thenReturn(lessonTime.plusHours(2));

            List<Lesson> lessons = new ArrayList<>();
            lessons.add(lessonMock);

            when(cohort.getLessons()).thenReturn(lessons);

            List<Lesson> lessonsTeacher = CohortUtils.getTeacherLessons(teacher, lessonTime, lessonTime.plusHours(1), cohort);
            assertEquals(0, lessonsTeacher.size());
        }

        @Test
        @DisplayName("when there are lessons but not for the current teacher")
        void shouldReturnNoLessonsForDifferentTeacher() {
            User differentTeacher = mock(User.class);

            LocalDateTime lessonTime = LocalDateTime.now();
            Lesson lessonMock = mock(Lesson.class);
            when(lessonMock.getTeacher()).thenReturn(differentTeacher);
            when(lessonMock.getLessonTime()).thenReturn(lessonTime);
            when(differentTeacher.getId()).thenReturn(2L);

            List<Lesson> lessons = new ArrayList<>();
            lessons.add(lessonMock);

            when(cohort.getLessons()).thenReturn(lessons);

            List<Lesson> lessonsTeacher = CohortUtils.getTeacherLessons(teacher, lessonTime, lessonTime.plusHours(1), cohort);
            assertEquals(0, lessonsTeacher.size());
        }

        @Test
        @DisplayName("when teacher has multiple lessons but not all fit the given criteria")
        void shouldReturnCorrectLessonsForTeacher() {
            Lesson lessonMock1 = mock(Lesson.class);
            LocalDateTime lessonTime = LocalDateTime.now();

            when(lessonMock1.getTeacher()).thenReturn(teacher);
            when(lessonMock1.getLessonTime()).thenReturn(lessonTime.plusMinutes(30));

            Lesson lessonMock2 = mock(Lesson.class);
            when(lessonMock2.getTeacher()).thenReturn(teacher);
            when(lessonMock2.getLessonTime()).thenReturn(lessonTime.plusHours(2));

            List<Lesson> lessons = new ArrayList<>();
            lessons.add(lessonMock1);
            lessons.add(lessonMock2);

            when(cohort.getLessons()).thenReturn(lessons);

            List<Lesson> lessonsTeacher = CohortUtils.getTeacherLessons(teacher, lessonTime, lessonTime.plusHours(1), cohort);
            assertEquals(1, lessonsTeacher.size());
        }

        @Test
        @DisplayName("when there are multiple lessons with different teachers")
        void shouldReturnOnlyCurrentTeacherLessons() {
            Lesson lessonMock1 = mock(Lesson.class);
            LocalDateTime lessonTime = LocalDateTime.now();
            when(lessonMock1.getTeacher()).thenReturn(teacher);
            when(lessonMock1.getLessonTime()).thenReturn(lessonTime.plusMinutes(30));

            Lesson lessonMock2 = mock(Lesson.class);
            User differentTeacher = mock(User.class);
            when(differentTeacher.getId()).thenReturn(2L);
            when(lessonMock2.getLessonTime()).thenReturn(lessonTime);

            when(lessonMock2.getTeacher()).thenReturn(differentTeacher);

            List<Lesson> lessons = new ArrayList<>();
            lessons.add(lessonMock1);
            lessons.add(lessonMock2);

            when(cohort.getLessons()).thenReturn(lessons);

            List<Lesson> lessonsTeacher = CohortUtils.getTeacherLessons(teacher, lessonTime, lessonTime.plusHours(1), cohort);

            assertEquals(1, lessonsTeacher.size());
            assertTrue(lessonsTeacher.contains(lessonMock1));
            assertFalse(lessonsTeacher.contains(lessonMock2));
        }

        @Test
        @DisplayName("when there are multiple lessons with different teachers and not all fit the given criteria")
        void shouldReturnOnlyCorrectLessonsForCurrentTeacher() {
            Lesson lessonMock1 = mock(Lesson.class);
            LocalDateTime lessonTime = LocalDateTime.now();
            when(lessonMock1.getTeacher()).thenReturn(teacher);
            when(lessonMock1.getLessonTime()).thenReturn(lessonTime.plusMinutes(30));

            Lesson lessonMock2 = mock(Lesson.class);
            when(lessonMock2.getTeacher()).thenReturn(teacher);
            when(lessonMock2.getLessonTime()).thenReturn(lessonTime.plusHours(2));

            Lesson lessonMock3 = mock(Lesson.class);
            User differentTeacher = mock(User.class);

            when(lessonMock3.getTeacher()).thenReturn(differentTeacher);
            when(lessonMock3.getLessonTime()).thenReturn(lessonTime);
            when(differentTeacher.getId()).thenReturn(2L);

            List<Lesson> lessons = new ArrayList<>();
            lessons.add(lessonMock1);
            lessons.add(lessonMock2);
            lessons.add(lessonMock3);

            when(cohort.getLessons()).thenReturn(lessons);

            List<Lesson> lessonsTeacher = CohortUtils.getTeacherLessons(teacher, lessonTime, lessonTime.plusHours(1), cohort);

            assertEquals(1, lessonsTeacher.size());
            assertTrue(lessonsTeacher.contains(lessonMock1));
            assertFalse(lessonsTeacher.contains(lessonMock2));
            assertFalse(lessonsTeacher.contains(lessonMock3));
        }
    }

    @Nested
    @DisplayName("Testing countLessonsByType")
    class CountLessonsByTypeTest {

        @Test
        @DisplayName("when there are lessons of required type")
        void shouldReturnCountOfTypeOfLessons() {
            Lesson lessonMock = mock(Lesson.class);
            when(lessonMock.getLessonType()).thenReturn(LessonType.LECTURE);
            List<Lesson> lessons = new ArrayList<>();
            lessons.add(lessonMock);
            long countOfType = CohortUtils.countLessonsByType(lessons, LessonType.LECTURE);
            assertEquals(1, countOfType);
        }

        @Test
        @DisplayName("when there are lessons of the required type")
        void shouldReturnCorrectNumberOfRequiredTypeLessons() {
            Lesson lessonMock1 = mock(Lesson.class);
            when(lessonMock1.getLessonType()).thenReturn(LessonType.LECTURE);

            Lesson lessonMock2 = mock(Lesson.class);
            when(lessonMock2.getLessonType()).thenReturn(LessonType.LECTURE);

            List<Lesson> lessons = new ArrayList<>();
            lessons.add(lessonMock1);
            lessons.add(lessonMock2);

            long numOfType1Lessons = CohortUtils.countLessonsByType(lessons, LessonType.LECTURE);

            assertEquals(2, numOfType1Lessons);
        }

        @Test
        @DisplayName("when there are lessons not of the required type")
        void shouldReturnZeroForOtherTypeLessons() {
            Lesson lessonMock1 = mock(Lesson.class);
            when(lessonMock1.getLessonType()).thenReturn(LessonType.CONSULTATION);

            Lesson lessonMock2 = mock(Lesson.class);
            when(lessonMock2.getLessonType()).thenReturn(LessonType.CONSULTATION);

            List<Lesson> lessons = new ArrayList<>();
            lessons.add(lessonMock1);
            lessons.add(lessonMock2);

            long numOfType1Lessons = CohortUtils.countLessonsByType(lessons, LessonType.LECTURE);

            assertEquals(0, numOfType1Lessons);
        }

        @Test
        @DisplayName("when there are no lessons")
        void shouldReturnZeroForNoLessons() {
            List<Lesson> lessons = new ArrayList<>();

            long numOfType1Lessons = CohortUtils.countLessonsByType(lessons, LessonType.LECTURE);

            assertEquals(0, numOfType1Lessons);
        }

        @Test
        @DisplayName("when there are lessons of different types")
        void shouldReturnCorrectNumberOfRequiredTypeLessonsInMixedTypes() {
            Lesson lessonMock1 = mock(Lesson.class);
            when(lessonMock1.getLessonType()).thenReturn(LessonType.LECTURE);

            Lesson lessonMock2 = mock(Lesson.class);
            when(lessonMock2.getLessonType()).thenReturn(LessonType.LECTURE);

            Lesson lessonMock3 = mock(Lesson.class);
            when(lessonMock3.getLessonType()).thenReturn(LessonType.CONSULTATION);

            Lesson lessonMock4 = mock(Lesson.class);
            when(lessonMock4.getLessonType()).thenReturn(LessonType.ELECTIVE);

            List<Lesson> lessons = new ArrayList<>();
            lessons.add(lessonMock1);
            lessons.add(lessonMock2);
            lessons.add(lessonMock3);
            lessons.add(lessonMock4);

            long numOfType1Lessons = CohortUtils.countLessonsByType(lessons, LessonType.LECTURE);

            assertEquals(2, numOfType1Lessons);
        }

    }

    @Nested
    @DisplayName("Testing calculateCohortStats")
    class CalculateCohortStatsTest {

        @Test
        @DisplayName("when there are cohorts, calculate correct stats")
        void testCalculateCohortStats() {
            User primaryStudent1 = new User();
            User otherStudent1 = new User();
            User primaryStudent3 = new User();

            primaryStudent1.setRole(User.Role.STUDENT);
            otherStudent1.setRole(User.Role.STUDENT);
            primaryStudent3.setRole(User.Role.STUDENT);

            Cohort cohort1 = mock(Cohort.class);
            Cohort cohort2 = mock(Cohort.class);

            primaryStudent1.setPrimaryCohort(cohort1);
            primaryStudent3.setPrimaryCohort(cohort2);

            when(cohort1.getId()).thenReturn(1L);
            when(cohort1.getAlias()).thenReturn("cohort1");
            when(cohort1.getUsers()).thenReturn(new HashSet<>(Arrays.asList(primaryStudent1, otherStudent1)));
            when(cohort1.getLessons()).thenReturn(Arrays.asList(new Lesson(), new Lesson()));

            when(cohort2.getId()).thenReturn(2L);
            when(cohort2.getAlias()).thenReturn("cohort2");
            when(cohort2.getUsers()).thenReturn(new HashSet<>(Collections.singletonList(primaryStudent3)));
            when(cohort2.getLessons()).thenReturn(List.of(new Lesson(), new Lesson()));

            List<Cohort> cohorts = Arrays.asList(cohort1, cohort2);

            List<CohortStats> result = new ArrayList<>();
            CohortUtils.calculateCohortStats(cohorts, result);

            assertEquals(2, result.size());

            CohortStats cohortStats1 = result.get(0);
            assertEquals(1L, cohortStats1.getCohortId());
            assertEquals("cohort1", cohortStats1.getCohortAlias());
            assertEquals(1, cohortStats1.getPrimaryStudents());
            assertEquals(1, cohortStats1.getOtherStudents());
            assertEquals(2, cohortStats1.getTotalLessons());

            CohortStats cohortStats2 = result.get(1);
            assertEquals(2L, cohortStats2.getCohortId());
            assertEquals("cohort2", cohortStats2.getCohortAlias());
            assertEquals(1, cohortStats2.getPrimaryStudents());
            assertEquals(0, cohortStats2.getOtherStudents());
            assertEquals(2, cohortStats2.getTotalLessons());
        }

        @Test
        @DisplayName("when there are no cohorts")
        void shouldKeepResultEmptyForNoCohorts() {
            List<Cohort> cohorts = Collections.emptyList();
            List<CohortStats> result = new ArrayList<>();
            CohortUtils.calculateCohortStats(cohorts, result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("when there are cohorts with students and non-students")
        void shouldCalculateCorrectStatsForCohortsWithMixedUsers() {
            User student = new User();
            User primaryStudent = new User();
            User nonStudent = new User();

            Cohort cohort = mock(Cohort.class);
            primaryStudent.setPrimaryCohort(cohort);
            nonStudent.setPrimaryCohort(cohort);

            student.setRole(User.Role.STUDENT);
            primaryStudent.setRole(User.Role.STUDENT);
            nonStudent.setRole(User.Role.TEACHER);

            when(cohort.getId()).thenReturn(1L);
            when(cohort.getAlias()).thenReturn("cohort1");
            when(cohort.getUsers()).thenReturn(new HashSet<>(List.of(student, nonStudent, primaryStudent)));
            when(cohort.getLessons()).thenReturn(List.of(new Lesson(), new Lesson(), new Lesson(), new Lesson()));

            List<Cohort> cohorts = List.of(cohort);

            List<CohortStats> result = new ArrayList<>();

            CohortUtils.calculateCohortStats(cohorts, result);

            assertEquals(1, result.size());
            CohortStats cohortStats = result.get(0);
            assertEquals(1L, cohortStats.getCohortId());
            assertEquals("cohort1", cohortStats.getCohortAlias());
            assertEquals(1, cohortStats.getPrimaryStudents());
            assertEquals(2, cohortStats.getOtherStudents());
            assertEquals(4, cohortStats.getTotalLessons());
        }
    }
}
