package net.blay09.mods.defaultoptions;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mod(DefaultOptions.MOD_ID)
public class DefaultOptions {

    public static final String MOD_ID = "defaultoptions";
    public static final Logger logger = LogManager.getLogger(MOD_ID);

    private static Map<String, DefaultBinding> defaultKeys = Maps.newHashMap();
    private static List<String> knownKeys = Lists.newArrayList();

    public DefaultOptions() {
        MinecraftForge.EVENT_BUS.register(this);

        applyDefaults();

        Minecraft mc = Minecraft.getInstance();
        GameSettings gameSettings = mc.gameSettings;
        gameSettings.loadOptions();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::finishLoading);

        MinecraftForge.EVENT_BUS.addListener(this::setupServer);
    }

    private void setupServer(FMLServerStartingEvent event) {
        CommandDefaultOptions.register(event.getCommandDispatcher());
    }

    public static void applyDefaults() {
        File mcDataDir = getMinecraftDataDir();
        File optionsFile = new File(mcDataDir, "options.txt");
        boolean firstRun = !optionsFile.exists();
        if (firstRun) {
            applyDefaultOptions();
        }
        File optionsFileOF = new File(mcDataDir, "optionsof.txt");
        if (!optionsFileOF.exists()) {
            applyDefaultOptionsOptifine();
        }
        File serversDatFile = new File(mcDataDir, "servers.dat");
        if (!serversDatFile.exists()) {
            applyDefaultServers();
        }
        File overwriteConfig = new File(mcDataDir, "overwrite-config");
        if (firstRun || overwriteConfig.exists()) {
            applyDefaultConfig();
            if (overwriteConfig.exists() && !overwriteConfig.delete()) {
                logger.warn("Could not delete overwrite-config file. Configs will be overwritten from defaults upon next run unless you delete the file manually.");
            }
        }
    }

    private void finishLoading(FMLLoadCompleteEvent event) {
        File defaultOptions = new File(getDefaultOptionsFolder(), "options.txt");
        if (!defaultOptions.exists()) {
            saveDefaultOptions();
        }

        File defaultOptionsOF = new File(getDefaultOptionsFolder(), "optionsof.txt");
        if (!defaultOptionsOF.exists()) {
            saveDefaultOptionsOptifine();
        }

        File defaultKeybindings = new File(getDefaultOptionsFolder(), "keybindings.txt");
        if (!defaultKeybindings.exists()) {
            saveDefaultMappings();
        }

        reloadDefaultMappings();
    }

    private static void applyDefaultServers() {
        try {
            FileUtils.copyFile(new File(getDefaultOptionsFolder(), "servers.dat"), new File(getMinecraftDataDir(), "servers.dat"));
        } catch (IOException e) {
            logger.error(e);
        }
    }

    public static void applyDefaultConfig() {
        try {
            FileUtils.copyDirectory(getDefaultOptionsFolder(), new File(getMinecraftDataDir(), "config"), file -> !file.getName().equals("options.txt")
                    && !file.getName().equals("optionsof.txt")
                    && !file.getName().equals("keybindings.txt")
                    && !file.getName().equals("defaultoptions")
                    && !file.getName().equals("servers.dat"));
        } catch (IOException e) {
            logger.error(e);
        }
    }

    private static boolean applyDefaultOptions() {
        File defaultOptionsFile = new File(getDefaultOptionsFolder(), "options.txt");
        if (defaultOptionsFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(defaultOptionsFile));
                 PrintWriter writer = new PrintWriter(new FileWriter(new File(getMinecraftDataDir(), "options.txt")))) {
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
        }
        return true;
    }

    private static void applyDefaultOptionsOptifine() {
        File defaultOptionsFile = new File(getDefaultOptionsFolder(), "optionsof.txt");
        if (defaultOptionsFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(defaultOptionsFile));
                 PrintWriter writer = new PrintWriter(new FileWriter(new File(getMinecraftDataDir(), "optionsof.txt")))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean saveDefaultOptionsOptifine() {
        if (!ModList.get().isLoaded("optifine")) {
            return true;
        }

        Minecraft.getInstance().gameSettings.saveOptions();
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
        Minecraft.getInstance().gameSettings.saveOptions();
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
            for (KeyBinding keyBinding : Minecraft.getInstance().gameSettings.keyBindings) {
                writer.println("key_" + keyBinding.getKeyDescription() + ":" + keyBinding.getTranslationKey() + ":" + keyBinding.getKeyModifier().name());
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
                        KeyModifier modifier = matcher.group(3) != null ? KeyModifier.valueFromString(matcher.group(3)) : KeyModifier.NONE;
                        defaultKeys.put(matcher.group(1), new DefaultBinding(InputMappings.getInputByName(matcher.group(2)), modifier));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
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
        for (KeyBinding keyBinding : Minecraft.getInstance().gameSettings.keyBindings) {
            if (defaultKeys.containsKey(keyBinding.getKeyDescription())) {
                DefaultBinding defaultBinding = defaultKeys.get(keyBinding.getKeyDescription());
                keyBinding.keyCodeDefault = defaultBinding.input;
                ObfuscationReflectionHelper.setPrivateValue(KeyBinding.class, keyBinding, defaultBinding.modifier, "keyModifierDefault");
                if (!knownKeys.contains(keyBinding.getKeyDescription())) {
                    keyBinding.setKeyModifierAndCode(keyBinding.getKeyModifierDefault(), keyBinding.getDefault());
                    knownKeys.add(keyBinding.getKeyDescription());
                }
            }
        }
        KeyBinding.resetKeyBindingArrayAndHash();

        // Save the updated known keys to the knownkeys.txt file in the Minecraft directory
        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(getMinecraftDataDir(), "knownkeys.txt")))) {
            for (String key : knownKeys) {
                writer.println(key);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getDefaultOptionsFolder() {
        File defaultOptions = new File(getMinecraftDataDir(), "config/defaultoptions");
        if (!defaultOptions.exists() && !defaultOptions.mkdirs()) {
            throw new IllegalStateException("Could not create default options directory.");
        }

        return defaultOptions;
    }

    public static File getMinecraftDataDir() {
        return Minecraft.getInstance().gameDir;
    }
}
