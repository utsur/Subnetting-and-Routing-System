package main.java.commands;

import main.java.model.Network;
import main.java.util.FileHelper;
import main.java.util.NetworkLoader;

import java.util.List;

/**
 * Command to load a network from a file.
 * This class handles the parsing of the load command, reading the file, and updating the network with the loaded configuration.
 * @author utsur
 */
public class LoadNetwork implements Command {
    private static final String ERROR_FORMAT = "Error, Invalid command format. Use 'load network <path>'";
    private static final String NETWORK_STRING = "network";
    private static final int FORMAT_ARGS = 3;
    private final Network network;
    private final NetworkLoader loader;

    /**
     * Creates a new LoadNetwork Command.
     * @param network the network to load the data into.
     */
    public LoadNetwork(Network network) {
        this.network = network;
        this.loader = new NetworkLoader();
    }

    @Override
    public String execute(String[] args) {
        if (args.length != FORMAT_ARGS || !args[1].equals(NETWORK_STRING)) {
            return ERROR_FORMAT;
        }
        String path = args[2];
        // Reading of the file content.
        List<String> fileContent = FileHelper.readAllLines(path);
        if (fileContent.isEmpty()) {
            return null; // FileHelper already printed the error message.
        }
        // print the network file content.
        for (String line : fileContent) {
            System.out.println(line);
        }
        // Loading and validation of the network.
        Network loadedNetwork = loader.loadNetwork(path);
        if (loadedNetwork == null) {
            return null; // The loader already prints error messages.
        }
        // Update the network after loading.
        network.updateFrom(loadedNetwork);
        network.updateBGPTables(); // Initialisation of the BGP tables.
        return null;
    }
}
