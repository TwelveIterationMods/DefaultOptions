package net.blay09.mods.defaultoptions.keys;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.api.client.keymappings.KeyModifier;
import net.blay09.mods.defaultoptions.DefaultOptions;
import net.blay09.mods.defaultoptions.DefaultOptionsInitializer;
import net.blay09.mods.defaultoptions.PlatformBindings;
import net.blay09.mods.defaultoptions.api.DefaultOptionsCategory;
import net.blay09.mods.defaultoptions.api.DefaultOptionsHandler;
import net.blay09.mods.defaultoptions.api.DefaultOptionsLoadStage;
import net.blay09.mods.defaultoptions.DefaultOptionsKeyMapping;
import net.blay09.mods.defaultoptions.mixin.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class KeyMappingDefaultsHandler implements DefaultOptionsHandler {

    private static final Pattern KEY_PATTERN = Pattern.compile("key_([^:]+):([^:]+)(?::(.+)?)?");
    private static final Map<String, DefaultKeyMapping> defaultKeys = new HashMap<>();

    private File getDefaultOptionsFile() {
        return new File(DefaultOptions.getDefaultOptionsFolder(), "keybindings.txt");
    }

    @Override
    public String getId() {
        return "keymappings";
    }

    @Override
    public DefaultOptionsCategory getCategory() {
        return DefaultOptionsCategory.KEYS;
    }

    @Override
    public DefaultOptionsLoadStage getLoadStage() {
        return DefaultOptionsLoadStage.POST_LOAD;
    }

    @Override
    public void saveCurrentOptions() {
        Minecraft.getInstance().options.save();
    }

    @Override
    public void saveCurrentOptionsAsDefault() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(DefaultOptions.getDefaultOptionsFolder(), "keybindings.txt")))) {
            for (KeyMapping keyMapping : Minecraft.getInstance().options.keyMappings) {
                final var keyModifiers = PlatformBindings.INSTANCE.getKeyModifiers(keyMapping);
                writer.println("key_" + keyMapping.getName() + ":" + keyMapping.saveString() + ":" + keyModifiers.stream()
                        .map(KeyModifier::name)
                        .collect(Collectors.joining(",")));
            }
        } catch (Exception e) {
            DefaultOptions.logger.error("Failed to save default key mappings", e);
        }

        loadDefaults();
    }

    @Override
    public boolean hasDefaults() {
        return getDefaultOptionsFile().exists();
    }

    @Override
    public boolean shouldLoadDefaults() {
        return true;
    }

    @Override
    public void loadDefaults() {
        DefaultOptionsInitializer.markUserSeenKeys(Minecraft.getInstance().options);

        // Clear old values
        defaultKeys.clear();

        // Load the default keys from the config
        File defaultKeysFile = new File(DefaultOptions.getDefaultOptionsFolder(), "keybindings.txt");
        if (defaultKeysFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(defaultKeysFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isEmpty()) {
                        continue;
                    }

                    Matcher matcher = KEY_PATTERN.matcher(line);
                    if (!matcher.matches()) {
                        DefaultOptions.logger.debug("Skipping line {} as the format is invalid", line);
                        continue;
                    }

                    try {
                        final var keyModifierNames = matcher.group(3) != null ? matcher.group(3).split(",") : new String[0];
                        final var modifiers = Arrays.stream(keyModifierNames).map(KeyModifier::valueOf).collect(Collectors.toSet());
                        final var keyMappingName = matcher.group(1);
                        defaultKeys.put(keyMappingName, new DefaultKeyMapping(InputConstants.getKey(matcher.group(2)), modifiers));
                        DefaultOptions.logger.debug("Registered a default key binding for {} ({}:{})", keyMappingName, matcher.group(2), matcher.group(3));
                    } catch (Exception e) {
                        DefaultOptions.logger.error("Error loading default key binding for {}", line, e);
                    }
                }
            } catch (Exception e) {
                DefaultOptions.logger.error("Error loading default key bindings", e);
            }
            DefaultOptions.logger.info("Loaded {} default key bindings.", defaultKeys.size());
        } else {
            DefaultOptions.logger.info("No default key bindings file found.");
        }

        // Override the default mappings and set the initial key codes, if the key is not known yet
        int defaultsApplied = 0;
        int bindingsOverridden = 0;
        for (KeyMapping keyMapping : Minecraft.getInstance().options.keyMappings) {
            final var originalDefaultMapping = new DefaultKeyMapping(keyMapping.getDefaultKey(), PlatformBindings.INSTANCE.getDefaultKeyModifiers(keyMapping));
            if (defaultKeys.containsKey(keyMapping.getName())) {
                DefaultKeyMapping defaultKeyMapping = defaultKeys.get(keyMapping.getName());
                ((KeyMappingAccessor) keyMapping).setDefaultKey(defaultKeyMapping.input());
                PlatformBindings.INSTANCE.setDefaultKeyModifiers(keyMapping, defaultKeyMapping.modifiers());
                defaultsApplied++;
                // If the key is still on the original default and has not yet been modified on this run (i.e. through options load),
                // we update it to the new default. Essentially options.txt now acts as what was previously knownkeys.txt.
                // That way we don't override changes the player themselves may have made already.
                if (!((DefaultOptionsKeyMapping) keyMapping).defaultoptions$wasSeen()
                        && originalDefaultMapping.matches(keyMapping)
                        && !defaultKeyMapping.matches(keyMapping)) {
                    final var defaultKeyModifiers = PlatformBindings.INSTANCE.getDefaultKeyModifiers(keyMapping);
                    PlatformBindings.INSTANCE.setKeyModifiers(keyMapping, defaultKeyModifiers);
                    keyMapping.setKey(keyMapping.getDefaultKey());
                    bindingsOverridden++;
                    DefaultOptions.logger.debug("Key mapping {} was previously on the original default. Configuring to new default.", keyMapping.getName());
                } else {
                    DefaultOptions.logger.debug("Key mapping {} has been previously set, skipping.", keyMapping.getName());
                }
            } else {
                DefaultOptions.logger.debug("No default key mapping configured for {}, skipping.", keyMapping.getName());
            }
        }
        DefaultOptions.logger.info("Applied {} defaults to key mappings ({} keys were reconfigured).", defaultsApplied, bindingsOverridden);
        if (bindingsOverridden > 0) {
            KeyMapping.resetMapping();
            saveCurrentOptions();
        }
    }
}
