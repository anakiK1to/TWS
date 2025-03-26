package com.example.cli_client.command;

import com.example.cli_client.rest.PersonDto;
import com.example.cli_client.rest.RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Scanner;

public class FindByIdCommand extends Command {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final Scanner scanner;

    public FindByIdCommand(RestClient personWebService, ObjectMapper objectMapper, Scanner scanner) {
        super("find", "Найти person по ID");
        this.restClient = personWebService;
        this.objectMapper = objectMapper;
        this.scanner = scanner;
    }

    @Override
    public void execute(String[] args) {
        try {
            System.out.print("Введите ID человека: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            PersonDto person = restClient.getPersonById(id);
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
