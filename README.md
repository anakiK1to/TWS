Работа №1. Поиск с помощью SOAP-сервиса

Первая лабораторная работа состоит из трех основных модулей:
1) cli_client - Клиент для взаимодейстия с сервисом
2) standalone_server - standalone приложение\
3) Wildfly + j2ee

Для запуска использовалось: 
Jdk Azul 17
Актуальная версия Maven
Актуальная версия Wildfly
psql 42.2.12


Проверка: 
mvn clean install 
java -jar lab1-standalone.jar
java -jar client-1.0.0.jar на 8080 порту 

Пример 
1) выбор операции
2) набор нужного запроса согласно инструкциям клиента 



```
public class CliSoap {
    private static final String BANNER =
                    "================================\n" +
                    "  SOAP-клиент для работы с persons\n" +
                    "================================\n";

    public static void main(String[] args) {
        String soapUrl = getSoapUrl(args);
        if (soapUrl == null) return;

        try {
            Map<String, Command> commands = CliUtil.produceCommands(soapUrl);
            Scanner scanner = new Scanner(System.in);

            showWelcomeMessage(commands);
            processUserInput(commands, scanner);

        } catch (Exception e) {
            System.err.println("Ошибка инициализации: " + e.getMessage());
        }
    }

    private static String getSoapUrl(String[] args) {
        String url = System.getenv("SOAP_SERVICE_URL");
        if (args.length > 0) url = args[0];

        if (url == null || url.isEmpty()) {
            System.err.println("URL SOAP-сервиса должен быть указан через:");
            System.err.println(" - Переменную окружения SOAP_SERVICE_URL");
            System.err.println(" - Аргумент командной строки");
            return null;
        }
        return url;
    }

    private static void showWelcomeMessage(Map<String, Command> commands) {
        System.out.println(BANNER);
        System.out.println("Доступные команды:");

        int index = 1;
        for (Command cmd : commands.values()) {
            System.out.printf("%2d. %-10s - %s%n",
                    index++,
                    cmd.getName(),
                    cmd.getDescription()
            );
        }

        System.out.println("\nУправление:");
        System.out.println("- Введите название команды или её номер");
        System.out.println("- 'очистить' - очистка экрана");
        System.out.println("- 'выход'    - завершение работы\n");
    }

    private static void processUserInput(Map<String, Command> commands, Scanner scanner) {
        while (true) {
            try {
                System.out.print("Введите команду > ");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("очистить")) {
                    clearConsole();
                    continue;
                }
                if (input.equalsIgnoreCase("выход")) {
                    System.out.println("Завершение работы...");
                    return;
                }

                processCommand(commands, input, scanner);

            } catch (Exception e) {
                System.err.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private static void processCommand(Map<String, Command> commands, String input, Scanner scanner) {
        Command command = null;

        try {
            int commandNumber = Integer.parseInt(input);
            command = (Command) commands.values().toArray()[commandNumber-1];
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            command = commands.get(input.toLowerCase());
        }

        if (command != null) {
            command.execute(new String[0]);
        } else {
            System.err.println("Команда не найдена!");
        }
    }

    private static void clearConsole() {
        try {
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls")
                        .inheritIO()
                        .start()
                        .waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.err.println("Не удалось очистить консоль: " + e.getMessage());
        }
    }
}
```
