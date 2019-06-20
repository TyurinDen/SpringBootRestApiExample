package com.websystique.springboot.testPackage;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.websystique.springboot.service.vkInfoBotClasses.commands.Command;
import com.websystique.springboot.service.vkInfoBotClasses.commands.FindClientsById;

import java.io.IOException;
import java.util.Map;

public class Main {
    public static Multimap<String, Map<String, JsonElement>> jsonResultMultiMap = ArrayListMultimap.create();

    public static void main(String[] args) {
        Command clientCommand = new FindClientsById();

    }

    private static void handleObject(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            JsonToken token = reader.peek();
            if (token.equals(JsonToken.BEGIN_ARRAY))
                handleArray(reader);
            else if (token.equals(JsonToken.BEGIN_OBJECT)) {
                handleObject(reader);
            } else if (token.equals(JsonToken.END_OBJECT)) {
                reader.endObject();
                return;
            } else {
                handleNonArrayToken(reader, token);
            }
        }

    }

    /**
     * Handle a json array. The first token would be JsonToken.BEGIN_ARRAY.
     * Arrays may contain objects or primitives.
     *
     * @param reader
     * @throws IOException
     */
    public static void handleArray(JsonReader reader) throws IOException {
        reader.beginArray();
        while (true) {
            JsonToken token = reader.peek();
            if (token.equals(JsonToken.END_ARRAY)) {
                reader.endArray();
                break;
            } else if (token.equals(JsonToken.BEGIN_OBJECT)) {
                handleObject(reader);
            } else if (token.equals(JsonToken.END_OBJECT)) {
                reader.endObject();
            } else
                handleNonArrayToken(reader, token);
        }
    }

    /**
     * Handle non array non object tokens
     *
     * @param reader
     * @param token
     * @throws IOException
     */
    public static void handleNonArrayToken(JsonReader reader, JsonToken token) throws IOException {
        if (token.equals(JsonToken.NAME)) {
            System.out.println(JsonToken.NAME);
            System.out.println(reader.nextName());
        } else if (token.equals(JsonToken.STRING))
            System.out.println(reader.nextString());
        else if (token.equals(JsonToken.NUMBER))
            System.out.println(reader.nextInt());
        else
            reader.skipValue();
    }
}
