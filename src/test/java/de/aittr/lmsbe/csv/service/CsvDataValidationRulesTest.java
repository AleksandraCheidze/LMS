package de.aittr.lmsbe.csv.service;

import de.aittr.lmsbe.model.User;
import de.aittr.lmsbe.repository.UsersRepository;
import de.aittr.lmsbe.service.UsersService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CsvDataValidationRulesTest {

    @Mock
    List<CsvFieldError> errors;

    @Mock
    UsersService usersService;

    @InjectMocks
    private CsvDataValidationRules csvDataValidationRules;

    @Test
    void isEmptyCheckTest(){
        csvDataValidationRules.isEmptyCheck("Alex","Name",errors);
        assertEquals(0,errors.size());
    }

    @Test
    void isFieldEmptyTest(){
        String fieldValue = " ";

        boolean result = csvDataValidationRules.isFieldEmpty(fieldValue);
        assertTrue(result);
    }

    @Test
    void validateColumnNameTest(){
        csvDataValidationRules.isEmptyCheck("Alex","Name",errors);
        assertEquals(0,errors.size());

        csvDataValidationRules.isEmptyCheck(" ", "Name", errors);
        Mockito.verify(errors).add(new CsvFieldError("Name",
                "this field must not be empty "));
    }

    @Test
    void checkIfEmailExistsTest(){
        String email = "user@mail.com";

        when(usersService.isUserWithEmailExists("user@mail.com")).thenReturn(true);
        csvDataValidationRules.checkIfEmailExists(email,errors);

        Mockito.verify(errors).add(new CsvFieldError("EMAIL",
                "user with this email already exists"));
    }

    @Test
    void validateColumnEmailTest(){
        String email = "user@mail.com";
        String role = "ADMIN";

        csvDataValidationRules.isEmptyCheck("user@mail.com","EMAIL",errors);
        assertEquals(0,errors.size());

        when(usersService.isUserWithEmailExists("user@mail.com")).thenReturn(true);
        csvDataValidationRules.checkIfEmailExists(email,errors);
        Mockito.verify(errors).add(new CsvFieldError("EMAIL",
               "user with this email already exists"));

        csvDataValidationRules.validateColumnEmail(email,role,errors);
        Mockito.verify(errors).add(new CsvFieldError("EMAIL",
                "for 'ADMIN' the domain must be '@ait-tr.de'"));
    }


    @Test
    void validateColumnRoleTest(){
        String role = "STUDENT";
        csvDataValidationRules.isEmptyCheck(role,"ROLE",errors);
        assertEquals(0,errors.size());

        assertTrue((User.Role.contains(role)));
        csvDataValidationRules.isRoleValid("User","ROLE",errors);
        Mockito.verify(errors).add(new CsvFieldError("ROLE",
                "role name must be:" + Arrays.toString(User.Role.values())));
    }

    @Test
    void validateColumnPrimaryGroupTest(){
        String role = "STUDENT";

        csvDataValidationRules.validateColumnPrimaryGroup(" ",role,errors);
        Mockito.verify(errors).add(new CsvFieldError("PRIMARY_GROUP",
                "for 'STUDENT' this field cannot be empty"));
    }

}