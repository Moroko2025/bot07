package suitebot.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class YourBotIdDeserializer implements JsonDeserializer<Integer>
{
	@Override
	public Integer deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
	{
		return jsonElement.getAsJsonObject().getAsJsonPrimitive("yourBotId").getAsInt();
	}
}
