package com.example.cli_client.command;

import com.example.cli_client.rest.PersonDto;
import com.example.cli_client.rest.RestClient;
import com.example.cli_client.utils.CliUtil;
import java.util.Scanner;

public class UpdateCommand extends Command {
    private final RestClient personWebService;
    private final Scanner scanner;

    public UpdateCommand(RestClient personWebService, Scanner scanner) {
        super("update", "Обновить данные Person по ID");
        this.personWebService = personWebService;
        this.scanner = scanner;
    }

    @Override
    public void execute(String[] args) {
        try {
            System.out.print("Введите ID Person для обновления: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            PersonDto existingPerson = personWebService.getPersonById(id);
            if (existingPerson == null) {
                System.out.println("Person с ID " + id + " не найден. Обновление невозможно.");
                return;
            }

            System.out.println("Person найден. Введите новые данные.");
            PersonDto updatedPersonDto = CliUtil.getPersonDtoFromInput(scanner);

            boolean success = personWebService.updatePerson(id, updatedPersonDto);
            if (success) {
                System.out.println("Данные person успешно обновлены.");
            } else {
                System.out.println("Не удалось обновить данные person.");
            }
        } catch (Exception e) {
            System.err.println("Ошибка при обновлении person: " + e.getMessage());
        }
    }
}
