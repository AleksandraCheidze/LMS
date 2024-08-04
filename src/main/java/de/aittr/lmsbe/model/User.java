package de.aittr.lmsbe.model;

import de.aittr.lmsbe.csv.model.CsvFileInfo;
import de.aittr.lmsbe.zoom.entity.ZoomMeeting;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Audited
@Table(name = "account")
public class User {

    public enum Role {
        STUDENT, TEACHER, ADMIN;

        public static boolean contains(String value) {
            for (Role role : values()) {
                if (role.name().equalsIgnoreCase(value)) {
                    return true;
                }
            }
            return false;
        }

        public static Optional<Role> findRole(String value) {
            return Arrays.stream(Role.values())
                    .filter(role -> role.name().equalsIgnoreCase(value))
                    .findAny();
        }
    }


    public enum State {
        NOT_CONFIRMED, CONFIRMED;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true)
    private String email;

    private String passwordHash;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private Role role;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private State state;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    private String country;

    private String phone;

    private Boolean isActive;

    @Column(unique = true)
    private String zoomAccount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "primary_cohort_id", referencedColumnName = "id")
    private Cohort primaryCohort;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "student_cohort",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "cohort_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "cohort_id"}))
    @ToString.Exclude
    private Set<Cohort> cohorts;

    @NotAudited
    @OneToMany(mappedBy = "user")
    private Set<CsvFileInfo> csvFiles;

    @NotAudited
    @OneToMany(mappedBy = "teacher")
    private final List<Lesson> lessons = new ArrayList<>();

    @NotAudited
    @OneToMany(mappedBy = "user")
    private final List<ZoomMeeting> zoomMeetings = new ArrayList<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
