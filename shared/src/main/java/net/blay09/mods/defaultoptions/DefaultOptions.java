package net.blay09.mods.defaultoptions;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.client.keymappings.KeyModifier;
import net.blay09.mods.balm.api.event.client.ClientStartedEvent;
import net.blay09.mods.defaultoptions.api.DefaultOptionsAPI;
import net.blay09.mods.defaultoptions.api.DefaultOptionsCategory;
import net.blay09.mods.defaultoptions.api.DefaultOptionsHandler;
import net.blay09.mods.defaultoptions.mixin.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.blay09.mods.defaultoptions.DefaultOptionsInitializer.getDefaultOptionsFolder;
import static net.blay09.mods.defaultoptions.DefaultOptionsInitializer.getMinecraftDataDir;

public class DefaultOptions {

    public static final String MOD_ID = "defaultoptions";
    public static final Logger logger = LogManager.getLogger(MOD_ID);

    private static final Map<String, DefaultBinding> defaultKeys = new HashMap<>();
    private static final List<String> knownKeys = new ArrayList<>();

    public static final List<DefaultOptionsHandler> defaultOptionsHandlers = new ArrayList<>();

    public static void initialize() {
        DefaultOptionsAPI.__internalMethods = new InternalMethodsImpl();

        DefaultOptionsConfig.initialize();
        Balm.getCommands().register(DefaultOptionsCommand::register);
        Balm.getEvents().onEvent(ClientStartedEvent.class, DefaultOptions::finishLoading);
        DefaultDifficultyHandler.initialize();

        DefaultOptionsAPI.registerOptionsFile(new File(getMinecraftDataDir(), "options.txt"))
                .withLinePredicate(line -> !line.startsWith("key_"))
                .withSaveHandler(() -> Minecraft.getInstance().options.save());

        DefaultOptionsAPI.registerOptionsFile(new File(getMinecraftDataDir(), "servers.dat"))
                .withCategory(DefaultOptionsCategory.SERVERS);

        if (Balm.isModLoaded("optifine")) {
            DefaultOptionsAPI.registerOptionsFile(new File(getMinecraftDataDir(), "optionsof.txt"))
                    .withSaveHandler(() -> Minecraft.getInstance().options.save());
        }
    }

    private static void finishLoading(ClientStartedEvent event) {
        // Once Minecraft has finished loading and there are no default settings yet, populate the default options with the current settings
        for (DefaultOptionsHandler handler : defaultOptionsHandlers) {
            if (!handler.hasDefaults()) {
                try {
                    handler.saveCurrentOptionsAsDefault();
                } catch (DefaultOptionsHandlerException e) {
                    logger.warn("Failed to create initial default options from current options for {}", e.getHandlerId(), e);
                }
            }
        }

        File defaultKeybindings = new File(getDefaultOptionsFolder(), "keybindings.txt");
        if (!defaultKeybindings.exists()) {
            saveDefaultMappings();
        }

        // Reload default key mappings from the default options
        reloadDefaultMappings();
    }


    public static void saveDefaultOptions(DefaultOptionsCategory category) throws DefaultOptionsHandlerException {
        for (DefaultOptionsHandler handler : defaultOptionsHandlers) {
            if (handler.getCategory() == category) {
                handler.saveCurrentOptionsAsDefault();
            }
        }
    }

    public static boolean saveDefaultMappings() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(getDefaultOptionsFolder(), "keybindings.txt")))) {
            for (KeyMapping keyMapping : Minecraft.getInstance().options.keyMappings) {
                KeyModifier keyModifier = PlatformBindings.INSTANCE.getKeyModifier(keyMapping);
                writer.println("key_" + keyMapping.getName() + ":" + keyMapping.saveString() + ":" + keyModifier.name());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static final Pattern KEY_PATTERN = Pattern.compile("key_([^:]+):([^:]+)(?::(.+))?");

    public static void reloadDefaultMappings() {
        // Clear old values
        defaultKeys.clear();
        knownKeys.clear();

        // Load the default keys from the config
        File defaultKeysFile = new File(getDefaultOptionsFolder(), "keybindings.txt");
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
                        defaultKeys.put(matcher.group(1), new DefaultBinding(InputConstants.getKey(matcher.group(2)), modifier));
                    } catch (Exception e) {
                        logger.error("Error loading default key binding for {}", line, e);
                    }
                }
            } catch (Exception e) {
                logger.error("Error loading default key bindings", e);
            }
        }

        // Load the known keys from the Minecraft directory
        File knownKeysFile = new File(getMinecraftDataDir(), "knownkeys.txt");
        if (knownKeysFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(knownKeysFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.isEmpty()) {
                        knownKeys.add(line);
                    }
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }

        // Override the default mappings and set the initial key codes, if the key is not known yet
        for (KeyMapping keyMapping : Minecraft.getInstance().options.keyMappings) {
            if (defaultKeys.containsKey(keyMapping.getName())) {
                DefaultBinding defaultBinding = defaultKeys.get(keyMapping.getName());
                ((KeyMappingAccessor) keyMapping).setDefaultKey(defaultBinding.input);
                PlatformBindings.INSTANCE.setDefaultKeyModifier(keyMapping, defaultBinding.modifier);
                if (!knownKeys.contains(keyMapping.getName())) {
                    KeyModifier defaultKeyModifier = PlatformBindings.INSTANCE.getDefaultKeyModifier(keyMapping);
                    PlatformBindings.INSTANCE.setKeyModifier(keyMapping, defaultKeyModifier);
                    keyMapping.setKey(keyMapping.getDefaultKey());
                    knownKeys.add(keyMapping.getName());
                }
            }
        }
        KeyMapping.resetMapping();

        // Save the updated known keys to the knownkeys.txt file in the Minecraft directory
        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(getMinecraftDataDir(), "knownkeys.txt")))) {
            for (String key : knownKeys) {
                writer.println(key);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
