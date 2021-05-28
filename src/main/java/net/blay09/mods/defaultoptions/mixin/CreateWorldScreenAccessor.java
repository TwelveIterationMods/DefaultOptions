package net.blay09.mods.defaultoptions.mixin;

import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreateWorldScreen.class)
public interface CreateWorldScreenAccessor {
    @Accessor(value = "field_238929_E_")
    Button getDifficultyButton();

    @Accessor(value = "field_238936_v_")
    void setSelectedDifficulty(Difficulty difficulty);

    @Accessor(value = "field_238937_w_")
    void setEffectiveDifficulty(Difficulty difficulty);
}
