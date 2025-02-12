package dev.saseq.oauth2.entities;

import lombok.Data;
import net.dv8tion.jda.api.Permission;

import java.util.List;
import java.util.Set;

@Data
public class OAuth2Guild {

    private String id;
    private String name;
    private String icon;
    private boolean owner;
    private Long permissions;
    private List<String> features;

    public Set<Permission> getPermissionList() {
        return Permission.getPermissions(permissions);
    }
}
