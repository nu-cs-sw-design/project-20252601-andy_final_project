package domain.game.effects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardEffect {
    private boolean success;
    private String message;
    private boolean shouldEndTurn;
    private List<Integer> affectedPlayers;
    private Map<String, Object> additionalData;

    public CardEffect(boolean success, String message, boolean shouldEndTurn) {
        this.success = success;
        this.message = message;
        this.shouldEndTurn = shouldEndTurn;
        this.affectedPlayers = new ArrayList<>();
        this.additionalData = new HashMap<>();
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public boolean shouldEndTurn() {
        return shouldEndTurn;
    }

    public List<Integer> getAffectedPlayers() {
        return affectedPlayers;
    }

    public void addData(String key, Object value) {
        additionalData.put(key, value);
    }

    public Object getData(String key) {
        return additionalData.get(key);
    }

    public boolean hasData(String key) {
        return additionalData.containsKey(key);
    }
}
