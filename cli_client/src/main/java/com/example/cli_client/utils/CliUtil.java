package com.example.cli_client.utils;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.cli_client.command.Command;
import com.example.cli_client.command.ExitCommand;
import com.example.cli_client.command.FilterCommand;
import ru.itmo.standalone_server.PersonService;
import ru.itmo.standalone_server.WebService;


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
        WebService personWebServiceProxy = personService.getWebServicePort();

        ExitCommand exitCommand = new ExitCommand();
        commands.put(exitCommand.getName(), exitCommand);
        Scanner scanner = new Scanner(System.in);

        FilterCommand filterPersonCommand = new FilterCommand(personWebServiceProxy, objectMapper, scanner);
        commands.put(filterPersonCommand.getName(), filterPersonCommand);

        return commands;
    }
}
