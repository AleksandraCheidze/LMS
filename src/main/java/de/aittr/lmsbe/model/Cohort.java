package de.aittr.lmsbe.model;

import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Audited
@Table(name = "cohort", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "alias"}))
public class Cohort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String githubRepository;

    @NotNull
    private String name;

    private String alias;

    @ManyToMany(mappedBy = "cohorts")
    @ToString.Exclude
    private Set<User> users;

    @NotAudited
    @OneToMany(mappedBy = "cohort")
    private List<Lesson> lessons = new ArrayList<>();

    public void addLesson(Lesson savedLesson) {
        this.lessons.add(savedLesson);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Cohort cohort = (Cohort) o;
        return getId() != null && Objects.equals(getId(), cohort.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Cohort{" +
                "id=" + id +
                ", githubRepository='" + githubRepository + '\'' +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                '}';
    }
}
