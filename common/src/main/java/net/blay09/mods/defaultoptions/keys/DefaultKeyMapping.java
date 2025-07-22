package net.blay09.mods.defaultoptions.keys;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.api.client.keymappings.KeyModifier;
import net.blay09.mods.defaultoptions.PlatformBindings;
import net.minecraft.client.KeyMapping;

import java.util.Set;

public record DefaultKeyMapping(InputConstants.Key input, Set<KeyModifier> modifiers) {
    public boolean matches(KeyMapping keyMapping) {
        final var keyModifiers = PlatformBindings.INSTANCE.getKeyModifiers(keyMapping);
        if (!modifiers.equals(keyModifiers)) {
            return false;
        }

        switch (input.getType()) {
            case KEYSYM -> {
                return keyMapping.matches(input.getValue(), InputConstants.UNKNOWN.getValue());
            }
            case SCANCODE -> {
                return keyMapping.matches(InputConstants.UNKNOWN.getValue(), input.getValue());
            }
            case MOUSE -> {
                return keyMapping.matchesMouse(input.getValue());
            }
            default -> {
                return false;
            }
        }
    }
}
