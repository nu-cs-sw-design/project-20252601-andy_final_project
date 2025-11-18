package domain.game.services;

import domain.game.CardType;
import domain.game.Player;
import domain.game.cards.PlayableCard;
import domain.game.context.GameContext;

public class CardValidator {

    public static boolean validateCardPlay(PlayableCard card, GameContext context) {
        Player player = context.getCurrentPlayer();

        if (!isCardInHand(player, card.getCardType())) {
            return false;
        }

        if (!card.validate(context)) {
            return false;
        }

        return true;
    }

    public static boolean canPlayerPlayCard(Player player, CardType cardType) {
        if (player.getIsDead()) {
            return false;
        }

        if (isUnplayableCard(cardType)) {
            return false;
        }

        return player.hasCard(cardType);
    }

    public static boolean isCardInHand(Player player, CardType cardType) {
        return player.hasCard(cardType);
    }

    public static boolean validatePlayerIndex(int playerIndex, int numberOfPlayers) {
        return playerIndex >= 0 && playerIndex < numberOfPlayers;
    }

    public static boolean validateDeckIndex(int deckIndex, int deckSize) {
        return deckIndex >= 0 && deckIndex <= deckSize;
    }

    private static boolean isUnplayableCard(CardType cardType) {
        return cardType == CardType.EXPLODING_KITTEN ||
               cardType == CardType.IMPLODING_KITTEN ||
               cardType == CardType.STREAKING_KITTEN ||
               cardType == CardType.DEFUSE;
    }
}
