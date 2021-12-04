package net.blay09.mods.defaultoptions;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.api.client.keymappings.KeyModifier;

class DefaultBinding {
    public final InputConstants.Key input;
    public final KeyModifier modifier;

    public DefaultBinding(InputConstants.Key input, KeyModifier modifier) {
        this.input = input;
        this.modifier = modifier;
    }
}
