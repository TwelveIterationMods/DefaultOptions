package net.blay09.mods.defaultoptions;

import net.minecraft.world.Difficulty;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class DefaultOptionsConfig {

    public static class Common {
        public final ForgeConfigSpec.ConfigValue<Difficulty> defaultDifficulty;
        public final ForgeConfigSpec.BooleanValue lockDifficulty;

        Common(ForgeConfigSpec.Builder builder) {
            defaultDifficulty = builder
                    .comment("The default difficulty selected for newly created worlds.")
                    .translation("defaultoptions.config.defaultDifficulty")
                    .define("defaultDifficulty", Difficulty.NORMAL);

            lockDifficulty = builder
                    .comment("Set to true if the difficulty for new world's should be locked ot the specific default. This cannot be unlocked by players without external tools! Probably a bad idea. I don't recommend. Why am I adding this option?")
                    .translation("defaultoptions.config.lockDifficulty")
                    .define("lockDifficulty", false);
        }
    }

    static final ForgeConfigSpec commonSpec;
    public static final Common COMMON;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }

}
