package net.blay09.mods.defaultoptions.fabric.client;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.EmptyLoadContext;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.api.client.keymappings.KeyModifier;
import net.blay09.mods.defaultoptions.DefaultOptions;
import net.blay09.mods.defaultoptions.PlatformBindings;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.KeyMapping;

import java.util.Collections;
import java.util.Set;

public class FabricDefaultOptionsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        PlatformBindings.INSTANCE = new PlatformBindings() {
            @Override
            public void setDefaultKeyModifiers(KeyMapping keyMapping, Set<KeyModifier> keyModifiers) {
            }

            @Override
            public void setKeyModifiers(KeyMapping keyMapping, Set<KeyModifier> keyModifiers) {
            }

            @Override
            public Set<KeyModifier> getKeyModifiers(KeyMapping keyMapping) {
                return Collections.emptySet();
            }

            @Override
            public Set<KeyModifier> getDefaultKeyModifiers(KeyMapping keyMapping) {
                return Collections.emptySet();
            }
        };

        Balm.initializeIfLoaded("amecsapi", "net.blay09.mods.defaultoptions.fabric.compat.AmecsIntegration");

        Balm.initialize(DefaultOptions.MOD_ID, EmptyLoadContext.INSTANCE, () -> {});
        BalmClient.initialize(DefaultOptions.MOD_ID, EmptyLoadContext.INSTANCE, DefaultOptions::initialize);
    }
}
