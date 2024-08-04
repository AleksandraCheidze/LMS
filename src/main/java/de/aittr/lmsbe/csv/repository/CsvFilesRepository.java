package de.aittr.lmsbe.csv.repository;

import de.aittr.lmsbe.csv.model.CsvFileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CsvFilesRepository extends JpaRepository<CsvFileInfo, Long> {

}
