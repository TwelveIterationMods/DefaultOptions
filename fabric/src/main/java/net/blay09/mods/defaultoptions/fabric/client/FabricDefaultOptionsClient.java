package net.blay09.mods.defaultoptions.fabric.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.client.BalmClient;
import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.blay09.mods.defaultoptions.DefaultOptions;
import net.blay09.mods.defaultoptions.DefaultOptionsInitializer;
import net.blay09.mods.defaultoptions.PlatformBindings;
import net.blay09.mods.defaultoptions.keys.KeyModifier;
import net.blay09.mods.defaultoptions.fabric.mixin.FabricKeyMappingAccessor;
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
            public InputConstants.Key getKey(KeyMapping keyMapping) {
                return ((FabricKeyMappingAccessor) keyMapping).getKey();
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

        Balm.initializeMod(DefaultOptions.MOD_ID, FabricLoadContext.INSTANCE, (registrars) -> {});
        BalmClient.initializeMod(DefaultOptions.MOD_ID, FabricLoadContext.INSTANCE, DefaultOptions::initialize);

        ClientLifecycleCallback.Started.EVENT.register((client) -> DefaultOptionsInitializer.postLoad());
    }
}
