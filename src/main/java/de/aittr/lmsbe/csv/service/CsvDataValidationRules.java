package de.aittr.lmsbe.csv.service;

import de.aittr.lmsbe.model.User;
import de.aittr.lmsbe.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvDataValidationRules {

    private final UsersService usersService;

    public void validateColumnEmail(String filedValue, String roleValue, List<CsvFieldError> errors) {
        isEmptyCheck(filedValue, "EMAIL", errors);
        checkIfEmailExists(filedValue, errors);
        if ("ADMIN".equals(roleValue)) {
            if (!filedValue.endsWith("@ait-tr.de")) {
                errors.add(
                        new CsvFieldError("EMAIL",
                                "for 'ADMIN' the domain must be '@ait-tr.de'")
                );
            }
        }
    }

    public void validateColumnPrimaryGroup(String filedValue, String roleValue, List<CsvFieldError> errors) {
        if ("STUDENT".equals(roleValue)) {
            if (isFieldEmpty(filedValue)) {
                errors.add(new CsvFieldError("PRIMARY_GROUP",
                        "for 'STUDENT' this field cannot be empty"));
            }
        }
    }

    public void validateColumnName(String columnName, String fieldValue, List<CsvFieldError> errors) {
        isEmptyCheck(fieldValue, columnName, errors);
    }

    public void validateColumnRole(String fieldValue, List<CsvFieldError> errors) {
        String columnName = "ROLE";
        isEmptyCheck(fieldValue, columnName, errors);
        isRoleValid(fieldValue, columnName, errors);
    }

    public boolean isFieldEmpty(String field) {
        return field == null || field.isBlank();
    }

    public void isRoleValid(String filedValue, String columnName, List<CsvFieldError> errors) {
        if (isFieldEmpty(filedValue) || !User.Role.contains(filedValue)) {
            String errorMsg = "role name must be:" + Arrays.toString(User.Role.values());
            errors.add(new CsvFieldError(columnName, errorMsg));
        }
    }

    public void isEmptyCheck(String fieldValue, String columnName, List<CsvFieldError> errors) {
        if (isFieldEmpty(fieldValue)) {
            errors.add(new CsvFieldError(columnName, "this field must not be empty "));
        }
    }

    public void checkIfEmailExists(String fieldValue, List<CsvFieldError> errors) {
        if (usersService.isUserWithEmailExists(fieldValue)) {
            errors.add(new CsvFieldError("EMAIL", "user with this email already exists"));
        }
    }
}
