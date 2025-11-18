package domain.game.context;

import domain.game.Game;
import domain.game.Player;
import domain.game.Deck;
import java.util.HashMap;
import java.util.Map;

public class GameContext {
    private Game game;
    private Player currentPlayer;
    private Deck deck;
    private GameUIHandler uiHandler;
    private Map<String, Object> additionalParams;

    public GameContext(Game game, Player currentPlayer, Deck deck) {
        this.game = game;
        this.currentPlayer = currentPlayer;
        this.deck = deck;
        this.additionalParams = new HashMap<>();
    }

    public Game getGame() {
        return game;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Deck getDeck() {
        return deck;
    }

    public GameUIHandler getUIHandler() {
        return uiHandler;
    }

    public void setUIHandler(GameUIHandler handler) {
        this.uiHandler = handler;
    }

    public Player getNextAlivePlayer() {
        int index = (currentPlayer.getPlayerID() + 1) % game.getNumberOfPlayers();
        while (game.checkIfPlayerDead(index)) {
            index = (index + 1) % game.getNumberOfPlayers();
        }
        return game.getPlayerAtIndex(index);
    }

    public Player[] getAllPlayers() {
        Player[] players = new Player[game.getNumberOfPlayers()];
        for (int i = 0; i < game.getNumberOfPlayers(); i++) {
            players[i] = game.getPlayerAtIndex(i);
        }
        return players;
    }

    public boolean isPlayerAlive(int playerIndex) {
        return !game.checkIfPlayerDead(playerIndex);
    }

    public void addParameter(String key, Object value) {
        additionalParams.put(key, value);
    }

    public Object getParameter(String key) {
        return additionalParams.get(key);
    }

    public boolean hasParameter(String key) {
        return additionalParams.containsKey(key);
    }
}
