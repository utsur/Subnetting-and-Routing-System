package edu.kit.kastel.commands;

/**
 * Interface for commands that can be executed by the {@link edu.kit.kastel.commands.CommandHandler}.
 * This interface defines the contract for all commands that can be executed by the CommandHandler.
 * Each command must implement this interface to be executed by the CommandHandler.
 * @author utsur
 */
public interface Command {

    /**
     * Executes the command with the given arguments.
     * @param args The arguments to execute the command with.
     * @return The output of the command execution as a String. If the command fails, an error message should be returned.
     */
    String execute(String[] args);
}
