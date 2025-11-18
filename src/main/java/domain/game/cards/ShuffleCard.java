package domain.game.cards;

import domain.game.CardType;
import domain.game.Deck;
import domain.game.context.GameContext;
import domain.game.context.GameUIHandler;
import domain.game.effects.CardEffect;

public class ShuffleCard implements PlayableCard {
    private static final int MAX_SHUFFLES = 100;

    @Override
    public CardEffect play(GameContext context) {
        GameUIHandler ui = context.getUIHandler();
        int count = ui.askForShuffleCount(1, MAX_SHUFFLES);

        Deck deck = context.getDeck();
        for (int i = 0; i < count; i++) {
            deck.shuffleDeck();
        }

        return new CardEffect(true, "Shuffled deck " + count + " times", false);
    }

    @Override
    public boolean canBeNoped() {
        return true;
    }

    @Override
    public boolean endsTurn() {
        return false;
    }

    @Override
    public boolean validate(GameContext context) {
        return context.getDeck().getDeckSize() > 0;
    }

    @Override
    public CardType getCardType() {
        return CardType.SHUFFLE;
    }

    @Override
    public String getDescription() {
        return "Shuffles the deck";
    }
}
