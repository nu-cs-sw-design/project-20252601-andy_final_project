package domain.game.actions;

import domain.game.CardType;
import domain.game.Game;
import domain.game.InputProvider;
import domain.game.Player;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

public class ExplodingKittenActionTest {

    @Test
    public void testExplodingKittenActionWithDefuse() {
        // 1. Create Mocks
        Game game = EasyMock.createMock(Game.class);
        Player player = EasyMock.createMock(Player.class);
        InputProvider input = EasyMock.createMock(InputProvider.class);

        // 2. Define Expectations
        input.displayMessage("explodingKittenMessage");
        EasyMock.expect(player.hasCard(CardType.DEFUSE)).andReturn(true);
        EasyMock.expect(player.getIsCursed()).andReturn(false);
        
        input.displayMessage("defusedMessage");
        input.displayMessage("whereToInsertMessage");
        EasyMock.expect(game.getDeckSize()).andReturn(10).anyTimes();
        input.displayMessage("validRangeMessage", 10);
        
        EasyMock.expect(input.getExplodingKittenInsertionIndex(10)).andReturn(5);
        EasyMock.expect(player.getPlayerID()).andReturn(0);
        game.playDefuse(5, 0);
        player.setCursed(false);

        // 3. Replay Mocks
        EasyMock.replay(game, player, input);

        // 4. Execute Action
        ExplodingKittenAction action = new ExplodingKittenAction();
        action.execute(game, player, input);

        // 5. Verify Mocks
        EasyMock.verify(game, player, input);
    }

    @Test
    public void testExplodingKittenActionNoDefuse() {
        // 1. Create Mocks
        Game game = EasyMock.createMock(Game.class);
        Player player = EasyMock.createMock(Player.class);
        InputProvider input = EasyMock.createMock(InputProvider.class);

        // 2. Define Expectations
        input.displayMessage("explodingKittenMessage");
        EasyMock.expect(player.hasCard(CardType.DEFUSE)).andReturn(false);
        
        input.displayMessage("noDefuseCardMessage");
        input.displayMessage("youExplodedMessage");
        player.setIsDead();
        EasyMock.expect(player.getPlayerID()).andReturn(0);
        EasyMock.expect(game.getPlayerTurn()).andReturn(0);
        game.setCurrentPlayerNumberOfTurns(0);

        // 3. Replay Mocks
        EasyMock.replay(game, player, input);

        // 4. Execute Action
        ExplodingKittenAction action = new ExplodingKittenAction();
        action.execute(game, player, input);

        // 5. Verify Mocks
        EasyMock.verify(game, player, input);
    }
}
