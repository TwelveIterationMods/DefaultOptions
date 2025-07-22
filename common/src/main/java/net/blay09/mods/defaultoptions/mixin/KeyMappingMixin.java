package net.blay09.mods.defaultoptions.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.defaultoptions.DefaultOptionsKeyMapping;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyMapping.class)
public class KeyMappingMixin implements DefaultOptionsKeyMapping {

    private boolean defaultoptions$userModified = false;

    @Inject(method = "setKey", at = @At("HEAD"))
    void setKey(InputConstants.Key key, CallbackInfo ci) {
        // setKey is only called when the key didn't match the default on options load, so it's not reliable.
        // We just track it additionally to cover all bases.
        defaultoptions$userModified = true;
    }

    @Override
    public boolean defaultoptions$wasUserModified() {
        return defaultoptions$userModified;
    }

    @Override
    public void defaultoptions$setUserModified(boolean modified) {
        defaultoptions$userModified = modified;
    }
}
