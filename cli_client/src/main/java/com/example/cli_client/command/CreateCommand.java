package com.example.cli_client.command;

import com.example.cli_client.utils.CliUtil;
import ru.itmo.standalone_server.PersonDto;
import ru.itmo.standalone_server.WebService;

import java.util.Scanner;

public class CreateCommand extends Command {
    private final WebService personWebService;
    private final Scanner scanner;

    public CreateCommand(WebService personWebService, Scanner scanner) {
        super("create", "Создать нового человека");
        this.personWebService = personWebService;
        this.scanner = scanner;
    }

    @Override
    public void execute(String[] args) {
        try {
            PersonDto personDto = CliUtil.getPersonDtoFromInput(scanner);
            int id = personWebService.createPerson(personDto);
            System.out.println("Создан человек с ID: " + id);
        } catch (Exception e) {
            System.err.println("Ошибка при создании человека: " + e.getMessage());
        }
    }
}
