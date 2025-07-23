package net.blay09.mods.defaultoptions.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.api.client.keymappings.KeyModifier;
import net.blay09.mods.balm.api.event.client.ClientStartedEvent;
import net.blay09.mods.defaultoptions.DefaultOptions;
import net.blay09.mods.defaultoptions.DefaultOptionsInitializer;
import net.blay09.mods.defaultoptions.PlatformBindings;
import net.blay09.mods.defaultoptions.fabric.mixin.FabricKeyMappingAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.KeyMapping;

public class FabricDefaultOptionsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        PlatformBindings.INSTANCE = new PlatformBindings() {
            @Override
            public void setDefaultKeyModifier(KeyMapping keyMapping, KeyModifier keyModifier) {
                // TODO amecs only allows default modifiers on Keybindings that are of type AmecsKeybindings
                //      not a big deal though since we have the knownKeys list and only set modifier if it's a previously unseen key
            }

            @Override
            public void setKeyModifier(KeyMapping keyMapping, KeyModifier keyModifier) {
                // TODO this we can do with amecs getBoundModifiers() which is a mutable object
            }

            @Override
            public InputConstants.Key getKey(KeyMapping keyMapping) {
                return ((FabricKeyMappingAccessor) keyMapping).getKey();
            }

            @Override
            public KeyModifier getKeyModifier(KeyMapping keyMapping) {
                return KeyModifier.NONE; // TODO support amecs here by translating getBoundModifiers() into our type
            }

            @Override
            public KeyModifier getDefaultKeyModifier(KeyMapping keyMapping) {
                // TODO amecs only allows default modifiers on Keybindings that are of type AmecsKeybindings
                return KeyModifier.NONE;
            }
        };

        Balm.initialize(DefaultOptions.MOD_ID, () -> {});
        BalmClient.initialize(DefaultOptions.MOD_ID, DefaultOptions::initialize);

        Balm.getEvents().onEvent(ClientStartedEvent.class, (event) -> DefaultOptionsInitializer.postLoad());
    }
}
