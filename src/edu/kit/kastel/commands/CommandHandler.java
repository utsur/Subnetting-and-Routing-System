package edu.kit.kastel.commands;

import edu.kit.kastel.commands.computer.AddComputerCommand;
import edu.kit.kastel.commands.computer.RemoveComputerCommand;
import edu.kit.kastel.commands.connection.AddConnectionCommand;
import edu.kit.kastel.commands.connection.RemoveConnectionCommand;
import edu.kit.kastel.commands.list.ListRangeCommand;
import edu.kit.kastel.commands.list.ListSubnetsCommand;
import edu.kit.kastel.commands.list.ListSystemsCommand;
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
    private static final String ERROR_NO_NETWORK = "Error, No network loaded. Use 'load network' first.";
    private final Map<String, Command> commands;
    private final Network network;

    /**
     * This constructor creates a new CommandHandler object with the given network.
     * @param network The network to handle commands for.
     */
    public CommandHandler(Network network) {
        this.network = network;
        commands = new HashMap<>();
        commands.put("load", new LoadNetworkCommand(network));
        commands.put("list", new ListSubnetsCommand(network));
        commands.put("list range", new ListRangeCommand(network));
        commands.put("list systems", new ListSystemsCommand(network));
        commands.put("add connection", new AddConnectionCommand(network));
        commands.put("remove connection", new RemoveConnectionCommand(network));
        commands.put("add computer", new AddComputerCommand(network));
        commands.put("remove computer", new RemoveComputerCommand(network));
        commands.put("send packet", new SendPacketCommand(network));
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
        String subCommand = parts.length > 1 ? parts[1].toLowerCase() : "";

        Command command = commands.get(mainCommand + " " + subCommand);
        if (command == null) {
            command = commands.get(mainCommand);
        }

        if (command != null) {
            // Check if network is loaded for all commands except 'load' and 'quit'
            if (!(command instanceof LoadNetworkCommand) && !(command instanceof QuitCommand)) {
                if (network.getSubnets().isEmpty()) {
                    return ERROR_NO_NETWORK;
                }
            }
            return command.execute(parts);
        }
        return ERROR_MESSAGE_UNKNOWN;
    }
}
