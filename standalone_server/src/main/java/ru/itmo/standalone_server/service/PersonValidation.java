package ru.itmo.standalone_server.service;

import ru.itmo.standalone_server.exceptions.ExceptionBean;
import ru.itmo.standalone_server.exceptions.WebException;
import ru.itmo.standalone_server.model.dtos.PersonDto;

public class PersonValidation {
    private static final String PHONE_REGEX = "^\\+?[0-9]{10,15}$";

    public static void validatePersonDto(PersonDto personDto) throws WebException {
        StringBuilder errorMessageBuilder = new StringBuilder();

        if (personDto == null) {
            throw new WebException("PersonDto is null", new ExceptionBean("Provided PersonDto is null"));
        }
        if (personDto.getName() == null || personDto.getName().isEmpty()) {
            errorMessageBuilder.append("Name field cannot be null or empty. ");
        }
        if (personDto.getSurname() == null) {
            errorMessageBuilder.append("Surname field cannot be null or empty. ");
        }
        if (personDto.getAge() < 0) {
            errorMessageBuilder.append("Age must be a non-negative integer. ");
        }
        if (personDto.getPhoneNumber() != null && !personDto.getPhoneNumber().matches(PHONE_REGEX)) {
            errorMessageBuilder.append("Phone number must match the format: +78005553535.");
        }
        if (errorMessageBuilder.length() == 0) {
            throw new WebException("Validation failed for PersonDto", new ExceptionBean(errorMessageBuilder.toString().trim()));
        }
    }
}