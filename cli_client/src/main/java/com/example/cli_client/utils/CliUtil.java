package com.example.cli_client.utils;

import com.example.cli_client.command.*;
import com.example.cli_client.rest.PersonDto;
import com.example.cli_client.rest.RestClient;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJacksonProvider;
import org.jboss.resteasy.spi.ResteasyProviderFactory;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
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

    public static Map<String, Command> produceCommands(String restUrl) throws Exception {
        Map<String, Command> commands = new HashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();
        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
        objectMapper.setDefaultPrettyPrinter(prettyPrinter);

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.property(ClientProperties.PROXY_URI, "");

        Client client = ClientBuilder.newClient(clientConfig).register(JacksonJsonProvider.class);

        RestClient personRestClient = new RestClient(restUrl, client);

        ExitCommand exitCommand = new ExitCommand();
        commands.put(exitCommand.getName(), exitCommand);
        Scanner scanner = new Scanner(System.in);

        FilterCommand filterPersonCommand = new FilterCommand(personRestClient, objectMapper, scanner);
        commands.put(filterPersonCommand.getName(), filterPersonCommand);

        FindByIdCommand findPersonByIdCommand = new FindByIdCommand(personRestClient, objectMapper, scanner);
        commands.put(findPersonByIdCommand.getName(), findPersonByIdCommand);

        CreateCommand createPersonCommand = new CreateCommand(personRestClient, scanner);
        commands.put(createPersonCommand.getName(), createPersonCommand);

        UpdateCommand updatePersonCommand = new UpdateCommand(personRestClient, scanner);
        commands.put(updatePersonCommand.getName(), updatePersonCommand);

        DeleteCommand deletePersonCommand = new DeleteCommand(personRestClient, scanner);
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
