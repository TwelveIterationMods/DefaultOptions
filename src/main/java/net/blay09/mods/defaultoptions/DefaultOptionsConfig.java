package net.blay09.mods.defaultoptions;

import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.common.config.Config;

@Config(modid = DefaultOptions.MOD_ID)
public class DefaultOptionsConfig {

    @Config.Name("Default Difficulty")
    @Config.Comment("The default difficulty selected for newly created worlds.")
    public static EnumDifficulty defaultDifficulty = EnumDifficulty.NORMAL;

    @Config.Name("Lock Difficulty")
    @Config.Comment("Set to true if the difficulty for new world's should be locked ot the specific default. This cannot be unlocked by players without external tools! Probably a bad idea. I don't recommend. Why am I adding this option?")
    public static boolean lockDifficulty = false;

    public static void onConfigReload() {
    }
}
