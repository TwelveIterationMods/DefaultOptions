package net.blay09.mods.defaultoptions.mixin;

import net.blay09.mods.defaultoptions.DefaultOptionsInitializer;
import net.minecraft.client.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameSettings.class)
public class GameSettingsMixin {
    @Inject(method = "loadOptions()V", at = @At("HEAD"))
    private void loadOptions(CallbackInfo ci) {
        DefaultOptionsInitializer.applyDefaults();
    }
}
