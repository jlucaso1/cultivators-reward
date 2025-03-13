package com.jlucaso.cultivatorsreward.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "cultivators-reward")
public class ModConfig implements ConfigData {
    @Comment("Amount of bonus XP given when harvesting crops (default: 1)")
    public int bonusXp = 1;

    public static ModConfig register() {
        AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }
}
