package edu.kit.kastel.commands;

/**
 * Interface for commands that can be executed by the {@link edu.kit.kastel.commands.CommandHandler}.
 * @author utsur
 */
public interface Command {

    /**
     * Executes the command with the given arguments.
     * @param args The arguments to execute the command with.
     * @return The output of the command execution.
     */
    String execute(String[] args);
}
