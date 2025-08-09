package net.blay09.mods.defaultoptions;

import net.blay09.mods.defaultoptions.api.DefaultOptionsCategory;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;

import java.io.File;
import java.io.IOException;

public class DefaultServersHandler extends SimpleDefaultOptionsFileHandler {

    public DefaultServersHandler(File file) {
        super(file);
        withCategory(DefaultOptionsCategory.SERVERS);
    }

    @Override
    public boolean shouldLoadDefaults() {
        final var hasServers = file.exists() && !isServerListEmpty(file);
        return !hasServers && hasDefaults();
    }

    private boolean isServerListEmpty(File file) {
        try {
            final var compoundTag = NbtIo.read(file);
            if (compoundTag == null) {
                return true;
            }

            final var serverList = compoundTag.getList("servers", Tag.TAG_COMPOUND);
            return serverList.isEmpty();
        } catch (IOException ignored) {
            return true;
        }
    }

}
