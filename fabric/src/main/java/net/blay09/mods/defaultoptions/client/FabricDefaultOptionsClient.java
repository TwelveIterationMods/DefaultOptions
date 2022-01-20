package net.blay09.mods.defaultoptions.client;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.api.client.keymappings.KeyModifier;
import net.blay09.mods.defaultoptions.DefaultOptions;
import net.blay09.mods.defaultoptions.PlatformBindings;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.KeyMapping;

public class FabricDefaultOptionsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        PlatformBindings.INSTANCE = new PlatformBindings() {
            @Override
            public void setDefaultKeyModifier(KeyMapping keyMapping, KeyModifier keyModifier) {
            }

            @Override
            public void setKeyModifier(KeyMapping keyMapping, KeyModifier keyModifier) {
            }

            @Override
            public KeyModifier getKeyModifier(KeyMapping keyMapping) {
                return KeyModifier.NONE;
            }

            @Override
            public KeyModifier getDefaultKeyModifier(KeyMapping keyMapping) {
                return KeyModifier.NONE;
            }
        };

        Balm.initialize(DefaultOptions.MOD_ID, () -> {});
        BalmClient.initialize(DefaultOptions.MOD_ID, DefaultOptions::initialize);
    }
}
