package dev.saseq.command.managers;

import dev.saseq.command.impl.ContextMenu;
import dev.saseq.command.impl.SlashCommand;
import dev.saseq.command.parts.Environment;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class CommandManager extends ListenerAdapter {

    private final List<SlashCommand> commands = new ArrayList<>();
    private final List<ContextMenu> contextMenus = new ArrayList<>();
    @Setter
    private String ownerId;
    @Setter
    private String devServerID;
    @Setter
    private Environment environment;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        if (environment == null) {
            throw new IllegalStateException("The environment is not set");
        }

        if (environment.equals(Environment.DEV)) {
            if (devServerID == null) {
                throw new IllegalStateException("The dev server id is not set");
            }
            Guild devGuild = event.getJDA().getGuildById(devServerID);
            if (devGuild == null) {
                log.error("Could not find guild with ID: {}", devServerID);
                return;
            }

            List<CommandData> commandDataList = convertCommandsToCommandDataList();
            devGuild.updateCommands().addCommands(commandDataList).queue();
        } else if (environment.equals(Environment.PROD)) {
            List<CommandData> commandDataList = convertCommandsToCommandDataList();
            event.getJDA().updateCommands().addCommands(commandDataList).queue();
        } else {
            log.error("Unknown environment: {}", environment.name());
        }
    }

    private List<CommandData> convertCommandsToCommandDataList() {
        List<CommandData> commandDataList = commands.stream().map(this::createCommandData).collect(Collectors.toList());
        commandDataList.addAll(contextMenus.stream().map(this::createContextMenuData).collect(Collectors.toList()));
        return commandDataList;
    }

    private CommandData createCommandData(SlashCommand command) {
        SlashCommandData commandData = Commands.slash(command.getName(), command.getDescription());
        if (!command.getOptions().isEmpty()) {
            commandData.addOptions(command.getOptions());
        }
        if(command.isGuildOnly()) {
            commandData.setContexts(InteractionContextType.GUILD);
        }
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

    private CommandData createContextMenuData(ContextMenu contextMenu) {
        CommandData commandData = Commands.context(contextMenu.getType(), contextMenu.getName());
        if (contextMenu.getPermissions().length != 0) {
            commandData.setDefaultPermissions(DefaultMemberPermissions.enabledFor(contextMenu.getPermissions()));
        }
        commandData.setNSFW(contextMenu.isNsfwOnly());

        return commandData;
    }

    public void addCommand(SlashCommand command) {
        commands.add(command);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        for (SlashCommand command : commands) {
            if (command.getName().equals(event.getName())) {
                if (checkIsOwner(command.isOwnerCommand(), event.getUser(), event)) return;

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

    public void addContextMenu(ContextMenu contextMenu) {
        contextMenus.add(contextMenu);
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        for (ContextMenu contextMenu : contextMenus) {
            if (contextMenu.getName().equals(event.getName())) {
                if (checkIsOwner(contextMenu.isOwnerCommand(), event.getUser(), event)) return;
                contextMenu.execute(event);
            }
        }
    }

    private boolean checkIsOwner(boolean ownerCommand, User user, @NotNull GenericCommandInteractionEvent event) {
        if (ownerCommand) {
            if (ownerId == null) {
                throw new IllegalStateException("The owner id is not set");
            }
            if (!user.getId().equals(ownerId)) {
                event.reply("⛔ You are not authorized to use this command!").setEphemeral(true).queue();
                return true;
            }
        }
        return false;
    }
}
