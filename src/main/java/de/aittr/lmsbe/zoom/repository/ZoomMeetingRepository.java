package de.aittr.lmsbe.zoom.repository;

import de.aittr.lmsbe.model.User;
import de.aittr.lmsbe.zoom.entity.ZoomMeeting;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ZoomMeetingRepository extends JpaRepository<ZoomMeeting, String> {

    ZoomMeeting findByUuid(String uuid);

    @Query("SELECT zm " +
            "FROM ZoomMeeting zm " +
            "WHERE zm.user = :teacher " +
            "AND zm.user.role in (:roles) " +
            "AND (zm.meetingTime BETWEEN :from AND :to)" +
            "ORDER BY zm.meetingTime")
    List<ZoomMeeting> findAllForUserAndDates(@NonNull User teacher,
                                             @NonNull LocalDateTime from,
                                             @NonNull LocalDateTime to,
                                             @NonNull List<User.Role> roles);
}
