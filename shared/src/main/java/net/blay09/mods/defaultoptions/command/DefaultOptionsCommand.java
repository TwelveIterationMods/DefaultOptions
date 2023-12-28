package net.blay09.mods.defaultoptions.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.blay09.mods.defaultoptions.DefaultOptions;
import net.blay09.mods.defaultoptions.DefaultOptionsHandlerException;
import net.blay09.mods.defaultoptions.api.DefaultOptionsCategory;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class DefaultOptionsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("defaultoptions")
                .then(Commands.literal("saveAll").executes(context -> saveDefaultOptions(context, null)))
                .then(Commands.literal("saveKeys").executes(context -> saveDefaultOptions(context, DefaultOptionsCategory.KEYS)))
                .then(Commands.literal("saveOptions").executes(context -> saveDefaultOptions(context, DefaultOptionsCategory.OPTIONS)))
                .then(Commands.literal("saveServers").executes(context -> saveDefaultOptions(context, DefaultOptionsCategory.SERVERS)))
        );
    }

    private static int saveDefaultOptions(CommandContext<CommandSourceStack> context, DefaultOptionsCategory categoryFilter) {
        CommandSourceStack source = context.getSource();
        if (categoryFilter == null || categoryFilter == DefaultOptionsCategory.KEYS) {
            try {
                DefaultOptions.saveDefaultOptions(DefaultOptionsCategory.KEYS);
                source.sendSuccess(() -> Component.literal("Successfully saved the key configuration."), true);
            } catch(DefaultOptionsHandlerException e) {
                DefaultOptions.logger.error("Failed to save default options for {}", e.getHandlerId(), e);
                source.sendFailure(Component.literal("Failed saving the key configuration. See the log for more information."));
            }
        }

        if (categoryFilter == null || categoryFilter == DefaultOptionsCategory.OPTIONS) {
            try {
                DefaultOptions.saveDefaultOptions(DefaultOptionsCategory.OPTIONS);
                source.sendSuccess(() -> Component.literal("Successfully saved the configuration."), true);
            } catch(DefaultOptionsHandlerException e) {
                DefaultOptions.logger.error("Failed to save default options for {}", e.getHandlerId(), e);
                source.sendFailure(Component.literal("Failed saving the configuration. See the log for more information."));
            }
        }

        if (categoryFilter == null || categoryFilter == DefaultOptionsCategory.SERVERS) {
            try {
                DefaultOptions.saveDefaultOptions(DefaultOptionsCategory.SERVERS);
                source.sendSuccess(() -> Component.literal("Successfully saved the server list."), true);
            } catch(DefaultOptionsHandlerException e) {
                DefaultOptions.logger.error("Failed to save default options for {}", e.getHandlerId(), e);
                source.sendFailure(Component.literal("Failed saving the server list. See the log for more information."));
            }
        }

        return Command.SINGLE_SUCCESS;
    }

}
