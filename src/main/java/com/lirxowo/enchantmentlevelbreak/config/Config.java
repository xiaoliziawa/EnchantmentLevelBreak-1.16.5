package com.lirxowo.enchantmentlevelbreak.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import com.lirxowo.enchantmentlevelbreak.Enchantmentlevelbreak;

@Mod.EventBusSubscriber(modid = Enchantmentlevelbreak.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue USE_ROMAN_NUMERALS_VALUE = BUILDER
            .comment("Use roman numerals for enchantment levels instead of arabic numbers")
            .define("useRomanNumerals", true);

    private static final ForgeConfigSpec.BooleanValue ALLOW_ANY_ENCHANTMENT_VALUE = BUILDER
            .comment("Allow applying any enchantment book to any item in anvil")
            .define("allowAnyEnchantment", false);

    private static final ForgeConfigSpec.BooleanValue ALLOW_VANILLA_LEVEL_STACKING_VALUE = BUILDER
            .comment("Allow vanilla enchantment level stacking in anvil (e.g. 4+4=5, same level +1)")
            .define("allowVanillaLevelStacking", true);

    private static final ForgeConfigSpec.BooleanValue ALLOW_LEVEL_STACKING_VALUE = BUILDER
            .comment("Allow unlimited enchantment level stacking in anvil (e.g. 4+4=8 instead of vanilla's 4+4=5)")
            .define("allowLevelStacking", false);

    private static final ForgeConfigSpec.IntValue MAX_ENCHANTMENT_LEVEL_VALUE = BUILDER
            .comment("Maximum level for enchantments (range: 255-2147483647)")
            .defineInRange("maxEnchantmentLevel", 2147483647, 255, 2147483647);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean useRomanNumerals;
    public static boolean allowAnyEnchantment;
    public static boolean allowVanillaLevelStacking;
    public static boolean allowLevelStacking;
    public static int maxEnchantmentLevel;

    @SubscribeEvent
    public static void onLoad(ModConfig.ModConfigEvent event) {
        useRomanNumerals = USE_ROMAN_NUMERALS_VALUE.get();
        allowAnyEnchantment = ALLOW_ANY_ENCHANTMENT_VALUE.get();
        allowVanillaLevelStacking = ALLOW_VANILLA_LEVEL_STACKING_VALUE.get();
        allowLevelStacking = ALLOW_LEVEL_STACKING_VALUE.get();
        maxEnchantmentLevel = MAX_ENCHANTMENT_LEVEL_VALUE.get();
    }
}
