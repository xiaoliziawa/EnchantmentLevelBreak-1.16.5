package com.lirxowo.enchantmentlevelbreak.mixin;

import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.enchantment.Enchantment;
import com.lirxowo.enchantmentlevelbreak.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @Inject(method = "getFullname", at = @At("HEAD"), cancellable = true)
    private void modifyEnchantmentName(int level, CallbackInfoReturnable<ITextComponent> cir) {
        Enchantment enchantment = (Enchantment) (Object) this;
        IFormattableTextComponent name = new TranslationTextComponent(enchantment.getDescriptionId());

        if (level != 1) {
            String levelText;
            if (level > 10000) {
                levelText = String.valueOf(level);
            } else {
                levelText = Config.useRomanNumerals ? enchantmentLevelBreak$intToRoman(level) : String.valueOf(level);
            }
            name.append(" ").append(new StringTextComponent(levelText).withStyle(TextFormatting.GRAY));
        }

        cir.setReturnValue(name);
    }

    @Unique
    private static String enchantmentLevelBreak$intToRoman(int num) {
        if (num <= 0) return "0";

        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        StringBuilder roman = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            while (num >= values[i]) {
                roman.append(symbols[i]);
                num -= values[i];
            }
        }

        return roman.toString();
    }
}
