package net.blay09.mods.defaultoptions.api;

import net.blay09.mods.defaultoptions.DefaultOptionsContext;
import net.blay09.mods.defaultoptions.DefaultOptionsHandlerException;
import net.minecraft.client.Minecraft;

public interface DefaultOptionsHandler {
    String getId();

    DefaultOptionsCategory getCategory();

    DefaultOptionsLoadStage getLoadStage();

    @Deprecated
    default void saveCurrentOptions() {
        saveCurrentOptions(new DefaultOptionsContext(Minecraft.getInstance().gameDirectory));
    }

    default void saveCurrentOptions(DefaultOptionsContext context) {
        saveCurrentOptions();
    }

    @Deprecated
    default void saveCurrentOptionsAsDefault() throws DefaultOptionsHandlerException {
        saveCurrentOptionsAsDefault(new DefaultOptionsContext(Minecraft.getInstance().gameDirectory));
    }

    default void saveCurrentOptionsAsDefault(DefaultOptionsContext context) throws DefaultOptionsHandlerException {
        saveCurrentOptionsAsDefault();
    }

    @Deprecated
    default boolean hasDefaults() {
        return hasDefaults(new DefaultOptionsContext(Minecraft.getInstance().gameDirectory));
    }

    default boolean hasDefaults(DefaultOptionsContext context) {
        return hasDefaults();
    }

    @Deprecated
    default boolean shouldLoadDefaults() {
        return shouldLoadDefaults(new DefaultOptionsContext(Minecraft.getInstance().gameDirectory));
    }

    default boolean shouldLoadDefaults(DefaultOptionsContext context) {
        return shouldLoadDefaults();
    }

    @Deprecated
    default void loadDefaults() throws DefaultOptionsHandlerException {
        loadDefaults(new DefaultOptionsContext(Minecraft.getInstance().gameDirectory));
    }

    default void loadDefaults(DefaultOptionsContext context) throws DefaultOptionsHandlerException {
        loadDefaults();
    }
}
