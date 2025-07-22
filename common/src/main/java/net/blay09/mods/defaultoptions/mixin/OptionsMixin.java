package net.blay09.mods.defaultoptions.mixin;

import net.blay09.mods.defaultoptions.DefaultOptionsInitializer;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Options.class, priority = 100)
public class OptionsMixin {
    @Inject(method = "load()V", at = @At("HEAD"))
    private void load(CallbackInfo ci) {
        DefaultOptionsInitializer.collectUserModifiedKeys((Options) (Object) this);
        DefaultOptionsInitializer.preLoad();
    }

    @Inject(method = "save()V", at = @At("RETURN"))
    private void save(CallbackInfo ci) {
        DefaultOptionsInitializer.postSave();
    }

}
