package net.blay09.mods.defaultoptions.mixin;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreateWorldScreen.class)
public interface CreateWorldScreenAccessor {
    @Accessor
    CycleButton<Difficulty> getDifficultyButton();

    @Accessor
    void setDifficulty(Difficulty difficulty);
}
