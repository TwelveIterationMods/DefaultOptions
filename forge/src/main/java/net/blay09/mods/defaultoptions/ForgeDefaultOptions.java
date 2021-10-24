package net.blay09.mods.defaultoptions;

import net.blay09.mods.balm.api.client.keymappings.KeyModifier;
import net.blay09.mods.defaultoptions.mixin.ForgeKeyMappingAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.ToggleKeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.network.FMLNetworkConstants;

@Mod(DefaultOptions.MOD_ID)
public class ForgeDefaultOptions {

    public ForgeDefaultOptions() {
        PlatformBindings.INSTANCE = new PlatformBindings() {
            @Override
            public void setDefaultKeyModifier(KeyMapping keyMapping, KeyModifier keyModifier) {
                net.minecraftforge.client.settings.KeyModifier forgeKeyModifier = switch(keyModifier) {
                    case ALT -> net.minecraftforge.client.settings.KeyModifier.ALT;
                    case SHIFT -> net.minecraftforge.client.settings.KeyModifier.SHIFT;
                    case CONTROL -> net.minecraftforge.client.settings.KeyModifier.CONTROL;
                    default -> net.minecraftforge.client.settings.KeyModifier.NONE;
                };
                ((ForgeKeyMappingAccessor) keyMapping).setKeyModifierDefault(forgeKeyModifier);
            }
        };

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> DefaultOptions::initialize);

        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }

}
