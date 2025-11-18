package domain.game.cards;

import domain.game.CardType;
import domain.game.context.GameContext;
import domain.game.effects.CardEffect;

public class NopeCard implements PlayableCard {

    @Override
    public CardEffect play(GameContext context) {
        context.getCurrentPlayer().removeCardFromHand(
            context.getCurrentPlayer().getIndexOfCard(CardType.NOPE)
        );

        return new CardEffect(true, "Nope card played", false);
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
        return context.getCurrentPlayer().hasCard(CardType.NOPE);
    }

    @Override
    public CardType getCardType() {
        return CardType.NOPE;
    }

    @Override
    public String getDescription() {
        return "Counters another player's card";
    }

    public boolean canCounter(PlayableCard targetCard) {
        if (targetCard.getCardType() == CardType.EXPLODING_KITTEN ||
            targetCard.getCardType() == CardType.DEFUSE) {
            return false;
        }
        return true;
    }
}
