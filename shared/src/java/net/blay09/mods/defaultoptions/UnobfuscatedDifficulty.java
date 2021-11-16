package net.blay09.mods.defaultoptions;

import net.minecraft.world.Difficulty;

/**
 * This is needed because Gson does not support obfuscated enums and will fail deserializing them
 */
public enum UnobfuscatedDifficulty {
    PEACEFUL,
    EASY,
    NORMAL,
    HARD;

    public Difficulty toDifficulty() {
        return switch (this) {
            case PEACEFUL -> Difficulty.PEACEFUL;
            case EASY -> Difficulty.EASY;
            case NORMAL -> Difficulty.NORMAL;
            case HARD -> Difficulty.HARD;
        };
    }
}
