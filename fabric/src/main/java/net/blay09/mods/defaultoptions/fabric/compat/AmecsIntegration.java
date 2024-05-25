package net.blay09.mods.defaultoptions.fabric.compat;

import de.siphalor.amecs.api.AmecsKeyBinding;
import de.siphalor.amecs.api.KeyBindingUtils;
import de.siphalor.amecs.api.KeyModifiers;
import net.blay09.mods.balm.api.client.keymappings.KeyModifier;
import net.blay09.mods.defaultoptions.PlatformBindings;
import net.minecraft.client.KeyMapping;

import java.util.HashSet;
import java.util.Set;

public class AmecsIntegration {
    public AmecsIntegration() {
        PlatformBindings.INSTANCE = new PlatformBindings() {
            @Override
            public void setDefaultKeyModifiers(KeyMapping keyMapping, Set<KeyModifier> keyModifiers) {
                if (keyMapping instanceof AmecsKeyBinding amecsKeyBinding) {
                    final var amecsDefaultModifiers = amecsKeyBinding.getDefaultModifiers();
                    applyModifiers(amecsDefaultModifiers, keyModifiers);
                }
            }

            @Override
            public void setKeyModifiers(KeyMapping keyMapping, Set<KeyModifier> keyModifiers) {
                final var amecsModifiers = KeyBindingUtils.getBoundModifiers(keyMapping);
                applyModifiers(amecsModifiers, keyModifiers);
            }

            @Override
            public Set<KeyModifier> getKeyModifiers(KeyMapping keyMapping) {
                final var amecsModifiers = KeyBindingUtils.getBoundModifiers(keyMapping);
                return fromAmecs(amecsModifiers);
            }

            @Override
            public Set<KeyModifier> getDefaultKeyModifiers(KeyMapping keyMapping) {
                final var amecsDefaultModifiers = KeyBindingUtils.getDefaultModifiers(keyMapping);
                return fromAmecs(amecsDefaultModifiers);
            }

            private static void applyModifiers(KeyModifiers amecsModifiers, Set<KeyModifier> keyModifiers) {
                amecsModifiers.unset();
                for (final var keyModifier : keyModifiers) {
                    switch (keyModifier) {
                        case CONTROL -> amecsModifiers.setControl(true);
                        case SHIFT -> amecsModifiers.setShift(true);
                        case ALT -> amecsModifiers.setAlt(true);
                    }
                }
            }

            private static HashSet<KeyModifier> fromAmecs(KeyModifiers amecsModifiers) {
                final var modifiers = new HashSet<KeyModifier>();
                if (amecsModifiers.getControl()) {
                    modifiers.add(KeyModifier.CONTROL);
                }
                if (amecsModifiers.getShift()) {
                    modifiers.add(KeyModifier.SHIFT);
                }
                if (amecsModifiers.getAlt()) {
                    modifiers.add(KeyModifier.ALT);
                }
                return modifiers;
            }
        };
    }
}
