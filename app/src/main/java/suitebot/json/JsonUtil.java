package suitebot.json;

import com.google.gson.GsonBuilder;
import suitebot.game.GameState;

public class JsonUtil
{
	public static GameState deserializeGameState(String json)
	{
		return new GsonBuilder()
				.registerTypeAdapter(GameState.class, new GameStateDeserializer())
				.create()
				.fromJson(json, GameState.class);
	}

	public static int deserializeYourBotId(String json)
	{
		return new GsonBuilder()
				.registerTypeAdapter(Integer.class, new YourBotIdDeserializer())
				.create()
				.fromJson(json, Integer.class);
	}
}
