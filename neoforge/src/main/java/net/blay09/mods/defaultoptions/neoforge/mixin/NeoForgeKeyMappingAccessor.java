package net.blay09.mods.defaultoptions.neoforge.mixin;

import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyMapping.class)
public interface NeoForgeKeyMappingAccessor {
    @Accessor
    @Mutable
    void setKeyModifierDefault(KeyModifier keyModifier);
}
