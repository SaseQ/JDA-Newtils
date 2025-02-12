package dev.saseq.oauth2.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.saseq.oauth2.entities.OAuth2Tokens;
import lombok.RequiredArgsConstructor;
import okhttp3.*;

import java.io.IOException;

import static dev.saseq.oauth2.impl.DiscordAPI.BASE_URI;

@RequiredArgsConstructor
public class DiscordOAuth {

    private static final Gson gson = new GsonBuilder().serializeNulls().enableComplexMapKeySerialization().create();
    private static final String GRANT_TYPE_AUTHORIZATION = "authorization_code";
    private static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
    private final String clientID;
    private final String clientSecret;
    private final String redirectUri;
    private final String[] scope;

    public String getAuthorizationURL() {
        return getAuthorizationURL(null);
    }

    public String getAuthorizationURL(String state) {
        StringBuilder builder = new StringBuilder();

        builder.append(BASE_URI + "/oauth2/authorize");
        builder.append("?response_type=").append("code");
        builder.append("&client_id=").append(clientID);
        builder.append("&redirect_uri=").append(redirectUri);
        if (state != null && !state.isEmpty()) {
            builder.append("&state=").append(state);
        }
        builder.append("&scope=").append(String.join("%20", scope));

        return builder.toString();
    }

    public OAuth2Tokens getTokens(String code) throws IOException {
        RequestBody body = new FormBody.Builder()
                .add("client_id", clientID)
                .add("client_secret", clientSecret)
                .add("grant_type", GRANT_TYPE_AUTHORIZATION)
                .add("code", code)
                .add("redirect_uri", redirectUri)
                .add("scope", String.join(" ", scope))
                .build();

        return fetchOAuth2Tokens(body);
    }

    public OAuth2Tokens refreshTokens(String refreshToken) throws IOException {
        RequestBody body = new FormBody.Builder()
                .add("client_id", clientID)
                .add("client_secret", clientSecret)
                .add("grant_type", GRANT_TYPE_REFRESH_TOKEN)
                .add("refresh_token", refreshToken)
                .build();

        return fetchOAuth2Tokens(body);
    }

    private OAuth2Tokens fetchOAuth2Tokens(RequestBody body) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BASE_URI + "/oauth2/token")
                .addHeader("Accept", "*/*")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String stringResponse = response.body().string();
            return toObject(stringResponse);
        }
    }

    private static OAuth2Tokens toObject(String str) {
        return gson.fromJson(str, OAuth2Tokens.class);
    }
}
