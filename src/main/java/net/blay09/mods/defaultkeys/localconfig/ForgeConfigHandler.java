package net.blay09.mods.defaultkeys.localconfig;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ForgeConfigHandler {

    private static final Logger logger = LogManager.getLogger();

    public static void backup(PrintWriter writer, Collection<LocalConfigEntry> entries, File configFile) {
        List<LocalConfigEntry> notEntries = new ArrayList<>();
        for(LocalConfigEntry entry : entries) {
            if (entry.not) {
                notEntries.add(entry);
            }
        }
        List<String> categoryPath = new ArrayList<>();
        boolean isInQuotes = false;
        boolean isInList = false;
        boolean consumeList = false;
        boolean listFlag = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            String line;
            lineLoop:while ((line = reader.readLine()) != null) {
                StringBuilder buffer = new StringBuilder();
                charLoop:for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    if(isInQuotes) {
                        if (c == '"') {
                            isInQuotes = false;
                        } else {
                            buffer.append(c);
                        }
                    } else if(isInList) {
                        if(c == '>') {
                            isInList = false;
                            listFlag = false;
                            if(consumeList) {
                                writer.println(">");
                            }
                        } else if(consumeList) {
                            if(listFlag) {
                                writer.print(", ");
                            }
                            writer.print(line);
                            listFlag = true;
                        }
                    } else {
                        String category;
                        String name;
                        String type;
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
                            case '<':
                                isInList = true;
                                consumeList = false;
                                category = StringUtils.join(categoryPath, ".");
                                name = buffer.toString().trim();
                                type = name.substring(0, 1);
                                name = name.substring(2);
                                for(LocalConfigEntry entry : entries) {
                                    if(entry.passesProperty(category, name, type)) {
                                        if(entry.containsWildcard()) {
                                            for(LocalConfigEntry notEntry : notEntries) {
                                                if(entry.passesNotEntry(notEntry)) {
                                                    continue lineLoop;
                                                }
                                            }
                                        }
                                        consumeList = true;
                                        writer.print(entry.file + "/" + category + "/" + type + ":" + name);
                                        writer.print("=<");
                                        continue lineLoop;
                                    }
                                }
                                continue lineLoop;
                            case '=':
                                category = StringUtils.join(categoryPath, ".");
                                name = buffer.toString().trim();
                                type = name.substring(0, 1);
                                name = name.substring(2);
                                String value = line.substring(i + 1);
                                for(LocalConfigEntry entry : entries) {
                                    if(entry.passesProperty(category, name, type)) {
                                        if(entry.containsWildcard()) {
                                            for(LocalConfigEntry notEntry : notEntries) {
                                                if(entry.passesNotEntry(notEntry)) {
                                                    continue lineLoop;
                                                }
                                            }
                                        }
                                        writer.println(entry.file + "/" + category + "/" + type + ":" + name + "=" + value);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void restore(Collection<LocalConfigEntry> entries, File configFile) {
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
                boolean isInList = false;
                boolean discardList = false;
                lineLoop:for (String line : lines) {
                    StringBuilder buffer = new StringBuilder();
                    charLoop:for (int i = 0; i < line.length(); i++) {
                        char c = line.charAt(i);
                        if(isInQuotes) {
                            if (c == '"') {
                                isInQuotes = false;
                            } else {
                                buffer.append(c);
                            }
                        } else if(isInList) {
                            if(c == '>') {
                                isInList = false;
                            }
                            if(discardList) {
                               continue lineLoop;
                            }
                        } else {
                            String category;
                            String name;
                            String type;
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
                                case '<':
                                    isInList = true;
                                    discardList = false;
                                    category = StringUtils.join(categoryPath, ".");
                                    name = buffer.toString().trim();
                                    type = name.substring(0, 1);
                                    name = name.substring(2);
                                    for(LocalConfigEntry entry : entries) {
                                        if(entry.passesProperty(category, name, type)) {
                                            if(entry.containsWildcard()) {
                                                for(LocalConfigEntry notEntry : notEntries) {
                                                    if(entry.passesNotEntry(notEntry)) {
                                                        break charLoop;
                                                    }
                                                }
                                            }
                                            discardList = true;
                                            String indent = StringUtils.repeat(' ', getIndent(line));
                                            writer.print(indent);
                                            writer.println('<');
                                            String[] values = entry.value.split(",");
                                            for (String value : values) {
                                                writer.print(indent);
                                                writer.print("    ");
                                                writer.println(value.trim());
                                            }
                                            writer.print(indent);
                                            writer.println('>');
                                            continue lineLoop;
                                        }
                                    }
                                    break charLoop;
                                case '=':
                                    category = StringUtils.join(categoryPath, ".");
                                    name = buffer.toString().trim();
                                    type = name.substring(0, 1);
                                    name = name.substring(2);
                                    for(LocalConfigEntry entry : entries) {
                                        if(entry.passesProperty(category, name, type)) {
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
        } catch (IOException e) {
            logger.error("Failed to restore local values in {}: {}", configFile, e);
        }
    }

    private static int getIndent(String line) {
        for(int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if(!Character.isWhitespace(c) || c != '\t') {
                return i;
            }
        }
        return line.length();
    }

}
