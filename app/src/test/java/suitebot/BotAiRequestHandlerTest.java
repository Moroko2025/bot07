package suitebot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import suitebot.ai.BotAi;
import suitebot.game.Direction;
import suitebot.game.GameState;

import static org.assertj.core.api.Assertions.assertThat;

class BotAiRequestHandlerTest
{
	private static final Direction AI_DIRECTION = Direction.LEFT;
	private static final String AI_NAME = "My AI";

	private BotRequestHandler REQUEST_HANDLER;

	@BeforeEach
	void setUp()
	{
		BotAi botAi = new BotAi()
		{
			@Override
			public Direction makeMove(int botId, GameState gameState)
			{
				return AI_DIRECTION;
			}

			@Override
			public String getName()
			{
				return AI_NAME;
			}
		};

		REQUEST_HANDLER = new BotRequestHandler(botAi);
	}

	@Test
	void testNameRequest() throws Exception
	{
		assertThat(REQUEST_HANDLER.processRequest(BotRequestHandler.NAME_REQUEST)).isEqualTo(AI_NAME);
	}
}