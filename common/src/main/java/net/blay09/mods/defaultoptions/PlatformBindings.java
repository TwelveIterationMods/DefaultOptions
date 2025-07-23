package net.blay09.mods.defaultoptions;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.api.client.keymappings.KeyModifier;
import net.minecraft.client.KeyMapping;

public abstract class PlatformBindings {

    public static PlatformBindings INSTANCE;

    public abstract void setDefaultKeyModifier(KeyMapping keyMapping, KeyModifier keyModifier);

    public abstract void setKeyModifier(KeyMapping keyMapping, KeyModifier keyModifier);

    public abstract InputConstants.Key getKey(KeyMapping keyMapping);

    public abstract KeyModifier getKeyModifier(KeyMapping keyMapping);

    public abstract KeyModifier getDefaultKeyModifier(KeyMapping keyMapping);
}
