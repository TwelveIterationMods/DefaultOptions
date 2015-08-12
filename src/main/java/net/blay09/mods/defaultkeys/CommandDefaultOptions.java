package net.blay09.mods.defaultkeys;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;

public class CommandDefaultOptions extends CommandBase {

    @Override
    public String getCommandName() {
        return "defaultoptions";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/defaultoptions save";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 1) {
            throw new WrongUsageException(getCommandUsage(sender));
        }
        if(args[0].equals("save")) {
            if(DefaultKeys.instance.saveDefaultOptions()) {
                sender.addChatMessage(new ChatComponentText("Successfully saved the configuration."));
            } else {
                sender.addChatMessage(new ChatComponentText("Failed saving the configuration. See the log for more information."));
            }
        } else {
            throw new WrongUsageException(getCommandUsage(sender));
        }
    }

}
