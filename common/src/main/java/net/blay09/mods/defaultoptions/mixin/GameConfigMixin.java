package net.blay09.mods.defaultoptions.mixin;

import com.mojang.blaze3d.platform.DisplayData;
import net.blay09.mods.defaultoptions.DefaultOptionsContext;
import net.blay09.mods.defaultoptions.DefaultOptionsInitializer;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameConfig.class)
public class GameConfigMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void defaultoptions$earlyLoad(GameConfig.UserData userData, DisplayData displayData, GameConfig.FolderData folderData, GameConfig.GameData gameData, GameConfig.QuickPlayData quickPlayData, CallbackInfo ci) {
        DefaultOptionsInitializer.earlyLoad(new DefaultOptionsContext(folderData.gameDirectory));
    }
}
