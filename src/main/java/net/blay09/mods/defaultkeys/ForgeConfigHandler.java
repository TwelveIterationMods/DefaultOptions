package net.blay09.mods.defaultkeys;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ForgeConfigHandler {

    private static final Logger logger = LogManager.getLogger();

    public static void restore(Collection<LocalConfigEntry> entries, File configFile) {
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
                                        if(entry.path.equals(category) && entry.name.equals(name) && entry.type.endsWith("<>") && entry.type.charAt(0) == type.charAt(0)) {
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
                                        if(entry.path.equals(category) && entry.name.equals(name) && !entry.type.endsWith("<>") && entry.type.charAt(0) == type.charAt(0)) {
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
