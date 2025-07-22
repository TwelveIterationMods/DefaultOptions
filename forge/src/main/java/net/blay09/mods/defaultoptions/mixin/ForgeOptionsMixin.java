package net.blay09.mods.defaultoptions.mixin;

import net.blay09.mods.defaultoptions.DefaultOptionsInitializer;
import net.minecraft.client.Options;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Options.class)
public class ForgeOptionsMixin {
    @ModifyArg(method = "load(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;dataFix(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/nbt/CompoundTag;"))
    private CompoundTag processPreDataFixedOptions(CompoundTag fields) {
        // This will operate on raw options data before data fixers are applied to it, but I think that's fine
        // considering key mapping options have been stable for a long time and even if they ever change,
        // it would be extremely rare to happen in a modpack environment.
        DefaultOptionsInitializer.detectAndMarkModifiedKeys((Options) (Object) this, fields);
        return fields;
    }

}
