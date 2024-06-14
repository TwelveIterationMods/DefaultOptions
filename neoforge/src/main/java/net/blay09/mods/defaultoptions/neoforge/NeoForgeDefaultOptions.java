package net.blay09.mods.defaultoptions.neoforge;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.api.client.keymappings.KeyModifier;
import net.blay09.mods.balm.neoforge.NeoForgeLoadContext;
import net.blay09.mods.defaultoptions.DefaultOptions;
import net.blay09.mods.defaultoptions.PlatformBindings;
import net.blay09.mods.defaultoptions.neoforge.mixin.NeoForgeKeyMappingAccessor;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import java.util.Collections;
import java.util.Set;

@Mod(value = DefaultOptions.MOD_ID, dist = Dist.CLIENT)
public class NeoForgeDefaultOptions {

    public NeoForgeDefaultOptions(IEventBus modEventBus) {
        PlatformBindings.INSTANCE = new PlatformBindings() {
            @Override
            public void setDefaultKeyModifiers(KeyMapping keyMapping, Set<KeyModifier> keyModifiers) {
                final var keyModifier = keyModifiers.stream().findFirst().orElse(KeyModifier.NONE);
                net.neoforged.neoforge.client.settings.KeyModifier forgeKeyModifier = switch (keyModifier) {
                    case ALT -> net.neoforged.neoforge.client.settings.KeyModifier.ALT;
                    case SHIFT -> net.neoforged.neoforge.client.settings.KeyModifier.SHIFT;
                    case CONTROL -> net.neoforged.neoforge.client.settings.KeyModifier.CONTROL;
                    default -> net.neoforged.neoforge.client.settings.KeyModifier.NONE;
                };
                ((NeoForgeKeyMappingAccessor) keyMapping).setKeyModifierDefault(forgeKeyModifier);
            }

            @Override
            public void setKeyModifiers(KeyMapping keyMapping, Set<KeyModifier> keyModifiers) {
                final var keyModifier = keyModifiers.stream().findFirst().orElse(KeyModifier.NONE);
                keyMapping.setKeyModifierAndCode(toForge(keyModifier), keyMapping.getKey());
            }

            @Override
            public Set<KeyModifier> getKeyModifiers(KeyMapping keyMapping) {
                final var keyModifier = fromForge(keyMapping.getKeyModifier());
                return keyModifier != KeyModifier.NONE ? Set.of(keyModifier) : Collections.emptySet();
            }

            @Override
            public Set<KeyModifier> getDefaultKeyModifiers(KeyMapping keyMapping) {
                final var keyModifier = fromForge(keyMapping.getDefaultKeyModifier());
                return keyModifier != KeyModifier.NONE ? Set.of(keyModifier) : Collections.emptySet();
            }

            private static KeyModifier fromForge(net.neoforged.neoforge.client.settings.KeyModifier keyModifier) {
                return switch (keyModifier) {
                    case NONE -> KeyModifier.NONE;
                    case SHIFT -> KeyModifier.SHIFT;
                    case CONTROL -> KeyModifier.CONTROL;
                    case ALT -> KeyModifier.ALT;
                };
            }

            private static net.neoforged.neoforge.client.settings.KeyModifier toForge(KeyModifier keyModifier) {
                return switch (keyModifier) {
                    case NONE -> net.neoforged.neoforge.client.settings.KeyModifier.NONE;
                    case SHIFT -> net.neoforged.neoforge.client.settings.KeyModifier.SHIFT;
                    case CONTROL -> net.neoforged.neoforge.client.settings.KeyModifier.CONTROL;
                    case ALT -> net.neoforged.neoforge.client.settings.KeyModifier.ALT;
                };
            }
        };

        final var context = new NeoForgeLoadContext(modEventBus);
        Balm.initialize(DefaultOptions.MOD_ID, context, () -> {
        });
        BalmClient.initialize(DefaultOptions.MOD_ID, context, DefaultOptions::initialize);
    }

}