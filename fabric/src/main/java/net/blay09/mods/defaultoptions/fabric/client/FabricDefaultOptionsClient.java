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
                // TODO amecs only allows default modifiers on Keybindings that are of type AmecsKeybindings
                //      not a big deal though since we have the knownKeys list and only set modifier if it's a previously unseen key
            }

            @Override
            public void setKeyModifiers(KeyMapping keyMapping, Set<KeyModifier> keyModifiers) {
                // TODO this we can do with amecs getBoundModifiers() which is a mutable object
            }

            @Override
            public Set<KeyModifier> getKeyModifiers(KeyMapping keyMapping) {
                return Collections.emptySet(); // TODO support amecs here by translating getBoundModifiers() into our type
            }

            @Override
            public Set<KeyModifier> getDefaultKeyModifiers(KeyMapping keyMapping) {
                // TODO amecs only allows default modifiers on Keybindings that are of type AmecsKeybindings
                return Collections.emptySet();
            }
        };

        Balm.initialize(DefaultOptions.MOD_ID, EmptyLoadContext.INSTANCE, () -> {});
        BalmClient.initialize(DefaultOptions.MOD_ID, EmptyLoadContext.INSTANCE, DefaultOptions::initialize);
    }
}
