package net.blay09.mods.defaultkeys;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;

@Deprecated
public class CommandDefaultKeys extends CommandBase {

    @Override
    public String getCommandName() {
        return "defaultkeys";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/defaultkeys was removed, use /defaultoptions";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        throw new WrongUsageException(getCommandUsage(sender));
    }

}
