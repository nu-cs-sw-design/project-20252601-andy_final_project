package domain.game.services;

import domain.game.CardType;
import domain.game.cards.PlayableCard;
import domain.game.cards.AttackCard;
import domain.game.cards.NopeCard;
import domain.game.cards.ShuffleCard;
import domain.game.cards.ExplodingKittenCard;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CardFactory {
    private static CardFactory instance;
    private Map<CardType, Supplier<PlayableCard>> cardCreators;

    private CardFactory() {
        cardCreators = new HashMap<>();
        registerCardCreators();
    }

    public static CardFactory getInstance() {
        if (instance == null) {
            instance = new CardFactory();
        }
        return instance;
    }

    private void registerCardCreators() {
        cardCreators.put(CardType.ATTACK, AttackCard::new);
        cardCreators.put(CardType.NOPE, NopeCard::new);
        cardCreators.put(CardType.SHUFFLE, ShuffleCard::new);
        cardCreators.put(CardType.EXPLODING_KITTEN, ExplodingKittenCard::new);
    }

    public PlayableCard createCard(CardType type) {
        Supplier<PlayableCard> creator = cardCreators.get(type);
        if (creator == null) {
            throw new IllegalArgumentException("Unknown card type: " + type);
        }
        return creator.get();
    }

    public boolean isPlayableCardType(CardType type) {
        return cardCreators.containsKey(type);
    }
}
