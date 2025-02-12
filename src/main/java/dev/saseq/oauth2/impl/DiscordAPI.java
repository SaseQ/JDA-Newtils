package dev.saseq.oauth2.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.saseq.oauth2.entities.OAuth2Connection;
import dev.saseq.oauth2.entities.OAuth2Guild;
import dev.saseq.oauth2.entities.OAuth2User;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class DiscordAPI {

    public static final String BASE_URI = "https://discord.com/api";
    private static final Gson gson = new GsonBuilder().serializeNulls().enableComplexMapKeySerialization().create();
    private final String accessToken;

    public OAuth2User fetchUser() throws IOException {
        return toObject(handleGet("/users/@me"), OAuth2User.class);
    }

    public List<OAuth2Guild> fetchGuilds() throws IOException {
        return Arrays.asList(toObject(handleGet("/users/@me/guilds"), OAuth2Guild[].class));
    }

    public List<OAuth2Connection> fetchConnections() throws IOException {
        return Arrays.asList(toObject(handleGet("/users/@me/connections"), OAuth2Connection[].class));
    }

    private static <T> T toObject(String str, Class<T> clazz) {
        return gson.fromJson(str, clazz);
    }

    private String handleGet(String path) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BASE_URI + path)
                .addHeader("Authorization", "Bearer " + accessToken)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}
