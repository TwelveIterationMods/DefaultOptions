package net.blay09.mods.defaultoptions;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

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
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 1) {
            throw new WrongUsageException(getCommandUsage(sender));
        }
        if(args[0].equals("createUpdateFile")) {
            sender.addChatMessage(new TextComponentString("modpack-update file has been created inside the config directory."));
            sender.addChatMessage(new TextComponentString("You should always ship that file in your modpack; just leave it there forever."));
            sender.addChatMessage(new TextComponentString("This will ensure that local player configs defined in localconfig.txt will be restored on updates."));
            return;
        }
        boolean saveOptions = args[0].equals("saveAll") || args[0].equals("saveOptions");
        boolean saveKeys = args[0].equals("saveAll") || args[0].equals("saveKeys");
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
        if(!saveOptions && !saveKeys) {
            throw new WrongUsageException(getCommandUsage(sender));
        }
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        if(args.length < 2) {
            return getListOfStringsMatchingLastWord(args, "saveAll", "saveKeys", "saveOptions", "createUpdateFile");
        }
        return super.getTabCompletionOptions(server, sender, args, pos);
    }

}
