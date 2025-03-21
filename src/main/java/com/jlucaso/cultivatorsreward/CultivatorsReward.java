package com.jlucaso.cultivatorsreward;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jlucaso.cropharvestserver.event.CropHarvestedCallback;
import com.jlucaso.cultivatorsreward.config.ModConfig;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class CultivatorsReward implements ModInitializer {
    public static final String MOD_ID = "cultivatorsreward";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private final Random random = new Random();
    private ModConfig config;

    @Override
    public void onInitialize() {
        this.config = ModConfig.register();

        // COMPATIBILITY WITH cropharvestserver mod
        FabricLoader.getInstance().getModContainer("cropharvestserver").ifPresent(modContainer -> {
            @SuppressWarnings("unchecked")
            Event<CropHarvestedCallback> sharedEvent = (Event<CropHarvestedCallback>) FabricLoader.getInstance()
                    .getObjectShare()
                    .get(CropHarvestedCallback.EVENT_KEY);
            if (sharedEvent != null) {
                LOGGER.info("Cropharvestserver mod detected, registering shared event.");
                sharedEvent.register((player, serverWorld, blockPos, blockState) -> {
                    addCropExperience((ServerPlayerEntity) player, blockState);
                });
            }
        });

        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (!world.isClient() && state.getBlock() instanceof CropBlock crop && crop.isMature(state)) {
                if (player instanceof ServerPlayerEntity serverPlayerEntity) {
                    addCropExperience(serverPlayerEntity, state);
                }
            }
            return true;
        });
    }

    private void addCropExperience(ServerPlayerEntity player, BlockState state) {
        // Get primary crop identifier
        Identifier blockId = Registries.BLOCK.getId(state.getBlock());
        String cropId = blockId.toString();

        // Get chance and check if we should add XP
        int xpChance = getConfigValue(cropId, this.config.cropSpecificXpChance, this.config.xpChance);
        if (random.nextInt(100) >= xpChance) {
            return;
        }

        // Get XP amount and add to player
        int xpAmount = getConfigValue(cropId, this.config.cropSpecificXp, this.config.bonusXp);
        player.addExperience(xpAmount);
    }

    /**
     * Helper method to get a config value for a crop, with fallbacks
     * 
     * @param <T>          The type of the value
     * @param cropId       The primary crop identifier
     * @param configMap    The config map to look in
     * @param defaultValue The default value if no match is found
     * @return The config value
     */
    private <T> T getConfigValue(String cropId, java.util.Map<String, T> configMap, T defaultValue) {
        // Direct lookup
        if (configMap.containsKey(cropId)) {
            return configMap.get(cropId);
        }

        // Try alternative ID from translation key
        String translationKey = Registries.BLOCK.get(Identifier.tryParse(cropId)).getTranslationKey();
        String[] parts = translationKey.split("\\.");
        if (parts.length >= 3) {
            String altCropId = parts[1] + ":" + parts[2];
            if (configMap.containsKey(altCropId)) {
                return configMap.get(altCropId);
            }
        }

        // Try matching by crop name
        String cropName = cropId.substring(cropId.lastIndexOf(':') + 1);
        for (String key : configMap.keySet()) {
            if (key.contains(cropName)) {
                return configMap.get(key);
            }
        }

        // Return default if no match found
        return defaultValue;
    }
}
