package net.blay09.mods.defaultoptions.difficulty;

import net.blay09.mods.balm.client.platform.event.callback.ScreenCallback;
import net.blay09.mods.defaultoptions.DefaultOptions;
import net.blay09.mods.defaultoptions.config.DefaultOptionsConfig;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.world.Difficulty;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class DefaultDifficultyHandler {

    private static final Set<Integer> touchedScreens = new HashSet<>();

    public static void initialize() {
        ScreenCallback.Init.After.EVENT.register(DefaultDifficultyHandler::onInitGui);
    }

    public static void onInitGui(Screen screen) {
        if (screen instanceof CreateWorldScreen createWorldScreen) {
            WorldCreationUiState uiState = createWorldScreen.getUiState();

            if (!touchedScreens.contains(createWorldScreen.hashCode())) {
                Difficulty difficulty = DefaultOptionsConfig.getActive().defaultDifficulty.toDifficulty();
                uiState.setDifficulty(difficulty);
                touchedScreens.add(createWorldScreen.hashCode());
                DefaultOptions.logger.info("Default difficulty has been set to {}.", difficulty);
            }

            if (DefaultOptionsConfig.getActive().lockDifficulty) {
                lockDifficultyButton(createWorldScreen);
            }

            uiState.addListener(state -> {
                if (DefaultOptionsConfig.getActive().lockDifficulty) {
                    lockDifficultyButton(createWorldScreen);
                }
            });
        }
    }

    private static void lockDifficultyButton(CreateWorldScreen screen) {
        AbstractWidget difficultyButton = findDifficultyButton(screen);
        if (difficultyButton != null) {
            difficultyButton.active = false;
            DefaultOptions.logger.info("Difficulty has been locked.");
        } else {
            DefaultOptions.logger.error("Could not find difficulty button. Unable to lock difficulty.");
        }
    }

    private static @Nullable AbstractWidget findDifficultyButton(CreateWorldScreen screen) {
        return (AbstractWidget) screen.children().stream()
                .filter(it -> it instanceof CycleButton<?> button && button.getValue() instanceof Difficulty)
                .findAny().orElse(null);
    }
}
