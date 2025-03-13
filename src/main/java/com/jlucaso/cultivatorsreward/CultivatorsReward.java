package com.jlucaso.cultivatorsreward;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.CropBlock;
import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.api.event.Event;

import com.jlucaso.cropharvestserver.event.CropHarvestedCallback;
import com.jlucaso.cultivatorsreward.config.CultivatorsConfig;
import com.jlucaso.cultivatorsreward.config.ModConfig;

public class CultivatorsReward implements ModInitializer {
    @Override
    public void onInitialize() {
        ModConfig config = ModConfig.register();
        CultivatorsConfig.bonusXp = config.bonusXp;

        // COMPATIBILITY WITH cropharvestserver mod
        FabricLoader.getInstance().getModContainer("cropharvestserver").ifPresent(modContainer -> {
            @SuppressWarnings("unchecked")
            Event<CropHarvestedCallback> sharedEvent = (Event<CropHarvestedCallback>) FabricLoader.getInstance()
                    .getObjectShare()
                    .get(CropHarvestedCallback.EVENT_KEY);
            if (sharedEvent != null) {
                sharedEvent.register(
                        (CropHarvestedCallback) (player, blockPos, state, blockEntity) -> {
                            if (player instanceof ServerPlayerEntity) {
                                ((ServerPlayerEntity) player).addExperience(CultivatorsConfig.bonusXp);
                            }
                        });

            }
        });

        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (!world.isClient() && state.getBlock() instanceof CropBlock crop && crop.isMature(state)) {
                if (player instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity) player).addExperience(CultivatorsConfig.bonusXp);
                }
            }
            return true;
        });
    }
}
