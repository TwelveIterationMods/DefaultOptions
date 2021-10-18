package net.blay09.mods.defaultoptions;

import net.blay09.mods.balm.api.client.keymappings.KeyModifier;
import net.minecraft.client.KeyMapping;

public abstract class PlatformBindings {

    public static PlatformBindings INSTANCE;

    public abstract void setDefaultKeyModifier(KeyMapping keyMapping, KeyModifier keyModifier);

}
