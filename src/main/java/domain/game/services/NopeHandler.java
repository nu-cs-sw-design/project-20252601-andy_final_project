package domain.game.services;

import domain.game.CardType;
import domain.game.Game;
import domain.game.Player;
import domain.game.cards.PlayableCard;
import domain.game.context.GameUIHandler;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NopeHandler {
    private Game game;
    private GameUIHandler uiHandler;

    public NopeHandler(Game game, GameUIHandler uiHandler) {
        this.game = game;
        this.uiHandler = uiHandler;
    }

    public boolean processNopeChain(PlayableCard playedCard, Player playingPlayer) {
        if (!canCardBeNoped(playedCard)) {
            return false;
        }

        int nopeCount = 0;
        boolean continueChecking = true;

        while (continueChecking) {
            continueChecking = false;

            for (Player player : getOtherPlayers(playingPlayer)) {
                if (player.hasCard(CardType.NOPE) && !player.getIsDead()) {
                    boolean wantsToNope = uiHandler.askToPlayNope(player);

                    if (wantsToNope) {
                        player.removeCardFromHand(player.getIndexOfCard(CardType.NOPE));
                        uiHandler.displayMessage("Player " + player.getPlayerID() + " played Nope!");
                        nopeCount++;
                        continueChecking = true;
                        break;
                    }
                }
            }
        }

        return nopeCount % 2 == 1;
    }

    public boolean canCardBeNoped(PlayableCard card) {
        return card.canBeNoped();
    }

    private List<Player> getOtherPlayers(Player excludePlayer) {
        Player[] allPlayers = new Player[game.getNumberOfPlayers()];
        for (int i = 0; i < game.getNumberOfPlayers(); i++) {
            allPlayers[i] = game.getPlayerAtIndex(i);
        }

        return Arrays.stream(allPlayers)
            .filter(p -> p.getPlayerID() != excludePlayer.getPlayerID())
            .collect(Collectors.toList());
    }
}
