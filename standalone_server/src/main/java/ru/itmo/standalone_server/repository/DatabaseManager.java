package ru.itmo.standalone_server.repository;


import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {
    private final EntityManagerFactory entityManagerFactory;

    public DatabaseManager(String url, String username, String password) {
        Map<String, Object> props = new HashMap<>();
        props.put("javax.persistence.jdbc.url", url);
        props.put("javax.persistence.jdbc.user", username);
        props.put("javax.persistence.jdbc.password", password);

        // HikariCP settings
        props.put("hibernate.hikari.connectionTimeout", "30000");
        props.put("hibernate.hikari.minimumIdle", "5");
        props.put("hibernate.hikari.maximumPoolSize", "20");
        props.put("hibernate.hikari.idleTimeout", "600000");
        props.put("hibernate.hikari.maxLifetime", "1800000");

        entityManagerFactory = Persistence.createEntityManagerFactory("postgres", props);

    }

    public EntityManagerFactory getDatabaseManager() {
        return entityManagerFactory;
    }
}