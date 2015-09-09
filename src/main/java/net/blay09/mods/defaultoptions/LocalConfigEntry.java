package net.blay09.mods.defaultoptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LocalConfigEntry {

    private static Logger logger = LogManager.getLogger();

    public final boolean not;
    public final String file;
    public final String type;
    public final String category;
    public final String name;
    public final String value;
    public final Map<String, String> parameters;

    public LocalConfigEntry(String file, String type, String category, String name, String value, boolean not, Map<String, String> parameters) {
        this.file = file;
        this.type = type;
        this.category = category;
        this.name = name;
        this.value = value;
        this.not = not;
        this.parameters = parameters;
    }

    public static LocalConfigEntry fromString(String line, boolean withValue) throws IOException {
        if(line.trim().startsWith("#") || line.trim().isEmpty()) {
            return null;
        }
        boolean not = line.trim().startsWith("!");
        String fileName = null;
        String path = null;
        String type = null;
        String name = null;
        String value = null;
        Map<String, String> parameters = new HashMap<>();
        StringBuilder buffer = new StringBuilder();
        boolean isQuoted = false;
        for(int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if(c == ']') {
                isQuoted = false;
                continue;
            } else if(c == '[' && !isQuoted) {
                isQuoted = true;
                continue;
            } else if(c == '/' && !isQuoted) {
                if(fileName == null) {
                    fileName = buffer.toString();
                } else if(path == null) {
                    path = buffer.toString();
                } else {
                    logger.error("Error in line '{}': duplicate path indicator '/' - use [] to escape", line);
                    return null;
                }
                buffer = new StringBuilder();
                continue;
            } else if(c == ':' && !isQuoted) {
                if(type != null) {
                    logger.error("Error in line '{}': duplicate type indicator ':' - use [] to escape", line);
                    return null;
                }
                type = String.valueOf(line.charAt(i - 1));
                buffer = new StringBuilder();
                continue;
            } else if(c == '=' && !isQuoted) {
                if(withValue) {
                    logger.error("Error in line '{}': invalid value indicator '=' - values not allowed in this file", line);
                    return null;
                }
                name = buffer.toString();
                buffer = new StringBuilder();
                continue;
            } else if(c == '$' && !isQuoted) {
                if(!withValue) {
                    name = buffer.toString();
                } else {
                    value = buffer.toString();
                }
                int parameterEnd = line.indexOf(' ', i);
                if(parameterEnd == -1) {
                    parameterEnd = line.length();
                }
                String[] parameter = line.substring(i + 1, parameterEnd).split("=");
                if(parameter.length == 1) {
                    parameters.put(parameter[0], null);
                } else {
                    parameters.put(parameter[0], parameter[1]);
                }
            } else if(c == '*' && !isQuoted) {
                if(fileName == null) {
                    logger.error("Error in line '{}': wildcard '*' not allowed in filenames - use [] to escape", line);
                    return null;
                }
            }
            buffer.append(c);
        }
        if(path == null || path.isEmpty()) {
            path = "*";
        }
        if(type == null || type.isEmpty()) {
            type = "*";
        }
        if (!withValue && name == null) {
            name = buffer.toString();
        } else if(value == null) {
            value = buffer.toString();
        }
        if(name.endsWith("<>")) {
            type += "<>";
            name = name.substring(0, name.length() - 2);
        }
        return new LocalConfigEntry(fileName, type, path, name, value, not, parameters);
    }

    public String getIdentifier() {
        return file + "/" + category + "." + type + ":" + name;
    }

    public String getFormat() {
        return parameters.containsKey("format") ? parameters.get("format") : "forge";
    }

    public boolean passesProperty(String category, String name, String type) {
        boolean passesCategory = passesWithWildcard(this.category, category);
        boolean passesName = passesWithWildcard(this.name, name);
        boolean passesType = passesWithWildcard(this.type, type);
        return passesCategory && passesName && passesType;
    }

    public boolean containsWildcard() {
        return category.indexOf('*') != -1 || name.indexOf('*') != -1 || type.indexOf('*') != -1;
    }

    public boolean passesNotEntry(LocalConfigEntry notEntry) {
        return passesProperty(notEntry.category, notEntry.name, notEntry.type);
    }

    private boolean passesWithWildcard(String s, String t) {
        return s.equals("*") || t.equals("*") || t.matches(Pattern.quote(s).replace("\\*", ".*"));
    }
}
