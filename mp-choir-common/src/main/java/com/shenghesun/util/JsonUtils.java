package com.shenghesun.util;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtils {
	private static final ObjectMapper JSON = new ObjectMapper();

	static {
		JSON.setSerializationInclusion(Include.NON_NULL);
		JSON.configure(SerializationFeature.INDENT_OUTPUT, Boolean.TRUE);
	}

	public static String toJson(Object obj) {
		try {
			return JSON.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static <T> T toJsonObject(String jsonString, Class<T> valueType) {
		try {
			return JSON.readValue(jsonString, valueType);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}