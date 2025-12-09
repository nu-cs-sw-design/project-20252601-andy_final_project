package domain.game.actions;

import domain.game.CardType;
import domain.game.Game;
import domain.game.InputProvider;
import domain.game.Player;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

public class ShuffleActionTest {

    @Test
    public void testShuffleAction() {
        // 1. Create Mocks
        Game game = EasyMock.createMock(Game.class);
        Player player = EasyMock.createMock(Player.class);
        InputProvider input = EasyMock.createMock(InputProvider.class);

        // 2. Define Expectations
        // Expect Nope check (assuming no Nope played)
        EasyMock.expect(game.getNumberOfPlayers()).andReturn(2).anyTimes();
        EasyMock.expect(game.getPlayerTurn()).andReturn(0).anyTimes();
        EasyMock.expect(game.checkIfPlayerHasCard(1, CardType.NOPE)).andReturn(false);

        // Expect Shuffle interaction
        input.displayMessage("decidedShuffle");
        EasyMock.expect(input.getShuffleCount(100)).andReturn(5); // User inputs 5
        game.playShuffle(5); // Verify Game.playShuffle is called with 5
        EasyMock.expectLastCall();

        // 3. Replay Mocks
        EasyMock.replay(game, player, input);

        // 4. Execute Action
        ShuffleAction action = new ShuffleAction();
        action.execute(game, player, input);

        // 5. Verify Mocks
        EasyMock.verify(game, player, input);
    }
}
