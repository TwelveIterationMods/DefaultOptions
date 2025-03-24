package net.blay09.mods.defaultoptions.difficulty;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Difficulty;

import java.util.Locale;

/**
 * This is needed because Gson does not support obfuscated enums and will fail deserializing them
 */
public enum UnobfuscatedDifficulty implements StringRepresentable {
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

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
