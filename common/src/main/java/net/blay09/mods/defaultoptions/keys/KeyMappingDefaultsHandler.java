package net.blay09.mods.defaultoptions.keys;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.api.client.keymappings.KeyModifier;
import net.blay09.mods.defaultoptions.DefaultOptions;
import net.blay09.mods.defaultoptions.PlatformBindings;
import net.blay09.mods.defaultoptions.api.DefaultOptionsCategory;
import net.blay09.mods.defaultoptions.api.DefaultOptionsHandler;
import net.blay09.mods.defaultoptions.api.DefaultOptionsLoadStage;
import net.blay09.mods.defaultoptions.mixin.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyMappingDefaultsHandler implements DefaultOptionsHandler {

    private static final Pattern KEY_PATTERN = Pattern.compile("key_([^:]+):([^:]+)(?::(.+))?");
    private static final Map<String, DefaultKeyMapping> defaultKeys = new HashMap<>();
    private static final List<String> knownKeys = new ArrayList<>();

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
                KeyModifier keyModifier = PlatformBindings.INSTANCE.getKeyModifier(keyMapping);
                writer.println("key_" + keyMapping.getName() + ":" + keyMapping.saveString() + ":" + keyModifier.name());
            }
        } catch (IOException e) {
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
        // Clear old values
        defaultKeys.clear();
        knownKeys.clear();

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
                        continue;
                    }

                    try {
                        KeyModifier modifier = matcher.group(3) != null ? KeyModifier.valueOf(matcher.group(3)) : KeyModifier.NONE;
                        defaultKeys.put(matcher.group(1), new DefaultKeyMapping(InputConstants.getKey(matcher.group(2)), modifier));
                    } catch (Exception e) {
                        DefaultOptions.logger.error("Error loading default key binding for {}", line, e);
                    }
                }
            } catch (Exception e) {
                DefaultOptions.logger.error("Error loading default key bindings", e);
            }
        }

        // Load the known keys from the Minecraft directory
        File knownKeysFile = new File(DefaultOptions.getMinecraftDataDir(), "knownkeys.txt");
        if (knownKeysFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(knownKeysFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.isEmpty()) {
                        knownKeys.add(line);
                    }
                }
            } catch (IOException e) {
                DefaultOptions.logger.error("Error loading known key bindings", e);
            }
        }

        // Override the default mappings and set the initial key codes, if the key is not known yet
        for (KeyMapping keyMapping : Minecraft.getInstance().options.keyMappings) {
            if (defaultKeys.containsKey(keyMapping.getName())) {
                DefaultKeyMapping defaultKeyMapping = defaultKeys.get(keyMapping.getName());
                ((KeyMappingAccessor) keyMapping).setDefaultKey(defaultKeyMapping.input);
                PlatformBindings.INSTANCE.setDefaultKeyModifier(keyMapping, defaultKeyMapping.modifier);
                if (!knownKeys.contains(keyMapping.getName())) {
                    KeyModifier defaultKeyModifier = PlatformBindings.INSTANCE.getDefaultKeyModifier(keyMapping);
                    PlatformBindings.INSTANCE.setKeyModifier(keyMapping, defaultKeyModifier);
                    keyMapping.setKey(keyMapping.getDefaultKey());
                    knownKeys.add(keyMapping.getName());
                }
            }
        }
        KeyMapping.resetMapping();
        saveCurrentOptions();

        // Save the updated known keys to the knownkeys.txt file in the Minecraft directory
        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(DefaultOptions.getMinecraftDataDir(), "knownkeys.txt")))) {
            for (String key : knownKeys) {
                writer.println(key);
            }
        } catch (IOException e) {
            DefaultOptions.logger.error("Error saving known key bindings", e);
        }
    }
}
