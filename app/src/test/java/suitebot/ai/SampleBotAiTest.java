package suitebot.ai;

import org.junit.jupiter.api.Test;
import suitebot.game.Direction;
import suitebot.game.GameState;
import suitebot.game.GameStateFactory;

import static org.assertj.core.api.Assertions.assertThat;

class SampleBotAiTest
{
	@Test
	void testAvoidsObstacles()
	{
		String gameStateAsString = "***\n" +
								   "*1*\n" +
								   "   \n";

		GameState gameState = GameStateFactory.createFromString(gameStateAsString);

		//assertThat(new SampleBotAi().makeMove(1, gameState)).isEqualTo(Direction.DOWN);
	}

	@Test
	void testAvoidsObstacles2()
	{
		String gameStateAsString = "**********\n" +
				"* 1      *\n" +
				"*  *     *\n" +
				"*2 *  3  *\n" +
				"*  *     *\n" +
				"*  *     *\n" +
				"*        *\n" +
				"*   *    *\n" +
				"*   4    *\n" +
				"**********";

		GameState gameState = GameStateFactory.createFromString(gameStateAsString);

		//assertThat(new SampleBotAi().makeMove(1, gameState)).isEqualTo(Direction.RIGHT);
	}
}