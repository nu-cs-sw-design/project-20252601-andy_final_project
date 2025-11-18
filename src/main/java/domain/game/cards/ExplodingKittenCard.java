package domain.game.cards;

import domain.game.CardType;
import domain.game.Player;
import domain.game.context.GameContext;
import domain.game.context.GameUIHandler;
import domain.game.effects.CardEffect;

public class ExplodingKittenCard implements PlayableCard {

    @Override
    public CardEffect play(GameContext context) {
        Player player = context.getCurrentPlayer();

        if (checkForDefuse(player)) {
            return handleDefuse(context, player);
        }

        return handleDeath(context, player);
    }

    @Override
    public boolean canBeNoped() {
        return false;
    }

    @Override
    public boolean endsTurn() {
        return true;
    }

    @Override
    public boolean validate(GameContext context) {
        return true;
    }

    @Override
    public CardType getCardType() {
        return CardType.EXPLODING_KITTEN;
    }

    @Override
    public String getDescription() {
        return "Kills player unless they have a Defuse card";
    }

    private boolean checkForDefuse(Player player) {
        return player.hasCard(CardType.DEFUSE);
    }

    private CardEffect handleDefuse(GameContext context, Player player) {
        GameUIHandler ui = context.getUIHandler();
        int deckSize = context.getDeck().getDeckSize();

        int position = ui.askForInsertionIndex(deckSize);

        player.removeCardFromHand(player.getIndexOfCard(CardType.DEFUSE));
        context.getGame().getDeck().insertExplodingKittenAtIndex(position);

        if (player.getIsCursed()) {
            player.setCursed(false);
        }

        return new CardEffect(true, "Defused! Exploding Kitten reinserted at position " + position, true);
    }

    private CardEffect handleDeath(GameContext context, Player player) {
        player.setIsDead();
        context.getGame().setCurrentPlayerNumberOfTurns(0);

        context.getUIHandler().displayMessage("BOOM! You exploded!");

        return new CardEffect(false, "Player eliminated", true);
    }

    private boolean checkStreakingKitten(Player player) {
        return player.hasCard(CardType.STREAKING_KITTEN) &&
               !player.hasCard(CardType.EXPLODING_KITTEN);
    }
}
