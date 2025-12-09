package domain.game.actions;

import domain.game.Game;
import domain.game.InputProvider;
import domain.game.Player;

public class ShuffleAction extends BaseCardAction {
    @Override
    public void execute(Game game, Player player, InputProvider input) {
        if (checkNope(game, input)) {
            return;
        }
        input.displayMessage("decidedShuffle");
        int count = input.getShuffleCount(100);
        game.playShuffle(count);
    }
}
