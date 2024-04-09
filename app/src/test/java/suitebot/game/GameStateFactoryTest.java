package suitebot.game;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class GameStateFactoryTest
{
	@Test
	void testCreatingFromValidString() throws Exception
	{
		String gameStateAsString =
				"*2 *\n" +
				"4  *\n" +
				"    \n";

		GameState gameState = GameStateFactory.createFromString(gameStateAsString);

		assertThat(gameState.getPlanWidth()).isEqualTo(4);
		assertThat(gameState.getPlanHeight()).isEqualTo(3);
		assertThat(gameState.getAllBotIds()).containsExactlyInAnyOrder(2, 4);
		assertThat(gameState.getLiveBotIds()).containsExactlyInAnyOrder(2, 4);
		assertThat(gameState.getBotLocation(2)).isEqualTo(new Point(1, 0));
		assertThat(gameState.getBotLocation(4)).isEqualTo(new Point(0, 1));
		assertThat(gameState.getObstacleLocations()).containsExactlyInAnyOrder(
				new Point(0, 0), new Point(3, 0), new Point(3, 1));
	}

	@Test
	void invalidCharacter_shouldThrowException()
	{
		assertThatExceptionOfType(GameStateFactory.GameStateCreationException.class)
				.isThrownBy(() -> {
					GameStateFactory.createFromString("**1#");
				}).withMessage("unrecognized character: #");
	}

	@Test
	void nonRectangularPlan_shouldThrowException()
	{
		assertThatExceptionOfType(GameStateFactory.GameStateCreationException.class)
				.isThrownBy(() -> {
					GameStateFactory.createFromString("****\n****\n***\n****");
				}).withMessage("non-rectangular plan: line 3 width (3) is different from the line 1 width (4)");
	}
}
