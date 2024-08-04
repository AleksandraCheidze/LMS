package de.aittr.lmsbe.feedback.repository;

import de.aittr.lmsbe.feedback.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
