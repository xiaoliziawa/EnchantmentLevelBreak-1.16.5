package com.lirxowo.enchantmentlevelbreak.mixin;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "enchant", at = @At("HEAD"), cancellable = true)
    private void onEnchant(Enchantment enchantment, int level, CallbackInfo ci) {
        ItemStack stack = (ItemStack)(Object)this;
        stack.getOrCreateTag();
        if (!stack.getTag().contains("Enchantments", 9)) {
            stack.getTag().put("Enchantments", new ListNBT());
        }
        ListNBT enchantments = stack.getTag().getList("Enchantments", 10);
        CompoundNBT enchantmentTag = new CompoundNBT();
        enchantmentTag.putString("id", String.valueOf(Registry.ENCHANTMENT.getKey(enchantment)));
        enchantmentTag.putInt("lvl", level);
        enchantments.add(enchantmentTag);
        ci.cancel();
    }
}
