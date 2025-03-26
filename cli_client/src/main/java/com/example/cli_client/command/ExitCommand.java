package com.example.cli_client.command;

public class ExitCommand extends Command {
    public ExitCommand() {
        super("exit", "Выйти из программы");
    }

    @Override
    public void execute(String[] args) {
        System.out.println("Завершение работы...");
        System.exit(0);
    }
}