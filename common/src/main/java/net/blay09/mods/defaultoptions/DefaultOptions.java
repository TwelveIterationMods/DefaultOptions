package net.blay09.mods.defaultoptions;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.client.BalmClientRegistrars;
import net.blay09.mods.defaultoptions.api.DefaultOptionsCategory;
import net.blay09.mods.defaultoptions.api.DefaultOptionsHandler;
import net.blay09.mods.defaultoptions.command.DefaultOptionsCommand;
import net.blay09.mods.defaultoptions.config.DefaultOptionsConfig;
import net.blay09.mods.defaultoptions.difficulty.DefaultDifficultyHandler;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DefaultOptions {

    public static final String MOD_ID = "defaultoptions";
    public static final Logger logger = LogManager.getLogger(MOD_ID);

    private static final List<DefaultOptionsHandler> defaultOptionsHandlers = new ArrayList<>();

    public static void initialize(BalmClientRegistrars registrars) {
        DefaultOptionsConfig.initialize();
        Balm.commands().register(DefaultOptionsCommand::register);
        DefaultDifficultyHandler.initialize();
    }

    @Deprecated
    public static void saveDefaultOptions(DefaultOptionsCategory category) throws DefaultOptionsHandlerException {
        saveDefaultOptions(new DefaultOptionsContext(Minecraft.getInstance().gameDirectory), category);
    }

    public static void saveDefaultOptions(DefaultOptionsContext context, DefaultOptionsCategory category) throws DefaultOptionsHandlerException {
        for (DefaultOptionsHandler handler : defaultOptionsHandlers) {
            if (handler.getCategory() == category) {
                handler.saveCurrentOptionsAsDefault(context);
            }
        }
    }

    @Deprecated
    public static File getMinecraftDataDir() {
        return Minecraft.getInstance().gameDirectory;
    }

    public static void addDefaultOptionsHandler(DefaultOptionsHandler handler) {
        defaultOptionsHandlers.add(handler);
    }

    public static List<DefaultOptionsHandler> getDefaultOptionsHandlers() {
        return defaultOptionsHandlers;
    }
}
