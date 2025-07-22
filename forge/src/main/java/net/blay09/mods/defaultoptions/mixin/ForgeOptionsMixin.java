package net.blay09.mods.defaultoptions.mixin;

import net.blay09.mods.defaultoptions.DefaultOptionsKeyMapping;
import net.minecraft.client.Options;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.HashSet;

@Mixin(Options.class)
public class ForgeOptionsMixin {
    @ModifyArg(method = "load(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;dataFix(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/nbt/CompoundTag;"))
    private CompoundTag processPreDataFixedOptions(CompoundTag fields) {
        // This will operate on raw options data before data fixers are applied to it, but I think that's fine
        // considering key mapping options have been stable for a long time and even if they ever change,
        // it would be extremely rare to happen in a modpack environment.
        final var options = (Options) (Object) this;
        final var knownKeys = new HashSet<String>();
        for (final var option : fields.getAllKeys()) {
            if (option.startsWith("key_")) {
                final var name = option.substring("key_".length());
                knownKeys.add(name);
            }
        }
        for (final var keyMapping : options.keyMappings) {
            if (knownKeys.contains(keyMapping.getName())) {
                ((DefaultOptionsKeyMapping) keyMapping).defaultoptions$setUserModified(true);
            }
        }
        return fields;
    }

}
