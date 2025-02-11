package dev.saseq.command.managers;

import dev.saseq.command.impl.SlashCommand;
import dev.saseq.command.parts.Environment;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class CommandManager extends ListenerAdapter {

    private final List<SlashCommand> commands = new ArrayList<>();
    @Setter
    private String ownerId;
    @Setter
    private String devServerID;
    @Setter
    private Environment environment;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        if (environment == null) {
            log.error("The environment is not set!");
            return;
        }

        if (environment.equals(Environment.DEV)) {
            if (devServerID == null) {
                log.error("The dev server id is not set!");
                return;
            }
            Guild devGuild = event.getJDA().getGuildById(devServerID);
            if (devGuild == null) {
                log.error("Could not find guild with ID: {}", devServerID);
                return;
            }

            List<CommandDataImpl> commandDataList = commands.stream().map(this::createCommandData).collect(Collectors.toList());
            devGuild.updateCommands().addCommands(commandDataList).queue();
        } else if (environment.equals(Environment.PROD)) {
            List<CommandDataImpl> commandDataList = commands.stream().map(this::createCommandData).collect(Collectors.toList());
            event.getJDA().updateCommands().addCommands(commandDataList).queue();
        } else {
            log.error("Unknown environment: {}", environment.name());
        }
    }

    private CommandDataImpl createCommandData(SlashCommand command) {
        CommandDataImpl commandData = new CommandDataImpl(command.getName(), command.getDescription());
        if (!command.getOptions().isEmpty()) {
            commandData.addOptions(command.getOptions());
        }
        commandData.setGuildOnly(command.isGuildOnly());
        if (command.getPermissions().length != 0) {
            commandData.setDefaultPermissions(DefaultMemberPermissions.enabledFor(command.getPermissions()));
        }
        commandData.setNSFW(command.isNsfwOnly());

        if (command.getChildren().length != 0) {
            Map<String, SubcommandGroupData> groupData = new HashMap<>();

            for (SlashCommand child : command.getChildren()) {
                SubcommandData subcommandData = new SubcommandData(child.getName(), child.getDescription());

                if (!child.getOptions().isEmpty()) {
                    subcommandData.addOptions(child.getOptions());
                }

                if (child.getSubcommandGroup() != null) {
                    SubcommandGroupData group = child.getSubcommandGroup();
                    SubcommandGroupData newData = (groupData.getOrDefault(group.getName(), group)).addSubcommands(subcommandData);
                    groupData.put(group.getName(), newData);
                } else {
                    commandData.addSubcommands(subcommandData);
                }
            }

            if (!groupData.isEmpty()) {
                commandData.addSubcommandGroups(groupData.values());
            }
        }


        return commandData;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        for (SlashCommand command : commands) {
            if (command.getName().equals(event.getName())) {
                if (command.isOwnerCommand()) {
                    if (ownerId == null) {
                        log.error("The owner id is not set!");
                        return;
                    }
                    if (!event.getUser().getId().equals(ownerId)) {
                        event.reply("⛔ You are not authorized to use this command!").setEphemeral(true).queue();
                        return;
                    }
                }

                String subcommandGroup = event.getSubcommandGroup();
                String subcommand = event.getSubcommandName();

                if (subcommandGroup != null) {
                    for (SlashCommand groupCommand : command.getChildren()) {
                        if (groupCommand.getSubcommandGroup() == null) {
                            event.reply("❌ Unknown subcommand group `" + subcommandGroup + "`").setEphemeral(true).queue();
                        }
                        if (groupCommand.getSubcommandGroup().getName().equals(subcommandGroup) && groupCommand.getName().equals(subcommand)) {
                            groupCommand.execute(event);
                            return;
                        }
                    }
                    event.reply("❌ Unknown subcommand `" + subcommand + "` in group `" + subcommandGroup + "`").setEphemeral(true).queue();
                } else if (subcommand != null) {
                    for (SlashCommand sub : command.getChildren()) {
                        if (sub.getName().equals(subcommand)) {
                            sub.execute(event);
                            return;
                        }
                    }
                    event.reply("❌ Unknown subcommand `" + subcommand + "`").setEphemeral(true).queue();
                } else {
                    command.execute(event);
                }
                return;
            }
        }
    }

    public void addCommand(SlashCommand command) {
        commands.add(command);
    }
}
