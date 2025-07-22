package net.blay09.mods.defaultoptions;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import net.blay09.mods.balm.api.event.client.ClientStartedEvent;
import net.blay09.mods.defaultoptions.api.*;
import net.minecraft.client.Options;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

public class DefaultOptionsInitializer {

    private static final Set<String> userSeenKeys = new HashSet<>();

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

    public static void collectSeenKeys(Options options) {
        try (final var reader = Files.newReader(options.getFile(), Charsets.UTF_8)) {
            reader.lines().forEach((line) -> {
                try {
                    if (line.startsWith("key_")) {
                        int colonIndex = line.indexOf(':');
                        if (colonIndex != -1) {
                            final var key = line.substring(0, colonIndex);
                            final var name = key.substring("key_".length());
                            userSeenKeys.add(name);
                        }
                    }
                } catch (Exception ignored) {
                }
            });
        } catch (Exception ignored) {
        }
    }

    public static void markUserSeenKeys(Options options) {
        for (final var keyMapping : options.keyMappings) {
            if (userSeenKeys.contains(keyMapping.getName())) {
                ((DefaultOptionsKeyMapping) keyMapping).defaultoptions$setSeen(true);
            }
        }
    }
}
