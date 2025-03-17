package net.blay09.mods.defaultoptions.config;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.config.reflection.Comment;
import net.blay09.mods.balm.api.config.reflection.Config;
import net.blay09.mods.defaultoptions.DefaultOptions;
import net.blay09.mods.defaultoptions.difficulty.UnobfuscatedDifficulty;

@Config(DefaultOptions.MOD_ID)
public class DefaultOptionsConfig {

    @Comment("The default difficulty selected for newly created worlds.")
    public UnobfuscatedDifficulty defaultDifficulty = UnobfuscatedDifficulty.NORMAL;

    @Comment("Set to true if the difficulty for new world's should be locked to the specific default. This cannot be unlocked by players without external tools! Probably a bad idea. I don't recommend. Why am I adding this option?")
    public boolean lockDifficulty = false;

    public static DefaultOptionsConfig getActive() {
        return Balm.getConfig().getActiveConfig(DefaultOptionsConfig.class);
    }

    public static void initialize() {
        Balm.getConfig().registerConfig(DefaultOptionsConfig.class);
    }
}
