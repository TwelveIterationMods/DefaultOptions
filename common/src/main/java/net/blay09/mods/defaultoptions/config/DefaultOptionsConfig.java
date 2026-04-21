package net.blay09.mods.defaultoptions.config;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.common.config.ConfigLocalization;
import net.blay09.mods.defaultoptions.DefaultOptions;

public class DefaultOptionsConfig {

    public static DefaultOptionsConfigData getActive() {
        return Balm.getConfig().getActive(DefaultOptionsConfigData.class);
    }

    public static void initialize() {
        ConfigLocalization.enableModernTranslationKeys(DefaultOptions.MOD_ID);
        Balm.getConfig().registerConfig(DefaultOptionsConfigData.class, null);
    }

}
