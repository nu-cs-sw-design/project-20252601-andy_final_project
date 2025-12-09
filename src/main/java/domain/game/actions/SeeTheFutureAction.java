package domain.game.actions;

import domain.game.Card;
import domain.game.Game;
import domain.game.GameType;
import domain.game.InputProvider;
import domain.game.Player;
import java.util.List;

public class SeeTheFutureAction extends BaseCardAction {
    @Override
    public void execute(Game game, Player player, InputProvider input) {
        if (checkNope(game, input)) {
            return;
        }
        input.displayMessage("decidedSeeFuture");
        
        int cardsToReveal = 2; // Default for Exploding Kittens
        if (game.getGameType() == GameType.STREAKING_KITTENS) {
             cardsToReveal = 5;
        }
        
        List<Card> cards = game.getTopCards(cardsToReveal);
        input.showFutureCards(cards);
    }
}
