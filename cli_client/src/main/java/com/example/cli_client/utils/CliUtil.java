package com.example.cli_client.utils;

import com.example.cli_client.command.*;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.itmo.standalone_server.PersonService;
import ru.itmo.standalone_server.WebService;
import ru.itmo.standalone_server.PersonDto;


import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CliUtil {
    public static int getIntInput(Scanner scanner, String prompt, int defaultValue) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (input.isEmpty()) {
                return defaultValue;
            }
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка ввода. Введите число.");
            }
        }
    }

    public static Map<String, Command> produceCommands(String soapUrl) throws Exception {
        Map<String, Command> commands = new HashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();
        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
        objectMapper.setDefaultPrettyPrinter(prettyPrinter);

        URL url = new URL(soapUrl);
        PersonService personService = new PersonService(url);
        WebService personWebServiceProxy = personService.getPersonServicePort();

        ExitCommand exitCommand = new ExitCommand();
        commands.put(exitCommand.getName(), exitCommand);
        Scanner scanner = new Scanner(System.in);

        FilterCommand filterPersonCommand = new FilterCommand(personWebServiceProxy, objectMapper, scanner);
        commands.put(filterPersonCommand.getName(), filterPersonCommand);

        FindByIdCommand findPersonByIdCommand = new FindByIdCommand(personWebServiceProxy, objectMapper, scanner);
        commands.put(findPersonByIdCommand.getName(), findPersonByIdCommand);

        CreateCommand createPersonCommand = new CreateCommand(personWebServiceProxy, scanner);
        commands.put(createPersonCommand.getName(), createPersonCommand);

        UpdateCommand updatePersonCommand = new UpdateCommand(personWebServiceProxy, scanner);
        commands.put(updatePersonCommand.getName(), updatePersonCommand);

        DeleteCommand deletePersonCommand = new DeleteCommand(personWebServiceProxy, scanner);
        commands.put(deletePersonCommand.getName(), deletePersonCommand);


        return commands;
    }

    public static PersonDto getPersonDtoFromInput(Scanner scanner) {
        System.out.println("Введите детали Person:");
        System.out.print("Имя: ");
        String name = scanner.nextLine();
        System.out.print("Фамилия: ");
        String surname = scanner.nextLine();
        System.out.print("Отчество: ");
        String address = scanner.nextLine();
        System.out.print("Возраст: ");
        int age = Integer.parseInt(scanner.nextLine());
        System.out.print("Телефон: ");
        String phoneNumber = scanner.nextLine();

        PersonDto personDto = new PersonDto();
        personDto.setName(name);
        personDto.setSurname(surname);
        personDto.setAge(age);
        personDto.setPatronymic(address);
        personDto.setPhoneNumber(phoneNumber);

        return personDto;
    }

}
