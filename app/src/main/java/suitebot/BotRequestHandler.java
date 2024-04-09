package suitebot;

import suitebot.ai.BotAi;
import suitebot.game.Direction;
import suitebot.game.GameState;
import suitebot.game.Move;
import suitebot.json.JsonUtil;
import suitebot.server.SimpleRequestHandler;

public class BotRequestHandler implements SimpleRequestHandler
{
	public static final String NAME_REQUEST = "NAME";

	private final BotAi botAi;

	public BotRequestHandler(BotAi botAi)
	{
		this.botAi = botAi;
	}

	@Override
	public String processRequest(String request)
	{
		try
		{
			return processRequestInternal(request);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return e.toString();
		}
	}

	private String processRequestInternal(String request)
	{
		if (NAME_REQUEST.equals(request))
			return botAi.getName();

		return processMoveRequest(request);
	}

	private String processMoveRequest(String request)
	{
		int botId = JsonUtil.deserializeYourBotId(request);
		GameState gameState = JsonUtil.deserializeGameState(request);

		if (isBotDead(botId, gameState)) {
			return null;
		}

		Direction direction = botAi.makeMove(botId, gameState);
		if (direction == null) {
			return null;
		}
		return new Move(direction).toString();
	}

	private boolean isBotDead(int botId, GameState gameState)
	{
		return !gameState.getLiveBotIds().contains(botId);
	}
}
