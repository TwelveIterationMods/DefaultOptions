package net.blay09.mods.defaultoptions;

import net.blay09.mods.defaultoptions.api.DefaultOptionsAPI;
import net.blay09.mods.defaultoptions.api.DefaultOptionsCategory;
import net.blay09.mods.defaultoptions.api.DefaultOptionsHandler;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class ExtraDefaultOptionsHandler implements DefaultOptionsHandler {

    private File getCustomDefaultOptionsFolder() {
        File customDefaultOptionsFolder = new File(DefaultOptionsAPI.getDefaultOptionsFolder(), "extra");
        if (!customDefaultOptionsFolder.exists() && !customDefaultOptionsFolder.mkdirs()) {
            throw new IllegalStateException("Could not create default options extra directory.");
        }

        return customDefaultOptionsFolder;
    }

    @Override
    public String getId() {
        return "extra/*";
    }

    @Override
    public DefaultOptionsCategory getCategory() {
        return DefaultOptionsCategory.OPTIONS;
    }

    @Override
    public void saveCurrentOptions() {
    }

    @Override
    public void saveCurrentOptionsAsDefault() {
    }

    @Override
    public boolean hasDefaults() {
        File[] files = getCustomDefaultOptionsFolder().listFiles();
        return files != null && files.length > 0;
    }

    @Override
    public boolean shouldLoadDefaults() {
        return true;
    }

    @Override
    public void loadDefaults() throws DefaultOptionsHandlerException {
        File[] files = getCustomDefaultOptionsFolder().listFiles();
        if (files != null) {
            for (File defaultOptionsFile : files) {
                File optionsFile = new File(DefaultOptions.getMinecraftDataDir(), defaultOptionsFile.getName());
                if (!optionsFile.exists()) {
                    try {
                        FileUtils.copyFile(defaultOptionsFile, optionsFile);
                    } catch (IOException e) {
                        throw new DefaultOptionsHandlerException(this, e);
                    }
                }
            }
        }
    }
}
