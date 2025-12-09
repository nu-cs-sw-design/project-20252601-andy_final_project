package domain.game.actions;

import domain.game.CardType;
import domain.game.Game;
import domain.game.InputProvider;
import domain.game.Player;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

public class NopeActionTest {

    @Test
    public void testNopeAction() {
        // 1. Create Mocks
        Game game = EasyMock.createMock(Game.class);
        Player player = EasyMock.createMock(Player.class);
        InputProvider input = EasyMock.createMock(InputProvider.class);

        // 2. Define Expectations
        EasyMock.expect(player.getPlayerID()).andReturn(1).anyTimes();
        input.displayMessage("decidedToPlayNope", 1);
        game.removeCardFromHand(1, CardType.NOPE);
        input.displayMessage("successfullyPlayedNope", 1);

        // 3. Replay Mocks
        EasyMock.replay(game, player, input);

        // 4. Execute Action
        NopeAction action = new NopeAction();
        action.execute(game, player, input);

        // 5. Verify Mocks
        EasyMock.verify(game, player, input);
    }
}
