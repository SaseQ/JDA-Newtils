package dev.saseq.oauth2.entities;

import com.google.gson.annotations.SerializedName;
import dev.saseq.oauth2.parts.PremiumType;
import lombok.Data;
import net.dv8tion.jda.api.entities.User;

import java.util.Set;

@Data
public class OAuth2User {

    private String id;
    private String username;
    private String avatar;
    private String discriminator;
    @SerializedName("global_name")
    private String globalName;
    private Boolean bot;
    private Boolean system;
    @SerializedName("mfa_enabled")
    private Boolean mfaEnabled;
    private String banner;
    @SerializedName("accent_color")
    private String accentColor;
    private String locale;
    private Boolean verified;
    private String email;
    private Integer flags;
    @SerializedName("premium_type")
    private Integer premiumType;
    @SerializedName("public_flags")
    private Integer publicFlags;
    @SerializedName("avatar_decoration")
    private String avatarDecoration;

    public String getFullUsername() {
        if (discriminator == null || discriminator.isEmpty() || discriminator.equals("0")) {
            return username;
        }
        return username + "#" + discriminator;
    }

    public Set<User.UserFlag> getFlagList() {
        return User.UserFlag.getFlags(flags);
    }

    public Set<User.UserFlag> getPublicFlagList() {
        return User.UserFlag.getFlags(publicFlags);
    }

    public PremiumType getPremiumType() {
        return PremiumType.fromValue(premiumType);
    }
}
