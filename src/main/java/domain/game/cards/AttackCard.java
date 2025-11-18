package domain.game.cards;

import domain.game.CardType;
import domain.game.Player;
import domain.game.context.GameContext;
import domain.game.effects.CardEffect;

public class AttackCard implements PlayableCard {
    private static final int TURNS_TO_ADD = 2;

    @Override
    public CardEffect play(GameContext context) {
        Player nextPlayer = context.getNextAlivePlayer();

        context.getGame().addAttackQueue(5);
        context.getGame().incrementAttackCounter();
        context.getGame().addAttacks();
        context.getGame().setAttacked(true);

        return new CardEffect(true, "Attack successful! Next player takes " + TURNS_TO_ADD + " turns", true);
    }

    @Override
    public boolean canBeNoped() {
        return true;
    }

    @Override
    public boolean endsTurn() {
        return true;
    }

    @Override
    public boolean validate(GameContext context) {
        return context.getNextAlivePlayer() != null;
    }

    @Override
    public CardType getCardType() {
        return CardType.ATTACK;
    }

    @Override
    public String getDescription() {
        return "Forces next player to take 2 turns";
    }
}
