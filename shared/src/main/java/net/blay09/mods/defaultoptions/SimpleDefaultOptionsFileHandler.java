package net.blay09.mods.defaultoptions;

import net.blay09.mods.defaultoptions.api.DefaultOptionsCategory;
import net.blay09.mods.defaultoptions.api.SimpleDefaultOptionsHandler;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.function.Predicate;

public class SimpleDefaultOptionsFileHandler implements SimpleDefaultOptionsHandler {

    private final File file;
    private DefaultOptionsCategory category = DefaultOptionsCategory.OPTIONS;
    private Runnable saveHandler;
    private Predicate<String> linePredicate;

    public SimpleDefaultOptionsFileHandler(File file) {
        this.file = file;
    }

    @Override
    public String getId() {
        return file.getName();
    }

    public File getFile() {
        return file;
    }

    public File getDefaultsFile() {
        return new File(DefaultOptions.getDefaultOptionsFolder(), file.getName());
    }

    @Override
    public DefaultOptionsCategory getCategory() {
        return category;
    }

    @Override
    public boolean hasDefaults() {
        return getDefaultsFile().exists();
    }

    @Override
    public void saveCurrentOptions() {
        if (saveHandler != null) {
            saveHandler.run();
        }
    }

    @Override
    public void saveCurrentOptionsAsDefault() throws DefaultOptionsHandlerException {
        saveCurrentOptions();

        try {
            if (linePredicate != null) {
                copyFileLineByLine(file, getDefaultsFile(), linePredicate);
            } else {
                FileUtils.copyFile(file, getDefaultsFile());
            }
        } catch (IOException e) {
            throw new DefaultOptionsHandlerException(this, e);
        }
    }

    @Override
    public boolean shouldLoadDefaults() {
        return !file.exists() && hasDefaults();
    }

    @Override
    public void loadDefaults() throws DefaultOptionsHandlerException {
        try {
            if (linePredicate != null) {
                copyFileLineByLine(getDefaultsFile(), file, linePredicate);
            } else {
                FileUtils.copyFile(getDefaultsFile(), file);
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
