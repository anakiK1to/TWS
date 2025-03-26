package com.example.cli_client.command;

import com.example.cli_client.rest.RestClient;
import com.example.cli_client.utils.CliUtil;
import com.example.cli_client.rest.PersonDto;

import java.util.Scanner;

public class CreateCommand extends Command {
    private final RestClient personWebService;
    private final Scanner scanner;

    public CreateCommand(RestClient personWebService, Scanner scanner) {
        super("create", "Создать нового person");
        this.personWebService = personWebService;
        this.scanner = scanner;
    }

    @Override
    public void execute(String[] args) {
        try {
            PersonDto personDto = CliUtil.getPersonDtoFromInput(scanner);
            int id = personWebService.addPerson(personDto);
            System.out.println("Создан человек с ID: " + id);
        } catch (Exception e) {
            System.err.println("Ошибка при создании person: " + e.getMessage());
        }
    }
}
