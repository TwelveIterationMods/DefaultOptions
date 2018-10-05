package net.blay09.mods.defaultoptions;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DefaultDifficultyHandler {

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (!event.getWorld().isRemote) {
            if (event.getWorld().getWorldInfo().getWorldTotalTime() == 0) {
                event.getWorld().getWorldInfo().setDifficulty(DefaultOptionsConfig.defaultDifficulty);
                event.getWorld().getWorldInfo().setDifficultyLocked(DefaultOptionsConfig.lockDifficulty);
            }
        }
    }

}
