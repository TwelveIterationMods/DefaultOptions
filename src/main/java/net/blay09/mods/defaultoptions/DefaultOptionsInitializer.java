package net.blay09.mods.defaultoptions;

import net.minecraft.client.Minecraft;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class DefaultOptionsInitializer {

    public static final Logger logger = LogManager.getLogger(DefaultOptions.MOD_ID);

    public static File getDefaultOptionsFolder() {
        File defaultOptions = new File(getMinecraftDataDir(), "config/defaultoptions");
        if (!defaultOptions.exists() && !defaultOptions.mkdirs()) {
            throw new IllegalStateException("Could not create default options directory.");
        }

        return defaultOptions;
    }

    public static File getMinecraftDataDir() {
        return Minecraft.getInstance().gameDir;
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
    }

    private static void applyDefaultServers() {
        try {
            FileUtils.copyFile(new File(getDefaultOptionsFolder(), "servers.dat"), new File(getMinecraftDataDir(), "servers.dat"));
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

}
