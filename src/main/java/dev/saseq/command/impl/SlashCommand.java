package dev.saseq.command.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public abstract class SlashCommand {

    protected String name = "null";
    protected String description = "no help available";
    protected SlashCommand[] children = new SlashCommand[0];
    protected SubcommandGroupData subcommandGroup = null;
    protected List<OptionData> options = new ArrayList<>();
    protected boolean guildOnly = true;
    protected boolean ownerCommand = false;
    protected Permission[] permissions = new Permission[0];
    protected boolean nsfwOnly = false;

    public abstract void execute(@NonNull SlashCommandInteractionEvent event);

    public void onAutoComplete(@NonNull CommandAutoCompleteInteractionEvent event) {}
}
