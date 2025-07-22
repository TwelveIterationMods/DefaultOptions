package net.blay09.mods.defaultoptions.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.defaultoptions.DefaultOptionsKeyMapping;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyMapping.class)
public class ForgeKeyMappingMixin {

    @Inject(method = "setKeyModifierAndCode", at = @At("HEAD"), remap = false)
    void setKeyModifierAndCode(KeyModifier keyModifier, InputConstants.Key keyCode, CallbackInfo ci) {
        // setKey is only called when the key didn't match the default on options load, so it's not reliable.
        // We just track it additionally to cover all bases.
        ((DefaultOptionsKeyMapping) this).defaultoptions$setUserModified(true);
    }

}
