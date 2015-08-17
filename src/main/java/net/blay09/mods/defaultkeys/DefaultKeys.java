package net.blay09.mods.defaultkeys;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod(modid = DefaultKeys.MODID)
public class DefaultKeys {

    public static final String MODID = "defaultkeys";

    @Mod.Instance
    public static DefaultKeys instance;

    private static boolean initialized;
    private static Map<String, Integer> defaultKeys = new HashMap<String, Integer>();
    private static List<String> knownKeys = new ArrayList<String>();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new CommandDefaultKeys());
        ClientCommandHandler.instance.registerCommand(new CommandDefaultOptions());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void finishMinecraftLoading(GuiScreenEvent.InitGuiEvent event) {
        if(initialized) {
            return;
        }
        if(event.gui instanceof GuiMainMenu) {
            reloadDefaultMappings();
            File optionsFile = new File(Minecraft.getMinecraft().mcDataDir, "options.txt");
            if(!optionsFile.exists()) {
                applyDefaultOptions();
            }
            initialized = true;
        }
    }

    public boolean saveDefaultOptions() {
        try {
            Minecraft.getMinecraft().gameSettings.saveOptions();
            PrintWriter writer = new PrintWriter(new FileWriter(new File(Minecraft.getMinecraft().mcDataDir, "config/defaultoptions.txt")));
            BufferedReader reader = new BufferedReader(new FileReader(new File(Minecraft.getMinecraft().mcDataDir, "options.txt")));
            String line;
            while((line = reader.readLine()) != null) {
                if(line.startsWith("key_")) {
                    continue;
                }
                writer.println(line);
            }
            writer.close();
            reader.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean applyDefaultOptions() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(Minecraft.getMinecraft().mcDataDir, "config/defaultoptions.txt")));
            PrintWriter writer = new PrintWriter(new FileWriter(new File(Minecraft.getMinecraft().mcDataDir, "options.txt")));
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.startsWith("key_")) {
                    continue;
                }
                writer.println(line);
            }
            writer.close();
            reader.close();
            Minecraft.getMinecraft().gameSettings.loadOptions();
            return true;
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveDefaultMappings() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(new File(Minecraft.getMinecraft().mcDataDir, "config/defaultkeys.txt")));
            for (KeyBinding keyBinding : Minecraft.getMinecraft().gameSettings.keyBindings) {
                writer.println("key_" + keyBinding.getKeyDescription() + ":" + keyBinding.getKeyCode());
            }
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void reloadDefaultMappings() {
        // Clear old values
        defaultKeys.clear();
        knownKeys.clear();

        // Load the default keys from the config
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(Minecraft.getMinecraft().mcDataDir, "config/defaultkeys.txt")));
            String line;
            while((line = reader.readLine()) != null) {
                if(line.isEmpty()) {
                    continue;
                }
                String[] s = line.split(":");
                if(s.length != 2 || !s[0].startsWith("key_")) {
                    continue;
                }
                try {
                    defaultKeys.put(s[0].substring(4), Integer.parseInt(s[1]));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            reader.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        // Load the known keys from the Minecraft directory
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(Minecraft.getMinecraft().mcDataDir, "knownkeys.txt")));
            String line;
            while((line = reader.readLine()) != null) {
                if(!line.isEmpty()) {
                    knownKeys.add(line);
                }
            }
            reader.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        // Override the default mappings and set the initial key codes, if the key is not known yet
        for(KeyBinding keyBinding : Minecraft.getMinecraft().gameSettings.keyBindings) {
            if(defaultKeys.containsKey(keyBinding.getKeyDescription())) {
                keyBinding.keyCodeDefault = defaultKeys.get(keyBinding.getKeyDescription());
                if(!knownKeys.contains(keyBinding.getKeyDescription())) {
                    keyBinding.setKeyCode(keyBinding.getKeyCodeDefault());
                    knownKeys.add(keyBinding.getKeyDescription());
                }
            }
        }

        // Save the updated known keys to the knownkeys.txt file in the Minecraft directory
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(new File(Minecraft.getMinecraft().mcDataDir, "knownkeys.txt")));
            for(String s : knownKeys) {
                writer.println(s);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
