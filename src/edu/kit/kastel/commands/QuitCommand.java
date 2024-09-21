package edu.kit.kastel.commands;

/**
 * A command that quits the program.
 * @author utusr
 */
public class QuitCommand implements Command {
    private static final String ERROR_MESSAGE = "Error, Invalid command format. Use 'quit' without any arguments.";
    @Override
    public String execute(String[] args) {
        if (args.length > 1) {
            return ERROR_MESSAGE;
        }
        return null;
    }
}
