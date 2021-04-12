package com.whatsaround.utility;

import java.lang.reflect.Type;

import org.joda.time.DateTime;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class JodaSerializer implements JsonSerializer<DateTime>, JsonDeserializer<DateTime>{

	@Override
	public DateTime deserialize(JsonElement json, Type type,
			JsonDeserializationContext context) throws JsonParseException {

		return new DateTime(json.getAsJsonPrimitive().getAsString());
	}

	@Override
	public JsonElement serialize(DateTime src, Type typeOfSrc,
			JsonSerializationContext jsonContext) {
		 return new JsonPrimitive(src.toString());
	}

}
