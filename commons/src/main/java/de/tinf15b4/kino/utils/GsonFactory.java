package de.tinf15b4.kino.utils;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GsonFactory {

	public static Gson buildGson() {
		GsonBuilder builder = new GsonBuilder();

		builder.registerTypeAdapter(Date.class, new DateDeserializer());
		builder.registerTypeAdapter(Date.class, new DateSerializer());
		builder.registerTypeHierarchyAdapter(byte[].class, new ByteArrayToBase64TypeAdapter());

		Gson gson = builder.create();
		return gson;
	}

	private static class ByteArrayToBase64TypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
		public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			return Base64.getDecoder().decode(json.getAsString());
		}

		public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
			String base64 = Base64.getEncoder().encodeToString(src);
			return new JsonPrimitive(base64);
		}
	}

	private static class DateDeserializer implements JsonDeserializer<Date> {
		@Override
		public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			return new Date(json.getAsJsonPrimitive().getAsLong());
		}
	}

	private static class DateSerializer implements JsonSerializer<Date> {
		@Override
		public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
			return src == null ? null : new JsonPrimitive(src.getTime());
		}
	}

}
