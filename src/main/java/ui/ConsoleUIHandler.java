package ui;

import domain.game.CardType;
import domain.game.Player;
import domain.game.context.GameUIHandler;
import java.util.InputMismatchException;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ConsoleUIHandler implements GameUIHandler {
    private Scanner scanner;
    private ResourceBundle messages;

    public ConsoleUIHandler(Scanner scanner, ResourceBundle messages) {
        this.scanner = scanner;
        this.messages = messages;
    }

    @Override
    public int askForShuffleCount(int min, int max) {
        while (true) {
            System.out.print("How many times to shuffle (" + min + "-" + max + "): ");
            try {
                int count = scanner.nextInt();
                if (count >= min && count <= max) {
                    return count;
                }
                System.out.println("Please enter a number between " + min + " and " + max);
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
            }
        }
    }

    @Override
    public int askForInsertionIndex(int maxIndex) {
        while (true) {
            System.out.print("Choose position to insert card (0 to " + maxIndex + "): ");
            try {
                int index = scanner.nextInt();
                if (index >= 0 && index <= maxIndex) {
                    return index;
                }
                System.out.println("Please enter a number between 0 and " + maxIndex);
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
            }
        }
    }

    @Override
    public int askForDefuseCardSelection(Player player) {
        System.out.println("Select which card is your Defuse:");
        int handSize = player.getHandSize();
        for (int i = 0; i < handSize; i++) {
            System.out.println((i + 1) + ". Card " + (i + 1));
        }

        while (true) {
            try {
                System.out.print("Your choice: ");
                int choice = scanner.nextInt();
                if (choice >= 1 && choice <= handSize) {
                    return choice - 1;
                }
                System.out.println("Invalid choice. Try again.");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
            }
        }
    }

    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }

    @Override
    public boolean confirmAction(String prompt) {
        System.out.print(prompt + " (1=Yes, 2=No): ");
        int choice = scanner.nextInt();
        return choice == 1;
    }

    @Override
    public boolean askToPlayNope(Player player) {
        System.out.print("Player " + player.getPlayerID() + ", do you want to play Nope? (1=Yes, 2=No): ");
        int choice = scanner.nextInt();
        return choice == 1;
    }
}
