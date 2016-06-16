package net.blay09.mods.defaultoptions;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class CommandDefaultOptions extends CommandBase {

    @Override
    public String getCommandName() {
        return "defaultoptions";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/defaultoptions (saveAll|saveKeys|saveOptions|overwriteConfig)";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 1) {
            throw new WrongUsageException(getCommandUsage(sender));
        }
        if(args[0].equals("overwriteConfig")) {
            try {
                if(new File(Minecraft.getMinecraft().mcDataDir, "overwrite-config").createNewFile()) {
                    sender.addChatMessage(new TextComponentString("overwrite-config file created. Settings will be overwritten from default-config on next run."));
                    return;
                }
            } catch (IOException e) {
                DefaultOptions.logger.error(e);
            }
            sender.addChatMessage(new TextComponentString("Failed to create overwrite-config file. Please create it manually."));
            return;
        }
        boolean saveOptions = args[0].equalsIgnoreCase("saveAll") || args[0].equalsIgnoreCase("saveOptions");
        boolean saveKeys = args[0].equalsIgnoreCase("saveAll") || args[0].equalsIgnoreCase("saveKeys");
        boolean saveServers = args[0].equalsIgnoreCase("saveAll") || args[0].equalsIgnoreCase("saveServers");
        if(saveKeys) {
            if (DefaultOptions.instance.saveDefaultMappings()) {
                sender.addChatMessage(new TextComponentString("Successfully saved the key configuration."));
                DefaultOptions.instance.reloadDefaultMappings();
            } else {
                sender.addChatMessage(new TextComponentString("Failed saving the key configuration. See the log for more information."));
            }
        }
        if(saveOptions) {
            if (DefaultOptions.instance.saveDefaultOptions() && DefaultOptions.instance.saveDefaultOptionsOptiFine()) {
                sender.addChatMessage(new TextComponentString("Successfully saved the configuration."));
            } else {
                sender.addChatMessage(new TextComponentString("Failed saving the configuration. See the log for more information."));
            }
        }
        if(saveServers) {
            if (DefaultOptions.instance.saveDefaultServers()) {
                sender.addChatMessage(new TextComponentString("Successfully saved the server list."));
            } else {
                sender.addChatMessage(new TextComponentString("Failed saving the server list. See the log for more information."));
            }
        }
        if(!saveOptions && !saveKeys && !saveServers) {
            throw new WrongUsageException(getCommandUsage(sender));
        }
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        if(args.length < 2) {
            return getListOfStringsMatchingLastWord(args, "saveAll", "saveKeys", "saveServers", "saveOptions", "overwriteConfig");
        }
        return super.getTabCompletionOptions(server, sender, args, pos);
    }

}
