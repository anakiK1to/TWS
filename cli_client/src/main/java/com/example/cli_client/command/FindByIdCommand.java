package com.example.cli_client.command;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.itmo.standalone_server.Person;
import ru.itmo.standalone_server.WebService;

import java.util.Scanner;

public class FindByIdCommand extends Command {
    private final WebService personWebService;
    private final ObjectMapper objectMapper;
    private final Scanner scanner;

    public FindByIdCommand(WebService personWebService, ObjectMapper objectMapper, Scanner scanner) {
        super("find", "Найти человека по ID");
        this.personWebService = personWebService;
        this.objectMapper = objectMapper;
        this.scanner = scanner;
    }

    @Override
    public void execute(String[] args) {
        try {
            System.out.print("Введите ID человека: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            Person person = personWebService.findPersonById(id);
            if (person == null) {
                System.out.println("Человек с ID " + id + " не найден.");
                return;
            }

            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(person);
            System.out.println("Найден человек:");
            System.out.println(json);
        } catch (Exception e) {
            System.err.println("Ошибка при поиске человека: " + e.getMessage());
        }
    }
}
