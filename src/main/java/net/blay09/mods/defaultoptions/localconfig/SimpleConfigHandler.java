package net.blay09.mods.defaultoptions.localconfig;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimpleConfigHandler {

    private static final Logger logger = LogManager.getLogger();

    public static void backup(PrintWriter writer, List<LocalConfigEntry> entries, File configFile) {
        boolean[] foundProperty = new boolean[entries.size()];
        List<LocalConfigEntry> notEntries = new ArrayList<>();
        for(LocalConfigEntry entry : entries) {
            if (entry.not) {
                notEntries.add(entry);
            }
        }
        List<String> categoryPath = new ArrayList<>();
        boolean isInQuotes = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            String line;
            lineLoop:while ((line = reader.readLine()) != null) {
                StringBuilder buffer = new StringBuilder();
                charLoop:for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    if(isInQuotes) {
                        if (c == '"') {
                            isInQuotes = false;
                        }
                        buffer.append(c);
                    } else {
                        String category;
                        String name;
                        switch (c) {
                            case '#':
                                break charLoop;
                            case '"':
                                isInQuotes = true;
                                break;
                            case '{':
                                categoryPath.add(buffer.toString().trim());
                                buffer = new StringBuilder();
                                break;
                            case '}':
                                categoryPath.remove(categoryPath.size() - 1);
                                break;
                            case '=':
                                category = StringUtils.join(categoryPath, ".");
                                name = buffer.toString().trim();
                                String value = line.substring(i + 1);
                                for(int j = 0; j < entries.size(); j++) {
                                    LocalConfigEntry entry = entries.get(j);
                                    if(entry.passesProperty(category, name, "*")) {
                                        foundProperty[j] = true;
                                        if(entry.containsWildcard()) {
                                            for(LocalConfigEntry notEntry : notEntries) {
                                                if(entry.passesNotEntry(notEntry)) {
                                                    continue lineLoop;
                                                }
                                            }
                                        }
                                        writer.println(entry.file + "/" + category + "/" + name + "=" + value);
                                        break;
                                    }
                                }
                                continue lineLoop;
                            default:
                                buffer.append(c);
                        }
                    }
                }
            }
            for(int i = 0; i < foundProperty.length; i++) {
                if(!foundProperty[i] && !entries.get(i).not) {
                    logger.warn("Failed to backup value {}: property not found", entries.get(i).getIdentifier());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void restore(List<LocalConfigEntry> entries, File configFile) {
        boolean[] foundProperty = new boolean[entries.size()];
        List<LocalConfigEntry> notEntries = new ArrayList<>();
        for(LocalConfigEntry entry : entries) {
            if (entry.not) {
                notEntries.add(entry);
            }
        }
        try {
            List<String> lines = FileUtils.readLines(configFile);
            try(PrintWriter writer = new PrintWriter(configFile)) {
                List<String> categoryPath = new ArrayList<>();
                boolean isInQuotes = false;
                for (String line : lines) {
                    StringBuilder buffer = new StringBuilder();
                    charLoop:for (int i = 0; i < line.length(); i++) {
                        char c = line.charAt(i);
                        if(isInQuotes) {
                            if (c == '"') {
                                isInQuotes = false;
                            }
                            buffer.append(c);
                        } else {
                            String category;
                            String name;
                            switch (c) {
                                case '#':
                                    break charLoop;
                                case '"':
                                    isInQuotes = true;
                                    break;
                                case '{':
                                    categoryPath.add(buffer.toString().trim());
                                    buffer = new StringBuilder();
                                    break;
                                case '}':
                                    categoryPath.remove(categoryPath.size() - 1);
                                    break;
                                case '=':
                                    category = StringUtils.join(categoryPath, ".");
                                    name = buffer.toString().trim();
                                    for(int j = 0; j < entries.size(); j++) {
                                        LocalConfigEntry entry = entries.get(j);
                                        if(entry.passesProperty(category, name, "*")) {
                                            foundProperty[j] = true;
                                            if(entry.containsWildcard()) {
                                                for(LocalConfigEntry notEntry : notEntries) {
                                                    if(entry.passesNotEntry(notEntry)) {
                                                        break charLoop;
                                                    }
                                                }
                                            }
                                            line = line.substring(0, i) + "=" + entry.value;
                                            break;
                                        }
                                    }
                                    break charLoop;
                                default:
                                    buffer.append(c);
                            }
                        }
                    }
                    writer.println(line);
                }
            }
            for(int i = 0; i < foundProperty.length; i++) {
                if(!foundProperty[i] && !entries.get(i).not) {
                    logger.warn("Failed to backup value {}: property not found", entries.get(i).getIdentifier());
                }
            }
        } catch (IOException e) {
            logger.error("Failed to restore local values in {}: {}", configFile, e);
        }
    }

}
