package ru.itmo.standalone_server.repository;

import ru.itmo.standalone_server.model.Person;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class PersonRepository {
    private final EntityManagerFactory entityManagerFactory;

    public PersonRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public List<Person> findPerson(String query, int limit, int offset) {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Person> criteriaQuery = builder.createQuery(Person.class);
            Root<Person> root = criteriaQuery.from(Person.class);

            Predicate predicate = parseQueryToPredicate(query, builder, root);
            if (predicate != null) {
                criteriaQuery.where(predicate);
            }

            return entityManager.createQuery(criteriaQuery)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    // Основной метод парсинга строки запроса в Predicate
    private Predicate parseQueryToPredicate(String query, CriteriaBuilder builder, Root<Person> root) {
        if (query == null || query.trim().isEmpty()) {
            return null;
        }

        List<String> tokens = tokenize(query);
        Stack<Predicate> predicateStack = new Stack<>();
        Stack<String> operatorStack = new Stack<>();

        for (String token : tokens) {
            if (token.equals("(")) {
                operatorStack.push(token);
            } else if (token.equals(")")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    processOperator(builder, predicateStack, operatorStack.pop());
                }
                if (!operatorStack.isEmpty()) {
                    operatorStack.pop();
                }
            } else if (isLogicalOperator(token)) {
                while (!operatorStack.isEmpty() &&
                        !operatorStack.peek().equals("(") &&
                        precedence(operatorStack.peek()) >= precedence(token)) {
                    processOperator(builder, predicateStack, operatorStack.pop());
                }
                operatorStack.push(token);
            } else {
                predicateStack.push(parseCondition(token, builder, root));
            }
        }

        while (!operatorStack.isEmpty()) {
            processOperator(builder, predicateStack, operatorStack.pop());
        }

        return predicateStack.isEmpty() ? null : predicateStack.pop();
    }

    private List<String> tokenize(String query) {
        List<String> tokens = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        int i = 0;
        while (i < query.length()) {
            char c = query.charAt(i);
            if (c == '(' || c == ')') {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
                tokens.add(String.valueOf(c));
                i++;
            } else if (Character.isWhitespace(c)) {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
                i++;
            } else {
                token.append(c);
                i++;
            }
        }
        if (token.length() > 0) {
            tokens.add(token.toString());
        }
        return tokens;
    }

    private boolean isLogicalOperator(String token) {
        return token.equalsIgnoreCase("AND") || token.equalsIgnoreCase("OR");
    }

    private void processOperator(CriteriaBuilder builder, Stack<Predicate> predicateStack, String operator) {
        if (predicateStack.size() < 2) {
            throw new IllegalArgumentException("Недостаточно условий для оператора " + operator);
        }
        Predicate right = predicateStack.pop();
        Predicate left = predicateStack.pop();
        Predicate combined;
        if ("AND".equalsIgnoreCase(operator)) {
            combined = builder.and(left, right);
        } else if ("OR".equalsIgnoreCase(operator)) {
            combined = builder.or(left, right);
        } else {
            throw new IllegalArgumentException("Неизвестный логический оператор: " + operator);
        }
        predicateStack.push(combined);
    }

    private Predicate parseCondition(String token, CriteriaBuilder builder, Root<Person> root) {
        // Разбиваем условие по операторам: =, !=, >=, <=, >, <, ~, !~
        String[] parts = token.split("=|!=|>=|<=|>|<|~|!~", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Неверный формат запроса. Ожидается: 'поле оператор значение' -> " + token);
        }

        String field = parts[0].trim();
        int operatorStart = parts[0].length();
        int operatorEnd = token.length() - parts[1].length();
        String operator = token.substring(operatorStart, operatorEnd).trim();
        String value = parts[1].trim().replace("\"", "");

        // Получаем путь к атрибуту
        Path<Object> path;
        try {
            path = root.get(field);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Не найден атрибут '" + field + "' в классе " + root.getJavaType().getName());
        }

        if ("=".equals(operator)) {
            return builder.equal(path, value);
        } else if ("!=".equals(operator)) {
            return builder.notEqual(path, value);
        } else if (">".equals(operator)) {
            return builder.greaterThan(path.as(String.class), value);
        } else if (">=".equals(operator)) {
            return builder.greaterThanOrEqualTo(path.as(String.class), value);
        } else if ("<".equals(operator)) {
            return builder.lessThan(path.as(String.class), value);
        } else if ("<=".equals(operator)) {
            return builder.lessThanOrEqualTo(path.as(String.class), value);
        } else if ("~".equals(operator)) {
            return builder.like(path.as(String.class), value);
        } else if ("!~".equals(operator)) {
            return builder.notLike(path.as(String.class), value);
        } else {
            throw new IllegalArgumentException("Не поддерживается оператор: " + operator);
        }
    }

    // Определение приоритета логических операторов
    private int precedence(String operator) {
        if ("AND".equalsIgnoreCase(operator)) {
            return 2;
        } else if ("OR".equalsIgnoreCase(operator)) {
            return 1;
        } else {
            return 0;
        }
    }
}
