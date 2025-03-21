package com.jlucaso.cultivatorsreward.config;

import java.util.HashMap;
import java.util.Map;

import com.jlucaso.cultivatorsreward.CultivatorsReward;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = CultivatorsReward.MOD_ID)
public class ModConfig implements ConfigData {
    @Comment("Default amount of bonus XP given when harvesting crops (default: 1)")
    public int bonusXp = 1;

    @Comment("Chance percentage to receive XP when harvesting crops (0-100, default: 100)")
    public int xpChance = 100;

    @Comment("Specific XP rewards for different crops. Format: 'modid:cropname'")
    public Map<String, Integer> cropSpecificXp = new HashMap<>();

    @Comment("Specific XP chances for different crops. Format: 'modid:cropname'")
    public Map<String, Integer> cropSpecificXpChance = new HashMap<>();

    public static ModConfig register() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }
}
