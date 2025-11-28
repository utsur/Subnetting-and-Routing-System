package main.java.commands;

/**
 * Represents a command to quit the program.
 * This command takes no arguments and signals the program to terminate.
 * @author utusr
 */
public class Quit implements Command {
    private static final String ERROR_MESSAGE = "Error, Invalid command format. Use 'quit' without any arguments.";

    @Override
    public String execute(String[] args) {
        if (args.length > 1) {
            return ERROR_MESSAGE;
        }
        return null;
    }
}
