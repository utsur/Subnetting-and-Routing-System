package edu.kit.kastel.commands;

import edu.kit.kastel.model.Network;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for handling commands and executing them.
 * It contains a map of commands and their corresponding command objects.
 * @author utsur
 */
public class CommandHandler {
    private static final String ERROR_MESSAGE_UNKNOWN = "Error, Unknown command.";
    private final Map<String, Command> commands;

    /**
     * This constructor creates a new CommandHandler object with the given network.
     * @param network The network to handle commands for.
     */
    public CommandHandler(Network network) {
        commands = new HashMap<>();
        commands.put("load", new LoadNetworkCommand(network));
        commands.put("list", new ListSubnetsCommand(network));
        commands.put("quit", new QuitCommand());
    }

    /**
     * This method handles the input command and executes it.
     * @param input The input command to handle.
     * @return The output of the command execution.
     */
    public String handleCommand(String input) {
        String[] parts = input.split("\\s+");
        String mainCommand = parts[0].toLowerCase();

        Command command = commands.get(mainCommand);
        if (command != null) {
            return command.execute(parts);
        }
        return ERROR_MESSAGE_UNKNOWN;
    }

    /**
     * This method prints the output of the command.
     * @param output The output to print.
     */
    public void printOutput(String output) {
        if (output != null && !output.isEmpty()) {
            System.out.println(output);
        }
    }
}
