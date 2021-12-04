package net.blay09.mods.defaultoptions;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;

public class DefaultOptionsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("defaultoptions")
                .then(Commands.literal("saveAll").executes(context -> saveDefaultOptions(context, true, true, true)))
                .then(Commands.literal("saveKeys").executes(context -> saveDefaultOptions(context, false, true, false)))
                .then(Commands.literal("saveOptions").executes(context -> saveDefaultOptions(context, true, false, false)))
                .then(Commands.literal("saveServers").executes(context -> saveDefaultOptions(context, false, false, true)))
        );
    }

    private static int saveDefaultOptions(CommandContext<CommandSourceStack> context, boolean saveOptions, boolean saveKeys, boolean saveServers) throws CommandRuntimeException {
        CommandSourceStack source = context.getSource();
        if (saveKeys) {
            if (DefaultOptions.saveDefaultMappings()) {
                source.sendSuccess(new TextComponent("Successfully saved the key configuration."), true);
                DefaultOptions.reloadDefaultMappings();
            } else {
                source.sendFailure(new TextComponent("Failed saving the key configuration. See the log for more information."));
            }
        }

        if (saveOptions) {
            if (DefaultOptions.saveDefaultOptions() && DefaultOptions.saveDefaultOptionsOptiFine()) {
                source.sendSuccess(new TextComponent("Successfully saved the configuration."), true);
            } else {
                source.sendFailure(new TextComponent("Failed saving the configuration. See the log for more information."));
            }
        }

        if (saveServers) {
            if (DefaultOptions.saveDefaultServers()) {
                source.sendSuccess(new TextComponent("Successfully saved the server list."), true);
            } else {
                source.sendFailure(new TextComponent("Failed saving the server list. See the log for more information."));
            }
        }

        return Command.SINGLE_SUCCESS;
    }

}
