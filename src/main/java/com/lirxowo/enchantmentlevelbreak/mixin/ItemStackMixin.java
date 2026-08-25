package com.lirxowo.enchantmentlevelbreak.mixin;

import com.lirxowo.enchantmentlevelbreak.config.Config;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "enchant", at = @At("HEAD"), cancellable = true)
    private void onEnchant(Enchantment enchantment, int level, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        ResourceLocation id = Registry.ENCHANTMENT.getKey(enchantment);
        if (id == null) {
            return;
        }

        String idString = id.toString();
        int clampedLevel = Math.min(level, Config.maxEnchantmentLevel);
        ListNBT enchantments = stack.getEnchantmentTags();

        for (int i = 0; i < enchantments.size(); i++) {
            CompoundNBT existing = enchantments.getCompound(i);
            if (idString.equals(existing.getString("id"))) {
                existing.putInt("lvl", clampedLevel);
                stack.addTagElement("Enchantments", enchantments);
                ci.cancel();
                return;
            }
        }

        CompoundNBT entry = new CompoundNBT();
        entry.putString("id", idString);
        entry.putInt("lvl", clampedLevel);
        enchantments.add(entry);
        stack.addTagElement("Enchantments", enchantments);
        ci.cancel();
    }
}
