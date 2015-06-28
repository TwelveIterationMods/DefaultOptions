package net.blay09.mods.defaultkeys;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CommandDefaultKeys extends CommandBase {

    @Override
    public String getCommandName() {
        return "defaultkeys";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/defaultkeys save";
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
            try {
                PrintWriter writer = new PrintWriter(new FileWriter(new File(Minecraft.getMinecraft().mcDataDir, "config/defaultkeys.txt")));
                for (KeyBinding keyBinding : Minecraft.getMinecraft().gameSettings.keyBindings) {
                    writer.println("key_" + keyBinding.getKeyDescription() + ":" + keyBinding.getKeyCode());
                }
                writer.close();
                sender.addChatMessage(new ChatComponentText("Successfully saved the configuration."));
            } catch (IOException e) {
                sender.addChatMessage(new ChatComponentText("Failed saving the configuration: " + e.getMessage()));
                e.printStackTrace();
            }
        } else {
            throw new WrongUsageException(getCommandUsage(sender));
        }
    }

}
