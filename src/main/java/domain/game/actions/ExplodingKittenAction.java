package domain.game.actions;

import domain.game.CardType;
import domain.game.Game;
import domain.game.InputProvider;
import domain.game.Player;

public class ExplodingKittenAction implements CardAction {

    @Override
    public void execute(Game game, Player player, InputProvider input) {
        input.displayMessage("explodingKittenMessage");

        if (!player.hasCard(CardType.DEFUSE)) {
            input.displayMessage("noDefuseCardMessage");
            input.displayMessage("youExplodedMessage");
            player.setIsDead();
            if (player.getPlayerID() == game.getPlayerTurn()) {
                game.setCurrentPlayerNumberOfTurns(0);
            }
            return;
        }

        if (player.getIsCursed()) {
            input.displayMessage("cursedExplodingMessage");
            boolean defused = handleCursedDefuse(game, player, input);
            if (!defused) {
                return;
            }
        }

        input.displayMessage("defusedMessage");
        input.displayMessage("whereToInsertMessage");
        input.displayMessage("validRangeMessage", game.getDeckSize());

        int insertIndex = input.getExplodingKittenInsertionIndex(game.getDeckSize());
        game.playDefuse(insertIndex, player.getPlayerID());
        player.setCursed(false); // Reset curse after successful defuse
    }

    private boolean handleCursedDefuse(Game game, Player player, InputProvider input) {
        while (true) {
            input.displayMessage("findDefuseCardMessage", player.getHandSize() - 1);
            int cardIndex = input.getInputInteger();
            
            // Validate index
            if (cardIndex < 0 || cardIndex >= player.getHandSize()) {
                input.displayMessage("invalidInputMessage");
                continue;
            }

            domain.game.Card card = player.getCardAt(cardIndex);

            if (card.getCardType() == CardType.DEFUSE) {
                return true; 
            } else if (card.getCardType() == CardType.EXPLODING_KITTEN) {
                input.displayMessage("anotherExplodingKittenMessage");
                player.removeCardFromHand(cardIndex);
                this.execute(game, player, input);
                if (player.getIsDead()) {
                    return false;
                }
                input.displayMessage("defusedFirstExplodingKitten");
                if (!player.hasCard(CardType.DEFUSE)) {
                     input.displayMessage("noDefuseCardMessage");
                     input.displayMessage("youExplodedMessage");
                     player.setIsDead();
                     return false;
                }
                continue; 
            } else if (card.getCardType() == CardType.STREAKING_KITTEN && player.hasCard(CardType.EXPLODING_KITTEN)) {
                input.displayMessage("discardStreakingKittenMessage");
                player.removeCardFromHand(cardIndex);
                int explIndex = player.getIndexOfCard(CardType.EXPLODING_KITTEN);
                player.removeCardFromHand(explIndex);
                
                this.execute(game, player, input);
                if (player.getIsDead()) return false;
                
                input.displayMessage("defusedFirstExplodingKitten");
                if (!player.hasCard(CardType.DEFUSE)) {
                     input.displayMessage("noDefuseCardMessage");
                     input.displayMessage("youExplodedMessage");
                     player.setIsDead();
                     return false;
                }
                continue;
            } else {
                input.displayMessage("notDefuseCardMessage");
                player.removeCardFromHand(cardIndex);
                input.displayMessage("discardCardMessage");
                input.displayMessage("reenterDefuseMessage");
            }
        }
    }
}

