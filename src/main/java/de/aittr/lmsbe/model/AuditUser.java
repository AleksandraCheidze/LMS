package de.aittr.lmsbe.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import javax.persistence.Entity;
import java.util.Date;

@Entity
@Getter
@Setter
@RevisionEntity(UserRevisionListener.class)
public class AuditUser extends DefaultRevisionEntity {
    private String modifiedBy;
    private Date modifiedDate;

}
