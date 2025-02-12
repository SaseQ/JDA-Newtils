package dev.saseq.oauth2.entities;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class OAuth2Tokens {

    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("token_type")
    private String tokenType;
    @SerializedName("expires_in")
    private int expiresIn;
    @SerializedName("refresh_token")
    private String refreshToken;
    private String scope;
}
