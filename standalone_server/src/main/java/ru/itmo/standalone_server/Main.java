package ru.itmo.standalone_server;

import java.net.URI;
import java.util.Map;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sun.net.httpserver.HttpServer;
import lombok.SneakyThrows;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import ru.itmo.standalone_server.controller.PersonRestController;
import ru.itmo.standalone_server.mapper.PersonExceptionMapper;
import ru.itmo.standalone_server.repository.DatabaseManager;
import ru.itmo.standalone_server.repository.PersonRepository;
import ru.itmo.standalone_server.service.PersonService;

import javax.ws.rs.core.UriBuilder;

public class Main {
    @SneakyThrows
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

        ResourceConfig config = new ResourceConfig();
        config.register(JacksonJsonProvider.class);
        config.register(new PersonRestController(personService));
        config.register(new PersonExceptionMapper());

        URI url = UriBuilder.fromUri("http://localhost/").port(8080).build();
        HttpServer server = JdkHttpServerFactory.createHttpServer(url, config);

        System.out.println("Server started");
        System.in.read();
        server.stop(0);
    }
}
