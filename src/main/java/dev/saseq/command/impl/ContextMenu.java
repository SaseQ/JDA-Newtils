package dev.saseq.command.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;

@NoArgsConstructor
@Getter
public abstract class ContextMenu {

    protected String name = "null";
    protected Command.Type type = Command.Type.UNKNOWN;
    protected boolean ownerCommand = false;
    protected Permission[] permissions = new Permission[0];
    protected boolean nsfwOnly = false;

    public abstract void execute(@NonNull MessageContextInteractionEvent event);
}
