package net.blay09.mods.defaultkeys;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;

import java.util.List;

public class CommandDefaultOptions extends CommandBase {

    @Override
    public String getCommandName() {
        return "defaultoptions";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/defaultoptions (saveAll|saveKeys|saveOptions|createUpdateFile)";
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
        if(args[0].equals("createUpdateFile")) {
            sender.addChatMessage(new ChatComponentText("modpack-update file has been created inside the config directory."));
            sender.addChatMessage(new ChatComponentText("You should always ship that file in your modpack; just leave it there forever."));
            sender.addChatMessage(new ChatComponentText("This will ensure that local player configs defined in localconfig.txt will be restored on updates."));
            return;
        }
        boolean saveOptions = args[0].equals("saveAll") || args[0].equals("saveOptions");
        boolean saveKeys = args[0].equals("saveAll") || args[0].equals("saveKeys");
        if(saveKeys) {
            if (DefaultKeys.instance.saveDefaultMappings()) {
                sender.addChatMessage(new ChatComponentText("Successfully saved the key configuration."));
                DefaultKeys.instance.reloadDefaultMappings();
            } else {
                sender.addChatMessage(new ChatComponentText("Failed saving the key configuration. See the log for more information."));
            }
        }
        if(saveOptions) {
            if (DefaultKeys.instance.saveDefaultOptions() && DefaultKeys.instance.saveDefaultOptionsOptiFine()) {
                sender.addChatMessage(new ChatComponentText("Successfully saved the configuration."));
            } else {
                sender.addChatMessage(new ChatComponentText("Failed saving the configuration. See the log for more information."));
            }
        }
        if(!saveOptions && !saveKeys) {
            throw new WrongUsageException(getCommandUsage(sender));
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if(args.length < 2) {
            return getListOfStringsMatchingLastWord(args, "saveAll", "saveKeys", "saveOptions", "createUpdateFile");
        }
        return super.addTabCompletionOptions(sender, args);
    }
}
