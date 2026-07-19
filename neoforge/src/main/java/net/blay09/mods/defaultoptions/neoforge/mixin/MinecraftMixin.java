package net.blay09.mods.defaultoptions.neoforge.mixin;

import net.blay09.mods.defaultoptions.DefaultOptionsContext;
import net.blay09.mods.defaultoptions.DefaultOptionsInitializer;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class, priority = 100)
public class MinecraftMixin {
    @Inject(
            method = "<init>",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/Minecraft;gameDirectory:Ljava/io/File;",
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER
            )
    )
    private void init(CallbackInfo ci) {
        DefaultOptionsInitializer.earlyLoad(new DefaultOptionsContext(((Minecraft) (Object) this).gameDirectory));
    }
}
