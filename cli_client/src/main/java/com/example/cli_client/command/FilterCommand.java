package com.example.cli_client.command;


import com.example.cli_client.rest.PersonDto;
import com.example.cli_client.rest.RestClient;
import com.example.cli_client.utils.CliUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Scanner;

/**
 * Команда для фильтрации объектов Person с интерактивным вводом
 */
public class FilterCommand extends Command {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final Scanner scanner;

    public FilterCommand(RestClient personWebService,
                         ObjectMapper objectMapper,
                         Scanner scanner) {
        super("search", "фильтрация людей с интерактивным вводом");
        this.restClient = personWebService;
        this.objectMapper = objectMapper;
        this.scanner = scanner;
    }

    @Override
    public void execute(String[] args) {
        try {
            printHelp();
            String query = readQuery();
            int limit = readLimit();
            int offset = readOffset();

            List<PersonDto> result = restClient.searchPersons(query, limit, offset);
            printResult(result);
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    private void printHelp() {
        System.out.println("\nFiltering options:");
        System.out.println("- Используйте AND/OR для логических операций");
        System.out.println("- Supported operators: =, !=, >, <, >=, <=, ~, !~");
        System.out.println("- Пример: 'age>18 AND name~\"Anton*\"'");
    }

    private String readQuery() {
        System.out.print("\nВведите query: ");
        return scanner.nextLine().trim();
    }

    private int readLimit() {
        return CliUtil.getIntInput(scanner, "Введите макс. записей (изначально 20): ", 20);
    }

    private int readOffset() {
        return CliUtil.getIntInput(scanner, "Введите оффсет (изначально 0): ", 0);
    }

    private void printResult(List<PersonDto> persons) throws Exception {
        String json = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(persons);
        System.out.println(json);
    }
}