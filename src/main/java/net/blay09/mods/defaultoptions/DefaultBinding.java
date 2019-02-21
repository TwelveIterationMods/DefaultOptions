package net.blay09.mods.defaultoptions;

import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyModifier;

class DefaultBinding {
    public final InputMappings.Input input;
    public final KeyModifier modifier;

    public DefaultBinding(InputMappings.Input input, KeyModifier modifier) {
        this.input = input;
        this.modifier = modifier;
    }
}
