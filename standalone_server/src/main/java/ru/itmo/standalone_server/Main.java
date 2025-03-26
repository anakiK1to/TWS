package ru.itmo.standalone_server;

import java.util.Map;

import ru.itmo.standalone_server.repository.DatabaseManager;
import ru.itmo.standalone_server.repository.PersonRepository;
import ru.itmo.standalone_server.server.WebService;
import ru.itmo.standalone_server.service.PersonService;

import javax.xml.ws.Endpoint;


public class Main {
    public static void main(String[] args) {
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");

        Map<String, String> env = System.getenv();
        String dbUrl = env.getOrDefault("Database_Url", "jdbc:postgresql://localhost:5432/postgres");
        String dbUser = env.getOrDefault("Database_User", "postgres");
        String dbPassword = env.getOrDefault("Database_Password", "postgres");

        DatabaseManager entityManagerFactoryProvider = new DatabaseManager(
                dbUrl,
                dbUser,
                dbPassword
        );
        PersonRepository personRepository = new PersonRepository(entityManagerFactoryProvider.getDatabaseManager());
        PersonService personService = new PersonService(personRepository);

        String url = env.getOrDefault("Project_Url", "http://localhost:8080/PersonService");
        Endpoint.publish(url, new WebService(personService));

        System.out.println("Server started");
    }
}
