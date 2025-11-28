package com.lirxowo.enchantmentlevelbreak.mixin;

import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.IntReferenceHolder;
import com.lirxowo.enchantmentlevelbreak.config.Config;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.HashMap;

@Mixin(RepairContainer.class)
public abstract class AnvilMenuMixin {

    @Shadow @Final private IntReferenceHolder cost;
    @Shadow private int repairItemCountCost;

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void onCreateResult(CallbackInfo ci) {
        RepairContainer container = (RepairContainer)(Object)this;
        Slot slot0 = container.slots.get(0);
        Slot slot1 = container.slots.get(1);
        Slot slot2 = container.slots.get(2);

        ItemStack left = slot0.getItem();
        ItemStack right = slot1.getItem();

        if (!left.isEmpty() && !right.isEmpty()) {
            handleAnvilOperation(slot2, left, right, ci);
        }
    }

    @Unique
    private void handleAnvilOperation(Slot resultSlot, ItemStack left, ItemStack right, CallbackInfo ci) {
        Map<Enchantment, Integer> leftEnchants = EnchantmentHelper.getEnchantments(left);
        Map<Enchantment, Integer> rightEnchants = EnchantmentHelper.getEnchantments(right);

        if (left.getItem() == right.getItem()) {
            if (!leftEnchants.isEmpty() || !rightEnchants.isEmpty()) {
                handleEnchantmentMerge(resultSlot, left, leftEnchants, rightEnchants, true, ci);
            }
            return;
        }

        if (!rightEnchants.isEmpty() && right.getItem() == Items.ENCHANTED_BOOK) {
            handleEnchantmentMerge(resultSlot, left, leftEnchants, rightEnchants, false, ci);
        }
    }

    @Unique
    private void handleEnchantmentMerge(Slot resultSlot, ItemStack target, Map<Enchantment, Integer> leftEnchants, Map<Enchantment, Integer> rightEnchants, boolean isSameItemMerge, CallbackInfo ci) {
        if (!Config.allowLevelStacking && !Config.allowVanillaLevelStacking && !Config.allowAnyEnchantment) {
            return;
        }

        Map<Enchantment, Integer> resultEnchants = new HashMap<>(leftEnchants);
        boolean anyEnchantmentApplied = false;
        int totalCost = 0;

        for (Map.Entry<Enchantment, Integer> entry : rightEnchants.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int rightLevel = entry.getValue();
            boolean canApply = isSameItemMerge || Config.allowAnyEnchantment || enchantment.canEnchant(target);
            if (canApply) {
                int leftLevel = resultEnchants.getOrDefault(enchantment, 0);
                int newLevel = calculateNewLevel(leftLevel, rightLevel);
                newLevel = Math.min(newLevel, Config.maxEnchantmentLevel);

                resultEnchants.put(enchantment, newLevel);
                totalCost += newLevel;
                anyEnchantmentApplied = true;
            }
        }

        if (anyEnchantmentApplied) {
            ItemStack result = target.copy();
            EnchantmentHelper.setEnchantments(resultEnchants, result);
            resultSlot.set(result);

            int finalCost = Math.max(1, Math.min(totalCost, 39));
            this.cost.set(finalCost);
            this.repairItemCountCost = 1;

            ci.cancel();
        }
    }

    @Unique
    private int calculateNewLevel(int leftLevel, int rightLevel) {
        if (Config.allowLevelStacking) {
            return leftLevel + rightLevel;
        } else if (Config.allowVanillaLevelStacking && leftLevel == rightLevel) {
            return leftLevel + 1;
        } else {
            return Math.max(leftLevel, rightLevel);
        }
    }
}
