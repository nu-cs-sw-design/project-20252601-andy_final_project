package domain.game;

public interface InputProvider {
    int getExplodingKittenInsertionIndex(int maxIndex);
    int getShuffleCount(int maxShuffles);
    int getInputInteger();
    boolean askPlayerToPlayNope(int playerIndex);
    void showFutureCards(java.util.List<Card> cards);
    void displayMessage(String messageKey, Object... args);

}
