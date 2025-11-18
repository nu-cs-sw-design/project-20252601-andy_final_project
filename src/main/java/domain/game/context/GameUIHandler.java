package domain.game.context;

import domain.game.Player;

public interface GameUIHandler {
    int askForShuffleCount(int min, int max);

    int askForInsertionIndex(int maxIndex);

    int askForDefuseCardSelection(Player player);

    void displayMessage(String message);

    boolean confirmAction(String prompt);

    boolean askToPlayNope(Player player);
}
