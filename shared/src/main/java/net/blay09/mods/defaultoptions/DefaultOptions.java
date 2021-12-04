package net.blay09.mods.defaultoptions;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.client.keymappings.KeyModifier;
import net.blay09.mods.balm.api.event.client.ClientStartedEvent;
import net.blay09.mods.defaultoptions.mixin.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.blay09.mods.defaultoptions.DefaultOptionsInitializer.getDefaultOptionsFolder;
import static net.blay09.mods.defaultoptions.DefaultOptionsInitializer.getMinecraftDataDir;

public class DefaultOptions {

    public static final String MOD_ID = "defaultoptions";
    public static final Logger logger = LogManager.getLogger(MOD_ID);

    private static final Map<String, DefaultBinding> defaultKeys = Maps.newHashMap();
    private static final List<String> knownKeys = Lists.newArrayList();

    public static void initializeCommon() {
        DefaultOptionsConfig.initialize();
        Balm.getCommands().register(DefaultOptionsCommand::register);
        Balm.getEvents().onEvent(ClientStartedEvent.class, DefaultOptions::finishLoading);
    }

    public static void initializeClient() {
        DefaultDifficultyHandler.initialize();
    }

    private static void finishLoading(ClientStartedEvent event) {
        File defaultOptions = new File(getDefaultOptionsFolder(), "options.txt");
        if (!defaultOptions.exists()) {
            saveDefaultOptions();
        }

        File defaultOptionsOF = new File(getDefaultOptionsFolder(), "optionsof.txt");
        if (!defaultOptionsOF.exists()) {
            saveDefaultOptionsOptiFine();
        }

        File defaultKeybindings = new File(getDefaultOptionsFolder(), "keybindings.txt");
        if (!defaultKeybindings.exists()) {
            saveDefaultMappings();
        }

        reloadDefaultMappings();
    }

    public static boolean saveDefaultOptionsOptiFine() {
        if (!Balm.isModLoaded("optifine")) {
            return true;
        }

        Minecraft.getInstance().options.save();

        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(getDefaultOptionsFolder(), "optionsof.txt")));
             BufferedReader reader = new BufferedReader(new FileReader(new File(getMinecraftDataDir(), "optionsof.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean saveDefaultOptions() {
        Minecraft.getInstance().options.save();

        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(getDefaultOptionsFolder(), "options.txt")));
             BufferedReader reader = new BufferedReader(new FileReader(new File(getMinecraftDataDir(), "options.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("key_")) {
                    continue;
                }
                writer.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean saveDefaultServers() {
        File serversDat = new File(getMinecraftDataDir(), "servers.dat");
        if (serversDat.exists()) {
            try {
                FileUtils.copyFile(serversDat, new File(getDefaultOptionsFolder(), "servers.dat"));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return true;
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
