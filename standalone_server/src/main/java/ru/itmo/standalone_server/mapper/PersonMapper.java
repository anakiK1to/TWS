package ru.itmo.standalone_server.mapper;

import ru.itmo.standalone_server.model.dtos.PersonDto;
import ru.itmo.standalone_server.model.entity.Person;

public class PersonMapper {

    public static PersonDto toDto(Person person) {
        if (person == null) {
            return null;
        }

        return PersonDto.builder()
                .name(person.getName())
                .surname(person.getSurname())
                .age(person.getAge())
                .patronymic(person.getPatronymic())
                .phoneNumber(person.getPhoneNumber())
                .build();
    }

    public static Person toEntity(PersonDto personDto) {
        if (personDto == null) {
            return null;
        }

        return Person.builder()
                .name(personDto.getName())
                .surname(personDto.getSurname())
                .age(personDto.getAge())
                .patronymic(personDto.getPatronymic())
                .phoneNumber(personDto.getPhoneNumber())
                .build();
    }
}