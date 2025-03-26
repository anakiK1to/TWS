package ru.itmo.j2ee.repository;

import ru.itmo.j2ee.model.entity.Person;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@ApplicationScoped
public class PersonRepository {

    @PersistenceContext(unitName = "PostgresDS")
    private EntityManager entityManager;

    public List<Person> findPerson(String query, int limit, int offset) {
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
    }

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
                operatorStack.pop();
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

    private void processOperator(CriteriaBuilder builder,
                                 Stack<Predicate> predicateStack,
                                 String operator) {
        if (predicateStack.size() < 2) {
            throw new IllegalArgumentException("Not enough operands for operator: " + operator);
        }
        Predicate right = predicateStack.pop();
        Predicate left = predicateStack.pop();

        switch (operator.toUpperCase()) {
            case "AND":
                predicateStack.push(builder.and(left, right));
                break;
            case "OR":
                predicateStack.push(builder.or(left, right));
                break;
            default:
                throw new IllegalArgumentException("Unsupported operator: " + operator);
        }
    }

    private Predicate parseCondition(String token,
                                     CriteriaBuilder builder,
                                     Root<Person> root) {
        String[] parts = token.split("=|!=|>=|<=|>|<|~|!~", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid condition format: " + token);
        }

        String field = parts[0].trim();
        String operator = token.substring(parts[0].length(),
                token.length() - parts[1].length()).trim();
        String value = parts[1].trim().replace("\"", "");

        Path<Object> path = root.get(field);

        switch (operator) {
            case "=":
                return builder.equal(path, value);
            case "!=":
                return builder.notEqual(path, value);
            case ">":
                return builder.greaterThan(path.as(String.class), value);
            case ">=":
                return builder.greaterThanOrEqualTo(path.as(String.class), value);
            case "<":
                return builder.lessThan(path.as(String.class), value);
            case "<=":
                return builder.lessThanOrEqualTo(path.as(String.class), value);
            case "~":
                return builder.like(path.as(String.class), value);
            case "!~":
                return builder.notLike(path.as(String.class), value);
            default:
                throw new IllegalArgumentException("Unsupported operator: " + operator);
        }
    }

    private int precedence(String operator) {
        return operator.equalsIgnoreCase("AND") ? 2 :
                operator.equalsIgnoreCase("OR") ? 1 : 0;
    }
}