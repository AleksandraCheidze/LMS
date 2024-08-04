package de.aittr.lmsbe.feedback.repository;

import de.aittr.lmsbe.feedback.model.FeedbackAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackAnswerRepository extends JpaRepository<FeedbackAnswer, Long> {
}
