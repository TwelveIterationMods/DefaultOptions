package net.blay09.mods.defaultoptions;

import net.blay09.mods.balm.api.event.client.screen.ScreenDrawEvent;
import net.blay09.mods.balm.api.event.client.screen.ScreenInitEvent;
import net.blay09.mods.defaultoptions.mixin.CreateWorldScreenAccessor;
import net.minecraft.world.Difficulty;

public class DefaultDifficultyHandler {

    public static void onInitGui(ScreenInitEvent.Post event) {
        if (event.getScreen() instanceof CreateWorldScreenAccessor screen) {
            Difficulty difficulty = DefaultOptionsConfig.getActive().defaultDifficulty;
            screen.setDifficulty(difficulty);
            if (DefaultOptionsConfig.getActive().lockDifficulty) {
                screen.getDifficultyButton().active = false;
            }
        }
    }

    public static void onDrawScreen(ScreenDrawEvent.Pre event) {
        if (event.getScreen() instanceof CreateWorldScreenAccessor screen) {
            if (screen.getDifficultyButton().active && DefaultOptionsConfig.getActive().lockDifficulty) {
                screen.getDifficultyButton().active = false;
            }
        }
    }
}
