package net.blay09.mods.defaultoptions.keys;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.api.client.keymappings.KeyModifier;
import net.blay09.mods.defaultoptions.PlatformBindings;
import net.blay09.mods.defaultoptions.mixin.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;

public class DefaultKeyMapping {
    public final InputConstants.Key input;
    public final KeyModifier modifier; // TODO needs to support multiple for amecs

    public DefaultKeyMapping(InputConstants.Key input, KeyModifier modifier) {
        this.input = input;
        this.modifier = modifier;
    }

    public boolean matches(KeyMapping keyMapping) {
        final var keyModifier = PlatformBindings.INSTANCE.getKeyModifier(keyMapping);
        if (keyModifier != modifier) {
            return false;
        }

        if (input.getValue() == InputConstants.UNKNOWN.getValue() && keyMapping.isUnbound()) {
            return true;
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
