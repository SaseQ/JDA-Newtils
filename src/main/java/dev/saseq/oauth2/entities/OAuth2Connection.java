package dev.saseq.oauth2.entities;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class OAuth2Connection {

    private String id;
    private String name;
    private String type;
    private boolean verified;
    @SerializedName("friend_sync")
    private boolean friendSync;
    @SerializedName("show_activity")
    private boolean showActivity;
    private int visibility;
}
