package net.blay09.mods.defaultoptions;

import net.blay09.mods.balm.api.event.client.ClientStartedEvent;
import net.blay09.mods.defaultoptions.api.*;
import net.minecraft.client.Options;
import net.minecraft.nbt.CompoundTag;

import java.util.HashSet;
import java.util.ServiceLoader;

public class DefaultOptionsInitializer {

    static {
        DefaultOptionsAPI.__internalMethods = new InternalMethodsImpl();

        ServiceLoader<DefaultOptionsPlugin> loader = ServiceLoader.load(DefaultOptionsPlugin.class);
        loader.forEach(DefaultOptionsPlugin::initialize);
    }

    public static void preLoad() {
        loadDefaults(DefaultOptionsLoadStage.PRE_LOAD);
    }

    public static void postLoad(ClientStartedEvent event) {
        loadDefaults(DefaultOptionsLoadStage.POST_LOAD);
    }

    private static void loadDefaults(DefaultOptionsLoadStage stage) {
        for (DefaultOptionsHandler handler : DefaultOptions.getDefaultOptionsHandlers()) {
            if (handler.getLoadStage() == stage) {
                if (handler.shouldLoadDefaults()) {
                    try {
                        handler.loadDefaults();
                        DefaultOptions.logger.info("Loaded default options for {}", handler.getId());
                    } catch (DefaultOptionsHandlerException e) {
                        DefaultOptions.logger.error("Failed to load default options for {}", e.getHandlerId(), e);
                    }
                } else if (handler.hasDefaults()) {
                    DefaultOptions.logger.debug("Skipping default options for {}; defaults are present but should not be loaded", handler.getId());
                } else {
                    DefaultOptions.logger.debug("Skipping default options for {}; no defaults available", handler.getId());
                }
            }
        }
    }

    public static void postSave() {
        for (DefaultOptionsHandler handler : DefaultOptions.getDefaultOptionsHandlers()) {
            handler.saveAdditional();
        }
    }

    public static void detectAndMarkModifiedKeys(Options options, CompoundTag fields) {
        final var knownKeys = new HashSet<String>();
        for (final var option : fields.getAllKeys()) {
            if (option.startsWith("key_")) {
                final var name = option.substring("key_".length());
                knownKeys.add(name);
            }
        }
        for (final var keyMapping : options.keyMappings) {
            if (knownKeys.contains(keyMapping.getName())) {
                ((DefaultOptionsKeyMapping) keyMapping).defaultoptions$setUserModified(true);
            }
        }
    }
}
