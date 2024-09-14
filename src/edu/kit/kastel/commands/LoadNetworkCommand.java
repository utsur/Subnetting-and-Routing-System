package edu.kit.kastel.commands;

import edu.kit.kastel.model.Network;
import edu.kit.kastel.util.NetworkLoader;
import edu.kit.kastel.util.OutputFormatter;

/**
 * Command to load a network from a file.
 * @author utsur
 */
public class LoadNetworkCommand implements Command {
    private static final String ERROR_FORMAT = "Error, Invalid command format. Use 'load network <path>'";
    private static final String ERROR_LOAD = "Error, Failed to load network.";
    private static final String NETWORK_STRING = "network";
    private final Network network;
    private final NetworkLoader loader;

    /**
     * Create a new LoadNetworkCommand.
     * @param network the network to load the data into
     */
    public LoadNetworkCommand(Network network) {
        this.network = network;
        this.loader = new NetworkLoader();
    }

    @Override
    public String execute(String[] args) {
        if (args.length != 3 || !args[1].equals(NETWORK_STRING)) {
            return ERROR_FORMAT;
        }
        String path = args[2];
        Network loadedNetwork = loader.loadNetwork(path);
        if (loadedNetwork == null) {
            return ERROR_LOAD;
        }

        network.updateFrom(loadedNetwork);
        return OutputFormatter.toMermaid(network);
    }
}
