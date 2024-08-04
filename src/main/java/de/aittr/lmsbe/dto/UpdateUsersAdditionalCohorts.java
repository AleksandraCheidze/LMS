package de.aittr.lmsbe.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class UpdateUsersAdditionalCohorts {
    private List<Long> userIds;
    private List<Long> cohortIds;
}
