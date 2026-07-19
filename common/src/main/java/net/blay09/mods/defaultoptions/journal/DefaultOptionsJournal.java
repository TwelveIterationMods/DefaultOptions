package net.blay09.mods.defaultoptions.journal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.blay09.mods.defaultoptions.DefaultOptions;
import net.blay09.mods.defaultoptions.DefaultOptionsContext;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class DefaultOptionsJournal {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String JOURNAL_FILE_NAME = "defaultoptions.journal.json";
    private static final String JOURNAL_PROPERTY = "journal";
    private static final String ID_PROPERTY = "id";

    private final Path path;
    private final JsonObject root;

    private DefaultOptionsJournal(Path path, JsonObject root) {
        this.path = path;
        this.root = root;
    }

    public static DefaultOptionsJournal load(DefaultOptionsContext context) {
        final var path = context.getMinecraftDataDir().toPath().resolve(JOURNAL_FILE_NAME);
        if (!Files.exists(path)) {
            return new DefaultOptionsJournal(path, new JsonObject());
        }

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            final var element = JsonParser.parseReader(reader);
            if (element.isJsonObject()) {
                return new DefaultOptionsJournal(path, element.getAsJsonObject());
            }
            DefaultOptions.logger.warn("Ignoring {} because its root is not a JSON object", path);
        } catch (Exception e) {
            DefaultOptions.logger.warn("Failed to read {}; treating all handlers as unapplied", path, e);
        }

        return new DefaultOptionsJournal(path, new JsonObject());
    }

    public boolean contains(String handlerId) {
        final var journal = getJournal(false);
        if (journal == null) {
            return false;
        }

        for (JsonElement element : journal) {
            if (element.isJsonObject()) {
                final var id = element.getAsJsonObject().get(ID_PROPERTY);
                if (id != null && id.isJsonPrimitive() && handlerId.equals(id.getAsString())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void markApplied(String handlerId) throws IOException {
        if (contains(handlerId)) {
            return;
        }

        final var entry = new JsonObject();
        entry.addProperty(ID_PROPERTY, handlerId);
        getJournal(true).add(entry);
        save();
    }

    private JsonArray getJournal(boolean create) {
        final var element = root.get(JOURNAL_PROPERTY);
        if (element != null && element.isJsonArray()) {
            return element.getAsJsonArray();
        }
        if (!create) {
            return null;
        }

        final var journal = new JsonArray();
        root.add(JOURNAL_PROPERTY, journal);
        return journal;
    }

    private void save() throws IOException {
        final var temporaryPath = path.resolveSibling(path.getFileName() + ".tmp");
        try (Writer writer = Files.newBufferedWriter(temporaryPath, StandardCharsets.UTF_8)) {
            GSON.toJson(root, writer);
        }

        try {
            Files.move(temporaryPath, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryPath, path, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
