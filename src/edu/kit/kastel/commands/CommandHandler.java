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
    private static final String LOAD_COMMAND = "load";
    private static final String LIST_COMMAND = "list";
    private static final String LIST_RANGE_COMMAND = "list range";
    private static final String LIST_SYSTEMS_COMMAND = "list systems";
    private static final String ADD_CONNECTION_COMMAND = "add connection";
    private static final String REMOVE_CONNECTION_COMMAND = "remove connection";
    private static final String ADD_COMPUTER_COMMAND = "add computer";
    private static final String REMOVE_COMPUTER_COMMAND = "remove computer";
    private static final String SEND_PACKET_COMMAND = "send packet";
    private static final String  QUIT_COMMAND = "quit";
    private static final String EMPTY_SPACE = " ";
    private static final String EMPTY_STRING = "";

    private final Map<String, Command> commands;
    private final Network network;

    /**
     * This constructor creates a new CommandHandler object with the given network.
     * @param network The network to handle commands for.
     */
    public CommandHandler(Network network) {
        this.network = network;
        commands = new HashMap<>();
        commands.put(LOAD_COMMAND, new LoadNetworkCommand(network));
        commands.put(LIST_COMMAND, new ListSubnetsCommand(network));
        commands.put(LIST_RANGE_COMMAND, new ListRangeCommand(network));
        commands.put(LIST_SYSTEMS_COMMAND, new ListSystemsCommand(network));
        commands.put(ADD_CONNECTION_COMMAND, new AddConnectionCommand(network));
        commands.put(REMOVE_CONNECTION_COMMAND, new RemoveConnectionCommand(network));
        commands.put(ADD_COMPUTER_COMMAND, new AddComputerCommand(network));
        commands.put(REMOVE_COMPUTER_COMMAND, new RemoveComputerCommand(network));
        commands.put(SEND_PACKET_COMMAND, new SendPacketCommand(network));
        commands.put(QUIT_COMMAND, new QuitCommand());
    }

    /**
     * This method handles the input command and executes it.
     * @param input The input command to handle.
     * @return The output of the command execution.
     */
    public String handleCommand(String input) {
        String[] parts = input.split("\\s+");
        String mainCommand = parts[0].toLowerCase();
        String subCommand = parts.length > 1 ? parts[1].toLowerCase() : EMPTY_STRING;

        Command command = commands.get(mainCommand + EMPTY_SPACE + subCommand);
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
