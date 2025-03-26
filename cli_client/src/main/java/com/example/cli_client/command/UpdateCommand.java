package com.example.cli_client.command;

import com.example.cli_client.utils.CliUtil;
import ru.itmo.standalone_server.Person;
import ru.itmo.standalone_server.PersonDto;
import ru.itmo.standalone_server.WebService;

import java.util.Scanner;

public class UpdateCommand extends Command {
    private final WebService personWebService;
    private final Scanner scanner;

    public UpdateCommand(WebService personWebService, Scanner scanner) {
        super("update", "Обновить данные человека по ID");
        this.personWebService = personWebService;
        this.scanner = scanner;
    }

    @Override
    public void execute(String[] args) {
        try {
            System.out.print("Введите ID человека для обновления: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            Person existingPerson = personWebService.findPersonById(id);
            if (existingPerson == null) {
                System.out.println("Человек с ID " + id + " не найден. Обновление невозможно.");
                return;
            }

            System.out.println("Человек найден. Введите новые данные.");
            PersonDto updatedPersonDto = CliUtil.getPersonDtoFromInput(scanner);

            boolean success = personWebService.updatePerson(id, updatedPersonDto);
            if (success) {
                System.out.println("Данные человека успешно обновлены.");
            } else {
                System.out.println("Не удалось обновить данные человека.");
            }
        } catch (Exception e) {
            System.err.println("Ошибка при обновлении человека: " + e.getMessage());
        }
    }
}
