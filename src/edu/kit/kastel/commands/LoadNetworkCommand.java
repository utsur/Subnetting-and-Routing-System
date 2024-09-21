package edu.kit.kastel.commands;

import edu.kit.kastel.model.Network;
import edu.kit.kastel.util.FileHelper;
import edu.kit.kastel.util.NetworkLoader;

import java.util.List;

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
        // Read the file content and print it to the console.
        List<String> fileContent = FileHelper.readAllLines(path);
        if (fileContent.isEmpty()) {
            return null; // No error message, FileHelper already printed an error message.
        }

        for (String line : fileContent) {
            System.out.println(line);
        }
        // Parse and validate the file content.
        Network loadedNetwork = loader.loadNetwork(path);
        if (loadedNetwork == null) {
            return ERROR_LOAD;
        }
        // Actualize the network with the loaded data.
        network.updateFrom(loadedNetwork);
        network.updateBGPTables(); // Initialize BGP tables after loading.
        return null;
    }
}
