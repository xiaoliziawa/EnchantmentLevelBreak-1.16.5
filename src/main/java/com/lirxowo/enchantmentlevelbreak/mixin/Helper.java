package com.lirxowo.enchantmentlevelbreak.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import com.lirxowo.enchantmentlevelbreak.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(EnchantmentHelper.class)
public class Helper {

    @Inject(method = "setEnchantments(Ljava/util/Map;Lnet/minecraft/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    private static void onSetEnchantments(Map<Enchantment, Integer> enchantments, ItemStack stack, CallbackInfo ci) {
        ListNBT listnbt = new ListNBT();

        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            if (enchantment != null) {
                int level = entry.getValue();
                CompoundNBT compoundnbt = new CompoundNBT();
                compoundnbt.putString("id", String.valueOf(Registry.ENCHANTMENT.getKey(enchantment)));
                compoundnbt.putInt("lvl", level);
                listnbt.add(compoundnbt);
            }
        }

        if (stack.getItem() == Items.ENCHANTED_BOOK) {
            if (listnbt.isEmpty()) {
                stack.removeTagKey("StoredEnchantments");
            } else {
                stack.addTagElement("StoredEnchantments", listnbt);
            }
        } else {
            if (listnbt.isEmpty()) {
                stack.removeTagKey("Enchantments");
            } else {
                stack.addTagElement("Enchantments", listnbt);
            }
        }

        ci.cancel();
    }

    @Redirect(
        method = "getItemEnchantmentLevel(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/item/ItemStack;)I",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/math/MathHelper;clamp(III)I"
        )
    )
    private static int redirectClamp(int value, int min, int max) {
        return MathHelper.clamp(value, min, Config.maxEnchantmentLevel);
    }
}
