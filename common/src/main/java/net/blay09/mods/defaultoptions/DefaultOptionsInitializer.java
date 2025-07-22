package net.blay09.mods.defaultoptions;

import net.blay09.mods.balm.api.event.client.ClientStartedEvent;
import net.blay09.mods.defaultoptions.api.*;

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
            if (handler.shouldLoadDefaults() && handler.getLoadStage() == stage) {
                try {
                    handler.loadDefaults();
                    DefaultOptions.logger.info("Loaded default options for {}", handler.getId());
                } catch (DefaultOptionsHandlerException e) {
                    DefaultOptions.logger.error("Failed to load default options for {}", e.getHandlerId(), e);
                }
            }
        }
    }

    public static void postSave() {
        for (DefaultOptionsHandler handler : DefaultOptions.getDefaultOptionsHandlers()) {
            handler.saveAdditional();
        }
    }
}
