package net.blay09.mods.defaultoptions;

import me.shedaniel.autoconfig.annotation.Config;
import net.blay09.mods.balm.api.config.BalmConfigData;
import net.blay09.mods.balm.api.config.Comment;
import net.minecraft.world.Difficulty;

@Config(name = DefaultOptions.MOD_ID)
public class DefaultOptionsConfigData implements BalmConfigData {

    @Comment("The default difficulty selected for newly created worlds.")
    public Difficulty defaultDifficulty = Difficulty.NORMAL;

    @Comment("Set to true if the difficulty for new world's should be locked ot the specific default. This cannot be unlocked by players without external tools! Probably a bad idea. I don't recommend. Why am I adding this option?")
    public boolean lockDifficulty = false;

}
