package net.blay09.mods.defaultoptions;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import net.blay09.mods.defaultoptions.api.DefaultOptionsAPI;
import net.blay09.mods.defaultoptions.api.DefaultOptionsHandler;
import net.blay09.mods.defaultoptions.api.DefaultOptionsLoadStage;
import net.blay09.mods.defaultoptions.api.DefaultOptionsPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

public class DefaultOptionsInitializer {

    private static final Set<String> userSeenKeys = new HashSet<>();
    private static boolean earlyLoaded;

    static {
        DefaultOptionsAPI.__internalMethods = new InternalMethodsImpl();

        ServiceLoader<DefaultOptionsPlugin> loader = ServiceLoader.load(DefaultOptionsPlugin.class);
        loader.forEach(DefaultOptionsPlugin::initialize);
    }

    public static void earlyLoad(DefaultOptionsContext context) {
        if (!earlyLoaded) {
            earlyLoaded = true;
            loadDefaults(DefaultOptionsLoadStage.EARLY_LOAD, context);
        }
    }

    public static void preLoad(Options options) {
        if (options.getFile().exists()) {
            DefaultOptions.logger.info("options.txt already exists - last modified {}", options.getFile().lastModified());
        }
        DefaultOptionsInitializer.collectSeenKeys(options);
        loadDefaults(DefaultOptionsLoadStage.PRE_LOAD, new DefaultOptionsContext(Minecraft.getInstance().gameDirectory));
    }

    public static void postLoad() {
        loadDefaults(DefaultOptionsLoadStage.POST_LOAD, new DefaultOptionsContext(Minecraft.getInstance().gameDirectory));
    }

    private static void loadDefaults(DefaultOptionsLoadStage stage, DefaultOptionsContext context) {
        for (DefaultOptionsHandler handler : DefaultOptions.getDefaultOptionsHandlers()) {
            if (handler.getLoadStage() == stage) {
                if (handler.shouldLoadDefaults(context)) {
                    try {
                        handler.loadDefaults(context);
                        DefaultOptions.logger.info("Loaded default options for {}", handler.getId());
                    } catch (DefaultOptionsHandlerException e) {
                        DefaultOptions.logger.error("Failed to load default options for {}", e.getHandlerId(), e);
                    }
                } else if (handler.hasDefaults(context)) {
                    DefaultOptions.logger.debug("Skipping default options for {}; defaults are present but should not be loaded", handler.getId());
                } else {
                    DefaultOptions.logger.debug("Skipping default options for {}; no defaults available", handler.getId());
                }
            }
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
                            DefaultOptions.logger.debug("Key {} is already configured in options.txt", name);
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
