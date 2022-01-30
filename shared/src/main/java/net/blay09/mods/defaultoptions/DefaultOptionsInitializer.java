package net.blay09.mods.defaultoptions;

import net.blay09.mods.balm.api.event.client.ClientStartedEvent;
import net.blay09.mods.defaultoptions.api.DefaultOptionsHandler;
import net.blay09.mods.defaultoptions.keys.DefaultKeyMappings;

import java.io.*;

public class DefaultOptionsInitializer {

    public static void loadDefaults() {
        for (DefaultOptionsHandler handler : DefaultOptions.getDefaultOptionsHandlers()) {
            if (handler.shouldLoadDefaults()) {
                try {
                    DefaultOptions.logger.info("Loaded default options for {}", handler.getId());
                    handler.loadDefaults();
                } catch (DefaultOptionsHandlerException e) {
                    DefaultOptions.logger.error("Failed to load default options for {}", e.getHandlerId(), e);
                }
            }
        }
    }

    public static void finishLoading(ClientStartedEvent event) {
        // Once Minecraft has finished loading and there are no default settings yet, populate the default options with the current settings
        for (DefaultOptionsHandler handler : DefaultOptions.getDefaultOptionsHandlers()) {
            if (!handler.hasDefaults()) {
                try {
                    handler.saveCurrentOptionsAsDefault();
                } catch (DefaultOptionsHandlerException e) {
                    DefaultOptions.logger.warn("Failed to create initial default options from current options for {}", e.getHandlerId(), e);
                }
            }
        }

        File defaultKeybindings = new File(DefaultOptions.getDefaultOptionsFolder(), "keybindings.txt");
        if (!defaultKeybindings.exists()) {
            DefaultKeyMappings.saveDefaultMappings();
        }

        // Reload default key mappings from the default options
        DefaultKeyMappings.reloadDefaultMappings();
    }

}
