package net.blay09.mods.defaultoptions;

import java.io.IOException;

public class LocalConfigEntry {

    public final String file;
    public final String type;
    public final String path;
    public final String name;
    public final String value;

    public LocalConfigEntry(String file, String type, String path, String name, String value) {
        this.file = file;
        this.type = type;
        this.path = path;
        this.name = name;
        this.value = value;
    }

    public static LocalConfigEntry fromString(String line, boolean withValue) throws IOException {
        if(line.trim().startsWith("#") || line.trim().isEmpty()) {
            return null;
        }
        String fileName = null;
        String path = null;
        String type = null;
        String name = null;
        String value = null;
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
            } else if(fileName == null && c == '/' && !isQuoted) {
                fileName = buffer.toString();
                buffer = new StringBuilder();
                continue;
            } else if(path == null && c == ':' && !isQuoted) {
                path = buffer.substring(0, buffer.length() - 2);
                type = String.valueOf(line.charAt(i - 1));
                buffer = new StringBuilder();
                continue;
            } else if(withValue && name == null && c == '=' && !isQuoted) {
                name = buffer.toString();
                buffer = new StringBuilder();
                continue;
            }
            buffer.append(c);
        }
        if(!withValue) {
            name = buffer.toString();
        } else {
            value = buffer.toString();
        }
        if(fileName == null || type == null || path == null || name == null || (withValue && value == null)) {
            return null;
        }
        if(name.endsWith("<>")) {
            type += "<>";
            name = name.substring(0, name.length() - 2);
        }
        return new LocalConfigEntry(fileName, type, path, name, value);
    }

    public String getIdentifier() {
        return file + "/" + path + "." + type + ":" + name;
    }

}
