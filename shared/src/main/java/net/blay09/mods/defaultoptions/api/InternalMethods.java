package net.blay09.mods.defaultoptions.api;

import java.io.File;

public interface InternalMethods {

    SimpleDefaultOptionsHandler registerOptionsFile(File file);
    void registerOptionsHandler(DefaultOptionsHandler handler);

}
