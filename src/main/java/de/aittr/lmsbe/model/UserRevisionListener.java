package de.aittr.lmsbe.model;

import de.aittr.lmsbe.security.details.AuthenticatedUser;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;

public class UserRevisionListener implements RevisionListener {


    private String getModifiedBy(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser) {
            AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
            return authenticatedUser.getUsername();
        }
        return "anonymousUser";
    }

    @Override
    public void newRevision(Object revisionEntity) {
        AuditUser auditUser = (AuditUser) revisionEntity;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        auditUser.setModifiedBy(getModifiedBy(authentication));
        auditUser.setModifiedDate(new Date());
    }

}