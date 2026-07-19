package net.blay09.mods.defaultoptions;

import java.io.File;

public class DefaultOptionsContext {

    private final File minecraftDataDir;

    public DefaultOptionsContext(File minecraftDataDir) {
        this.minecraftDataDir = minecraftDataDir;
    }

    public File getMinecraftDataDir() {
        return minecraftDataDir;
    }

    public File getDefaultOptionsFolder() {
        File defaultOptions = new File(minecraftDataDir, "config/defaultoptions");
        if (!defaultOptions.exists() && !defaultOptions.mkdirs()) {
            throw new IllegalStateException("Could not create default options directory.");
        }

        return defaultOptions;
    }

    public File getDefaultOptionsFile(String file) {
        return new File(getDefaultOptionsFolder(), file);
    }
}
