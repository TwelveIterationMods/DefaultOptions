package net.blay09.mods.defaultoptions;

import net.blay09.mods.balm.api.client.keymappings.KeyModifier;
import net.minecraft.client.KeyMapping;

import java.util.Set;

public abstract class PlatformBindings {

    public static PlatformBindings INSTANCE;

    public abstract void setDefaultKeyModifiers(KeyMapping keyMapping, Set<KeyModifier> keyModifiers);

    public abstract void setKeyModifiers(KeyMapping keyMapping, Set<KeyModifier> keyModifiers);

    public abstract Set<KeyModifier> getKeyModifiers(KeyMapping keyMapping);

    public abstract Set<KeyModifier> getDefaultKeyModifiers(KeyMapping keyMapping);
}
