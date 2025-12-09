package domain.game.actions;

import domain.game.CardType;
import domain.game.Game;
import domain.game.InputProvider;

public abstract class BaseCardAction implements CardAction {

    protected boolean checkNope(Game game, InputProvider input) {
        return checkAllPlayersForNope(game, input, game.getPlayerTurn());
    }

    private boolean checkAllPlayersForNope(Game game, InputProvider input, int originalPlayerIndex) {
        for (int i = 0; i < game.getNumberOfPlayers(); i++) {
            if (i == originalPlayerIndex) {
                continue;
            }
            if (game.checkIfPlayerHasCard(i, CardType.NOPE)) {
                boolean wantsToPlay = input.askPlayerToPlayNope(i);
                if (wantsToPlay) {
                    new NopeAction().execute(game, game.getPlayerAtIndex(i), input);
                    return !checkAllPlayersForNope(game, input, i);
                } else {
                    input.displayMessage("playerDidNotPlayNope", i);
                }
            }
        }
        return false;
    }
}
