package net.blay09.mods.defaultoptions.resources;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.defaultoptions.DefaultOptions;
import net.blay09.mods.defaultoptions.DefaultOptionsHandlerException;
import net.blay09.mods.defaultoptions.api.DefaultOptionsCategory;
import net.blay09.mods.defaultoptions.api.DefaultOptionsHandler;
import net.blay09.mods.defaultoptions.api.DefaultOptionsLoadStage;
import net.blay09.mods.defaultoptions.config.DefaultOptionsConfig;
import net.blay09.mods.defaultoptions.journal.DefaultOptionsJournal;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.PackRepository;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;

public class DefaultResourcePacksHandler implements DefaultOptionsHandler {

    private static final String ID = "resource-packs";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public DefaultOptionsCategory getCategory() {
        return DefaultOptionsCategory.RESOURCE_PACKS;
    }

    @Override
    public DefaultOptionsLoadStage getLoadStage() {
        return DefaultOptionsLoadStage.POST_LOAD;
    }

    @Override
    public void saveCurrentOptions() {
    }

    @Override
    public void saveCurrentOptionsAsDefault() {
        final var selectedPacks = List.copyOf(Minecraft.getInstance().options.resourcePacks);
        Balm.config().updateLocalConfig(DefaultOptionsConfig.class, config -> config.defaultResourcePacks = selectedPacks);
    }

    @Override
    public boolean hasDefaults() {
        return !DefaultOptionsConfig.getActive().defaultResourcePacks.isEmpty();
    }

    @Override
    public boolean shouldLoadDefaults() {
        return hasDefaults() && !DefaultOptionsJournal.load().contains(ID);
    }

    @Override
    public void loadDefaults() throws DefaultOptionsHandlerException {
        final var minecraft = Minecraft.getInstance();
        final var repository = minecraft.getResourcePackRepository();
        final var selectedPacks = resolveConfiguredPacks(repository, DefaultOptionsConfig.getActive().defaultResourcePacks);

        repository.setSelected(selectedPacks);
        minecraft.options.updateResourcePacks(repository);

        try {
            DefaultOptionsJournal.load().markApplied(ID);
        } catch (IOException e) {
            throw new DefaultOptionsHandlerException(this, "Failed to record resource pack defaults in the journal", e);
        }
    }

    private static List<String> resolveConfiguredPacks(PackRepository repository, List<String> configuredPacks) {
        final var resolvedPacks = new LinkedHashSet<String>();
        for (final var configuredPack : configuredPacks) {
            if (configuredPack == null || configuredPack.isBlank()) {
                continue;
            }

            if (repository.isAvailable(configuredPack)) {
                resolvedPacks.add(configuredPack);
            } else {
                final var filePackId = "file/" + configuredPack;
                if (!configuredPack.startsWith("file/") && repository.isAvailable(filePackId)) {
                    resolvedPacks.add(filePackId);
                } else {
                    DefaultOptions.logger.warn("Could not apply default resource pack '{}' because it is not available", configuredPack);
                }
            }
        }
        return List.copyOf(resolvedPacks);
    }
}
