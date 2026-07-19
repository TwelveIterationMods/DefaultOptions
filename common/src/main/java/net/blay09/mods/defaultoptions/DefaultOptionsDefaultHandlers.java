package net.blay09.mods.defaultoptions;

import net.blay09.mods.defaultoptions.api.DefaultOptionsAPI;
import net.blay09.mods.defaultoptions.api.DefaultOptionsCategory;
import net.blay09.mods.defaultoptions.api.DefaultOptionsPlugin;
import net.blay09.mods.defaultoptions.keys.KeyMappingDefaultsHandler;
import net.blay09.mods.defaultoptions.resources.DefaultResourcePacksHandler;
import net.minecraft.client.Minecraft;

import java.io.File;

public class DefaultOptionsDefaultHandlers implements DefaultOptionsPlugin {

    @Override
    public void initialize() {
        DefaultOptionsAPI.registerOptionsFile(new File("options.txt"))
                .withLinePredicate(line -> !line.startsWith("key_"))
                .withSaveHandler(() -> Minecraft.getInstance().options.save());

        DefaultOptionsAPI.registerOptionsFile(new File("servers.dat"))
                .withCategory(DefaultOptionsCategory.SERVERS);

        DefaultOptionsAPI.registerOptionsFile(new File("optionsof.txt"))
                .withSaveHandler(() -> Minecraft.getInstance().options.save());

        DefaultOptionsAPI.registerOptionsFile(new File("optionsviveprofiles.txt"));

        DefaultOptionsAPI.registerOptionsHandler(new KeyMappingDefaultsHandler());
        DefaultOptionsAPI.registerOptionsHandler(new ExtraDefaultOptionsHandler());
        DefaultOptionsAPI.registerOptionsHandler(new DefaultResourcePacksHandler());
    }
}
