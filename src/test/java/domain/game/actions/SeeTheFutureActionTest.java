package domain.game.actions;

import domain.game.Card;
import domain.game.CardType;
import domain.game.Game;
import domain.game.GameType;
import domain.game.InputProvider;
import domain.game.Player;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class SeeTheFutureActionTest {

    @Test
    public void testSeeTheFutureAction() {
        // 1. Create Mocks
        Game game = EasyMock.createMock(Game.class);
        Player player = EasyMock.createMock(Player.class);
        InputProvider input = EasyMock.createMock(InputProvider.class);

        // 2. Define Expectations
        // Expect Nope check
        EasyMock.expect(game.getNumberOfPlayers()).andReturn(2).anyTimes();
        EasyMock.expect(game.getPlayerTurn()).andReturn(0).anyTimes();
        EasyMock.expect(game.checkIfPlayerHasCard(1, CardType.NOPE)).andReturn(false);

        // Expect SeeTheFuture interaction
        input.displayMessage("decidedSeeFuture");
        EasyMock.expect(game.getGameType()).andReturn(GameType.EXPLODING_KITTENS);
        
        List<Card> mockCards = new ArrayList<>();
        EasyMock.expect(game.getTopCards(2)).andReturn(mockCards);
        input.showFutureCards(mockCards);
        EasyMock.expectLastCall();

        // 3. Replay Mocks
        EasyMock.replay(game, player, input);

        // 4. Execute Action
        SeeTheFutureAction action = new SeeTheFutureAction();
        action.execute(game, player, input);

        // 5. Verify Mocks
        EasyMock.verify(game, player, input);
    }
}
