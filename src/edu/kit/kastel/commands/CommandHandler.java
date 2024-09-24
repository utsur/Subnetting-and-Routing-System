package edu.kit.kastel.commands;

import edu.kit.kastel.commands.computer.AddComputer;
import edu.kit.kastel.commands.computer.RemoveComputer;
import edu.kit.kastel.commands.connection.AddConnection;
import edu.kit.kastel.commands.connection.RemoveConnection;
import edu.kit.kastel.commands.list.ListRange;
import edu.kit.kastel.commands.list.ListSubnets;
import edu.kit.kastel.commands.list.ListSystems;
import edu.kit.kastel.model.Network;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for handling commands and executing them.
 * This class acts as a central point for processing all user commands,
 * mapping them to their respective Command objects and executing them.
 * @author utsur
 */
public class CommandHandler {
    private static final String ERROR_MESSAGE_UNKNOWN = "Error, Unknown command.";
    private static final String ERROR_NO_NETWORK = "Error, No network loaded. Use 'load network' first.";
    private static final String WHITESPACE_REGEX = "\\s+";
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
        commands.put(LOAD_COMMAND, new LoadNetwork(network));
        commands.put(LIST_COMMAND, new ListSubnets(network));
        commands.put(LIST_RANGE_COMMAND, new ListRange(network));
        commands.put(LIST_SYSTEMS_COMMAND, new ListSystems(network));
        commands.put(ADD_CONNECTION_COMMAND, new AddConnection(network));
        commands.put(REMOVE_CONNECTION_COMMAND, new RemoveConnection(network));
        commands.put(ADD_COMPUTER_COMMAND, new AddComputer(network));
        commands.put(REMOVE_COMPUTER_COMMAND, new RemoveComputer(network));
        commands.put(SEND_PACKET_COMMAND, new SendPacket(network));
        commands.put(QUIT_COMMAND, new Quit());
    }

    /**
     * Handles the input command by parsing it, finding the corresponding Command object, and executing it if valid.
     * @param input The input command to handle.
     * @return The output of the command execution, or an error message if the command is nor valid.
     */
    public String handleCommand(String input) {
        String[] parts = input.split(WHITESPACE_REGEX);
        String mainCommand = parts[0].toLowerCase();
        String subCommand = parts.length > 1 ? parts[1].toLowerCase() : EMPTY_STRING;

        Command command = commands.get(mainCommand + EMPTY_SPACE + subCommand);
        if (command == null) {
            command = commands.get(mainCommand);
        }

        if (command != null) {
            // Check if network is loaded for all commands except 'load' and 'quit'
            if (!(command instanceof LoadNetwork) && !(command instanceof Quit) && network.getSubnets().isEmpty()) {
                return ERROR_NO_NETWORK;
            }
            // Execute the command if it is valid.
            return command.execute(parts);
        }
        return ERROR_MESSAGE_UNKNOWN;
    }
}
