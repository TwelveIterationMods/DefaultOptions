package net.blay09.mods.defaultoptions;

import net.blay09.mods.defaultoptions.api.DefaultOptionsCategory;
import net.blay09.mods.defaultoptions.api.DefaultOptionsHandler;
import net.blay09.mods.defaultoptions.api.DefaultOptionsLoadStage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ExtraDefaultOptionsHandler implements DefaultOptionsHandler {

    private File getCustomDefaultOptionsFolder(DefaultOptionsContext context) {
        File customDefaultOptionsFolder = new File(context.getDefaultOptionsFolder(), "extra");
        if (!customDefaultOptionsFolder.exists() && !customDefaultOptionsFolder.mkdirs()) {
            throw new IllegalStateException("Could not create default options extra directory.");
        }

        return customDefaultOptionsFolder;
    }

    @Override
    public String getId() {
        return "extra-folder";
    }

    @Override
    public DefaultOptionsCategory getCategory() {
        return DefaultOptionsCategory.OPTIONS;
    }

    @Override
    public DefaultOptionsLoadStage getLoadStage() {
        return DefaultOptionsLoadStage.EARLY_INIT;
    }

    @Override
    public void saveCurrentOptions(DefaultOptionsContext context) {
    }

    @Override
    public void saveCurrentOptionsAsDefault(DefaultOptionsContext context) {
    }

    @Override
    public boolean hasDefaults(DefaultOptionsContext context) {
        File[] files = getCustomDefaultOptionsFolder(context).listFiles();
        return files != null && files.length > 0;
    }

    @Override
    public boolean shouldLoadDefaults(DefaultOptionsContext context) {
        return true;
    }

    @Override
    public void loadDefaults(DefaultOptionsContext context) throws DefaultOptionsHandlerException {
        Path defaultOptionsPath = getCustomDefaultOptionsFolder(context).toPath();
        try (final var files = Files.walk(defaultOptionsPath)) {
            List<Path> paths = files.toList();
            for (Path path : paths) {
                File defaultOptionsFile = path.toFile();
                if (defaultOptionsFile.isFile()) {
                    Path relativeDefaultOptionsPath = defaultOptionsPath.relativize(defaultOptionsFile.toPath());
                    File optionsFile = new File(context.getMinecraftDataDir(), relativeDefaultOptionsPath.toString());
                    if (!optionsFile.exists()) {
                        FileUtils.copyFile(defaultOptionsFile, optionsFile);
                        DefaultOptions.logger.info("Populated {} with default options from {}", optionsFile, defaultOptionsFile);
                    } else {
                        DefaultOptions.logger.debug("Extra defaults skipping {} because it already exists, last modified {}", optionsFile, optionsFile.lastModified());
                    }
                } else {
                    DefaultOptions.logger.debug("Extra defaults skipping {} because it is not a file", defaultOptionsFile);
                }
            }
        } catch (IOException e) {
            throw new DefaultOptionsHandlerException(this, e);
        }
    }
}
