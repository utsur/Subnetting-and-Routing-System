package edu.kit.kastel.commands;

/**
 * A command that quits the program.
 * @author utusr
 */
public class QuitCommand implements Command {
    @Override
    public String execute(String[] args) {
        if (args.length > 1) {
            return "Error, Invalid command format. Use 'quit' without any arguments.";
        }
        return null;
    }
}
