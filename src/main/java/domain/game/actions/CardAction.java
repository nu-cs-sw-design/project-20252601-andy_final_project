package domain.game.actions;

import domain.game.Game;
import domain.game.InputProvider;
import domain.game.Player;

public interface CardAction {
    void execute(Game game, Player player, InputProvider input);
}
