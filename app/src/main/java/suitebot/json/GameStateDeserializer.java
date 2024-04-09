package suitebot.json;

import com.google.gson.*;
import suitebot.game.GameState;
import suitebot.game.GameStateFactory;
import suitebot.game.ImmutableGameState;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GameStateDeserializer implements JsonDeserializer<GameState>
{
	@Override
	public GameState deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
	{
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		return ImmutableGameState.builder(deserializeGamePlan(jsonObject.getAsJsonArray("gamePlan")))
				.setBotIds(deserializeIntegerArray(jsonObject.getAsJsonArray("botIds")))
				.build();
	}

	private static GameState deserializeGamePlan(JsonArray gamePlanJson)
	{
		return GameStateFactory.createFromString(
				StreamSupport.stream(gamePlanJson.spliterator(), false)
						.map(JsonElement::getAsString)
						.collect(Collectors.joining("\n"))
		);
	}

	private static List<Integer> deserializeIntegerArray(JsonArray integerArrayJson)
	{
		return StreamSupport.stream(integerArrayJson.spliterator(), false)
				.map(JsonElement::getAsInt)
				.collect(Collectors.toList());
	}
}
