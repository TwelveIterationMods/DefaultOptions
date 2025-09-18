package net.blay09.mods.defaultoptions.keys;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.mixin.KeyMappingAccessor;
import net.blay09.mods.defaultoptions.PlatformBindings;
import net.minecraft.client.KeyMapping;

import java.util.Set;

public record DefaultKeyMapping(InputConstants.Key input, Set<KeyModifier> modifiers) {
    public boolean matches(KeyMapping keyMapping) {
        final var keyModifiers = PlatformBindings.INSTANCE.getKeyModifiers(keyMapping);
        if (!modifiers.equals(keyModifiers)) {
            return false;
        }

        if (input.getValue() == InputConstants.UNKNOWN.getValue() && keyMapping.isUnbound()) {
            return true;
        }

        final var key = ((KeyMappingAccessor) keyMapping).getKey();
        switch (input.getType()) {
            case KEYSYM -> {
                return key.getType() == InputConstants.Type.KEYSYM && key.getValue() == input.getValue();
            }
            case SCANCODE -> {
                return key.getType() == InputConstants.Type.SCANCODE && key.getValue() == input.getValue();
            }
            case MOUSE -> {
                return key.getType() == InputConstants.Type.MOUSE && key.getValue() == input.getValue();
            }
            default -> {
                return false;
            }
        }
    }
}
