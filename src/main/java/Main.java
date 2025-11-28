package main.java;

import main.java.commands.CommandHandler;
import main.java.model.Network;

import java.util.Scanner;

/**
 * main.java.Main class of the program.
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
     * main.java.Main method of the program.
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        System.out.println("Sim started. Enter commands or type 'quit' to exit.");
        Network network = new Network();
        CommandHandler commandHandler = new CommandHandler(network);
        Scanner scanner = new Scanner(System.in);

        boolean isRunning = true;
        while (isRunning) {
            String input = scanner.nextLine().trim();
            String response = commandHandler.handleCommand(input);

            printOutput(response);

            if (input.equalsIgnoreCase(QUIT)) {
                isRunning = false;
            }
        }

        scanner.close();
    }

    /**
     * Prints the output if it's not null or empty.
     * @param output the output to print
     */
    private static void printOutput(String output) {
        if (output != null && !output.isEmpty()) {
            System.out.println(output);
        }
    }
}
