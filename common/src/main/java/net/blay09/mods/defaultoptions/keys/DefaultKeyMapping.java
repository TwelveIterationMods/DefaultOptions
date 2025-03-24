package net.blay09.mods.defaultoptions.keys;

import com.mojang.blaze3d.platform.InputConstants;

import java.util.Set;

public record DefaultKeyMapping(InputConstants.Key input, Set<KeyModifier> modifiers) {
}
