package de.aittr.lmsbe.repository;

import de.aittr.lmsbe.model.UserConfirmationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountsConfirmRepository extends JpaRepository<UserConfirmationCode, Long> {
    Optional<UserConfirmationCode> findByUuid(String uuid);

    UserConfirmationCode findByUserId(Long id);
}
