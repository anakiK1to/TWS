package com.example.cli_client.command;


import com.example.cli_client.rest.RestClient;

import java.util.Scanner;

public class DeleteCommand extends Command {
    private final RestClient personWebService;
    private final Scanner scanner;

    public DeleteCommand(RestClient personWebService, Scanner scanner) {
        super("delete", "Удалить человека по ID");
        this.personWebService = personWebService;
        this.scanner = scanner;
    }

    @Override
    public void execute(String[] args) {
        try {
            System.out.print("Введите ID человека для удаления: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            boolean success = personWebService.removePerson(id);
            if (success) {
                System.out.println("Человек успешно удалён.");
            } else {
                System.out.println("Человек с ID " + id + " не найден.");
            }
        } catch (Exception e) {
            System.err.println("Ошибка при удалении человека: " + e.getMessage());
        }
    }
}
