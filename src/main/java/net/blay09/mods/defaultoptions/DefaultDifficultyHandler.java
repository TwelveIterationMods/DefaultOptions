package net.blay09.mods.defaultoptions;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DefaultOptions.MOD_ID)
public class DefaultDifficultyHandler {

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        if (!event.getWorld().isRemote()) {
            if (event.getWorld().getWorldInfo().getGameTime() == 0) {
                event.getWorld().getWorldInfo().setDifficulty(DefaultOptionsConfig.COMMON.defaultDifficulty.get());
                event.getWorld().getWorldInfo().setDifficultyLocked(DefaultOptionsConfig.COMMON.lockDifficulty.get());
            }
        }
    }

}
