package edu.kit.kastel;

import edu.kit.kastel.commands.CommandHandler;
import edu.kit.kastel.model.Network;

import java.util.Scanner;

/**
 * Main class of the program.
 * This class is used to start the program and handle the user input.
 * The program will run until the user types "quit".
 * @author utsur
 */
public final class Main {
    private static final String QUIT = "quit";

    private Main() {
        // private constructor to hide the implicit public one.
    }

    /**
     * Main method of the program.
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        Network network = new Network();
        CommandHandler commandHandler = new CommandHandler(network);
        Scanner scanner = new Scanner(System.in);

        boolean isRunning = true;
        while (isRunning) {
            String input = scanner.nextLine().trim();
            String response = commandHandler.handleCommand(input);

            if (response != null) {
                commandHandler.printOutput(response);
            }

            if (input.equalsIgnoreCase(QUIT)) {
                isRunning = false;
            }
        }

        scanner.close();
    }
}
