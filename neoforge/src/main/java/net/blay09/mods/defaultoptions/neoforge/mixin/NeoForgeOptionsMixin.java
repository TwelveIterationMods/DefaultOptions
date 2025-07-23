package net.blay09.mods.defaultoptions.neoforge.mixin;

import net.blay09.mods.defaultoptions.DefaultOptionsInitializer;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public class NeoForgeOptionsMixin {
    @Inject(method = "load(Z)V", at = @At("RETURN"), remap = false)
    public void load(boolean limited, CallbackInfo ci) {
        if (limited) {
            DefaultOptionsInitializer.postLoad();
        }
    }
}
