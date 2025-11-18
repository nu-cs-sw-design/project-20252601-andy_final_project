package domain.game.cards;

import domain.game.context.GameContext;
import domain.game.effects.CardEffect;
import domain.game.CardType;

public interface PlayableCard {
    CardEffect play(GameContext context);

    boolean canBeNoped();

    boolean endsTurn();

    boolean validate(GameContext context);

    CardType getCardType();

    String getDescription();
}
