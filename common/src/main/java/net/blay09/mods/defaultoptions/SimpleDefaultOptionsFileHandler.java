package net.blay09.mods.defaultoptions;

import net.blay09.mods.defaultoptions.api.DefaultOptionsCategory;
import net.blay09.mods.defaultoptions.api.DefaultOptionsLoadStage;
import net.blay09.mods.defaultoptions.api.SimpleDefaultOptionsHandler;
import org.apache.commons.io.FileUtils;
import org.jspecify.annotations.Nullable;

import java.io.*;
import java.util.function.Predicate;

public class SimpleDefaultOptionsFileHandler implements SimpleDefaultOptionsHandler {

    private final File file;
    private DefaultOptionsCategory category = DefaultOptionsCategory.OPTIONS;
    private DefaultOptionsLoadStage loadStage = DefaultOptionsLoadStage.PRE_LOAD;
    private @Nullable Runnable saveHandler;
    private @Nullable Predicate<String> linePredicate;

    public SimpleDefaultOptionsFileHandler(File file) {
        this.file = file;
    }

    @Override
    public String getId() {
        return file.getName();
    }

    public File getFile(DefaultOptionsContext context) {
        if (!file.isAbsolute()) {
            return new File(context.getMinecraftDataDir(), file.getPath());
        }

        return file;
    }

    public File getDefaultsFile(DefaultOptionsContext context) {
        return new File(context.getDefaultOptionsFolder(), file.getName());
    }

    @Override
    public DefaultOptionsCategory getCategory() {
        return category;
    }

    @Override
    public DefaultOptionsLoadStage getLoadStage() {
        return loadStage;
    }

    @Override
    public boolean hasDefaults(DefaultOptionsContext context) {
        return getDefaultsFile(context).exists();
    }

    @Override
    public void saveCurrentOptions(DefaultOptionsContext context) {
        if (saveHandler != null) {
            saveHandler.run();
        }
    }

    @Override
    public void saveCurrentOptionsAsDefault(DefaultOptionsContext context) throws DefaultOptionsHandlerException {
        saveCurrentOptions(context);

        File file = getFile(context);
        if (file.exists()) {
            try {
                if (linePredicate != null) {
                    copyFileLineByLine(file, getDefaultsFile(context), linePredicate);
                } else {
                    FileUtils.copyFile(file, getDefaultsFile(context));
                }
            } catch (IOException e) {
                throw new DefaultOptionsHandlerException(this, e);
            }
        }
    }

    @Override
    public boolean shouldLoadDefaults(DefaultOptionsContext context) {
        return !getFile(context).exists() && hasDefaults(context);
    }

    @Override
    public void loadDefaults(DefaultOptionsContext context) throws DefaultOptionsHandlerException {
        try {
            if (linePredicate != null) {
                copyFileLineByLine(getDefaultsFile(context), getFile(context), linePredicate);
            } else {
                FileUtils.copyFile(getDefaultsFile(context), getFile(context));
            }
        } catch (IOException e) {
            throw new DefaultOptionsHandlerException(this, e);
        }
    }

    @Override
    public SimpleDefaultOptionsHandler withSaveHandler(Runnable saveHandler) {
        this.saveHandler = saveHandler;
        return this;
    }

    @Override
    public SimpleDefaultOptionsHandler withLinePredicate(Predicate<String> linePredicate) {
        this.linePredicate = linePredicate;
        return this;
    }

    @Override
    public SimpleDefaultOptionsHandler withCategory(DefaultOptionsCategory category) {
        this.category = category;
        return this;
    }

    @Override
    public SimpleDefaultOptionsHandler withLoadStage(DefaultOptionsLoadStage loadStage) {
        this.loadStage = loadStage;
        return this;
    }

    private static void copyFileLineByLine(File source, File target, Predicate<String> linePredicate) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(target));
             BufferedReader reader = new BufferedReader(new FileReader(source))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (linePredicate.test(line)) {
                    writer.println(line);
                }
            }
        }
    }
}
