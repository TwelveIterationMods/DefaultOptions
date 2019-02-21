package net.blay09.mods.defaultoptions;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentString;

import java.io.File;
import java.io.IOException;

public class CommandDefaultOptions {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("defaultoptions")
                .then(Commands.literal("saveAll").executes(context -> saveDefaultOptions(context, true, true, true)))
                .then(Commands.literal("saveKeys").executes(context -> saveDefaultOptions(context, false, true, false)))
                .then(Commands.literal("saveOptions").executes(context -> saveDefaultOptions(context, true, false, false)))
                .then(Commands.literal("saveServers").executes(context -> saveDefaultOptions(context, false, false, true)))
                .then(Commands.literal("overwriteConfig").executes(context -> {
                    try {
                        if (new File(DefaultOptions.getMinecraftDataDir(), "overwrite-config").createNewFile()) {
                            context.getSource().sendFeedback(new TextComponentString("overwrite-config file created. Settings will be overwritten from default-config on next run."), true);
                            return Command.SINGLE_SUCCESS;
                        }
                    } catch (IOException e) {
                        DefaultOptions.logger.error(e);
                    }
                    context.getSource().sendFeedback(new TextComponentString("Failed to create overwrite-config file. Please create it manually."), true);
                    return 0;
                }))
        );
    }

    private static int saveDefaultOptions(CommandContext<CommandSource> context, boolean saveOptions, boolean saveKeys, boolean saveServers) throws CommandException {
        CommandSource source = context.getSource();
        if (saveKeys) {
            if (DefaultOptions.saveDefaultMappings()) {
                source.sendFeedback(new TextComponentString("Successfully saved the key configuration."), true);
                DefaultOptions.reloadDefaultMappings();
            } else {
                source.sendFeedback(new TextComponentString("Failed saving the key configuration. See the log for more information."), true);
            }
        }

        if (saveOptions) {
            if (DefaultOptions.saveDefaultOptions() && DefaultOptions.saveDefaultOptionsOptifine()) {
                source.sendFeedback(new TextComponentString("Successfully saved the configuration."), true);
            } else {
                source.sendFeedback(new TextComponentString("Failed saving the configuration. See the log for more information."), true);
            }
        }

        if (saveServers) {
            if (DefaultOptions.saveDefaultServers()) {
                source.sendFeedback(new TextComponentString("Successfully saved the server list."), true);
            } else {
                source.sendFeedback(new TextComponentString("Failed saving the server list. See the log for more information."), true);
            }
        }

        return Command.SINGLE_SUCCESS;
    }

}
