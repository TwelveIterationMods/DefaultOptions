package net.blay09.mods.defaultoptions;

import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.IWorldInfo;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DefaultOptions.MOD_ID)
public class DefaultDifficultyHandler {

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        final IWorldInfo worldInfo = event.getWorld().getWorldInfo();
        if (worldInfo instanceof IServerConfiguration) {
            if (worldInfo.getGameTime() == 0) {
                ((IServerConfiguration) worldInfo).setDifficulty(DefaultOptionsConfig.COMMON.defaultDifficulty.get());
                ((IServerConfiguration) worldInfo).setDifficultyLocked(DefaultOptionsConfig.COMMON.lockDifficulty.get());
            }
        }
    }

}
