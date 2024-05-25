package net.blay09.mods.defaultoptions.forge;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.EmptyLoadContext;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.api.client.keymappings.KeyModifier;
import net.blay09.mods.defaultoptions.DefaultOptions;
import net.blay09.mods.defaultoptions.PlatformBindings;
import net.blay09.mods.defaultoptions.forge.mixin.ForgeKeyMappingAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.Set;

@Mod(DefaultOptions.MOD_ID)
public class ForgeDefaultOptions {

    public ForgeDefaultOptions() {
        PlatformBindings.INSTANCE = new PlatformBindings() {
            @Override
            public void setDefaultKeyModifiers(KeyMapping keyMapping, Set<KeyModifier> keyModifiers) {
                final var keyModifier = keyModifiers.stream().findFirst().orElse(KeyModifier.NONE);
                net.minecraftforge.client.settings.KeyModifier forgeKeyModifier = switch (keyModifier) {
                    case ALT -> net.minecraftforge.client.settings.KeyModifier.ALT;
                    case SHIFT -> net.minecraftforge.client.settings.KeyModifier.SHIFT;
                    case CONTROL -> net.minecraftforge.client.settings.KeyModifier.CONTROL;
                    default -> net.minecraftforge.client.settings.KeyModifier.NONE;
                };
                ((ForgeKeyMappingAccessor) keyMapping).setKeyModifierDefault(forgeKeyModifier);
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

            private static KeyModifier fromForge(net.minecraftforge.client.settings.KeyModifier keyModifier) {
                return switch (keyModifier) {
                    case NONE -> KeyModifier.NONE;
                    case SHIFT -> KeyModifier.SHIFT;
                    case CONTROL -> KeyModifier.CONTROL;
                    case ALT -> KeyModifier.ALT;
                };
            }

            private static net.minecraftforge.client.settings.KeyModifier toForge(KeyModifier keyModifier) {
                return switch (keyModifier) {
                    case NONE -> net.minecraftforge.client.settings.KeyModifier.NONE;
                    case SHIFT -> net.minecraftforge.client.settings.KeyModifier.SHIFT;
                    case CONTROL -> net.minecraftforge.client.settings.KeyModifier.CONTROL;
                    case ALT -> net.minecraftforge.client.settings.KeyModifier.ALT;
                };
            }
        };

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            Balm.initialize(DefaultOptions.MOD_ID, EmptyLoadContext.INSTANCE, () -> {
            });
            BalmClient.initialize(DefaultOptions.MOD_ID, EmptyLoadContext.INSTANCE, DefaultOptions::initialize);
        });

        ModLoadingContext.get()
                .registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                        () -> new IExtensionPoint.DisplayTest(() -> IExtensionPoint.DisplayTest.IGNORESERVERONLY, (a, b) -> true));
    }

}
