package domain.game.actions;

import domain.game.CardType;
import domain.game.Game;
import domain.game.InputProvider;
import domain.game.Player;

public class NopeAction implements CardAction {
    @Override
    public void execute(Game game, Player player, InputProvider input) {
        input.displayMessage("decidedToPlayNope", player.getPlayerID());
        game.removeCardFromHand(player.getPlayerID(), CardType.NOPE);
        input.displayMessage("successfullyPlayedNope", player.getPlayerID());
    }
}
