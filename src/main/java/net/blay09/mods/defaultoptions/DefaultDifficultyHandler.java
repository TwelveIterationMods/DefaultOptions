package net.blay09.mods.defaultoptions;

import net.blay09.mods.defaultoptions.mixin.CreateWorldScreenAccessor;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.world.Difficulty;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.IWorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DefaultOptions.MOD_ID, value = Dist.CLIENT)
public class DefaultDifficultyHandler {

    @SubscribeEvent
    public static void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof CreateWorldScreenAccessor) {
            CreateWorldScreenAccessor screen = (CreateWorldScreenAccessor) event.getGui();
            Difficulty difficulty = DefaultOptionsConfig.COMMON.defaultDifficulty.get();
            screen.setSelectedDifficulty(difficulty);
            screen.setEffectiveDifficulty(difficulty);
            if (DefaultOptionsConfig.COMMON.lockDifficulty.get()) {
                screen.getDifficultyButton().active = false;
            }
        }
    }

    @SubscribeEvent
    public static void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Pre event) {
        if (event.getGui() instanceof CreateWorldScreenAccessor) {
            CreateWorldScreenAccessor screen = (CreateWorldScreenAccessor) event.getGui();
            if (screen.getDifficultyButton().active && DefaultOptionsConfig.COMMON.lockDifficulty.get()) {
                screen.getDifficultyButton().active = false;
            }
        }
    }
}
