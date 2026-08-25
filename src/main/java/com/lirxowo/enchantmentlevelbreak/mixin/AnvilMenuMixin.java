package com.lirxowo.enchantmentlevelbreak.mixin;

import com.lirxowo.enchantmentlevelbreak.config.Config;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IntReferenceHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(RepairContainer.class)
public abstract class AnvilMenuMixin {
    @Unique
    private static final int MAX_ANVIL_COST = 39;

    @Shadow
    @Final
    private IntReferenceHolder cost;

    @Inject(method = "createResult", at = @At("RETURN"))
    private void onCreateResult(CallbackInfo ci) {
        AbstractRepairContainerAccessor accessor = (AbstractRepairContainerAccessor) this;
        IInventory inputSlots = accessor.enchantmentLevelBreak$getInputSlots();
        ItemStack left = inputSlots.getItem(0);
        ItemStack right = inputSlots.getItem(1);

        if (!left.isEmpty() && !right.isEmpty()) {
            handleAnvilOperation(accessor, left, right);
        }
    }

    @Unique
    private void handleAnvilOperation(AbstractRepairContainerAccessor accessor, ItemStack left, ItemStack right) {
        Map<Enchantment, Integer> rightEnchants = EnchantmentHelper.getEnchantments(right);
        if (rightEnchants.isEmpty()) {
            return;
        }

        boolean sameItem = left.getItem() == right.getItem();
        if (!sameItem && right.getItem() != Items.ENCHANTED_BOOK) {
            return;
        }

        ItemStack result = accessor.enchantmentLevelBreak$getResultSlots().getItem(0);
        if (result.isEmpty()) {
            result = left.copy();
        }

        Map<Enchantment, Integer> leftEnchants = EnchantmentHelper.getEnchantments(left);
        Map<Enchantment, Integer> merged = new LinkedHashMap<>(leftEnchants);
        boolean anyApplied = false;
        long totalCost = 0L;

        for (Map.Entry<Enchantment, Integer> entry : rightEnchants.entrySet()) {
            Enchantment enchantment = entry.getKey();
            boolean canApply = sameItem || Config.allowAnyEnchantment || enchantment.canEnchant(left);
            if (!canApply) {
                continue;
            }
            int newLevel = calculateNewLevel(leftEnchants.getOrDefault(enchantment, 0), entry.getValue());
            merged.put(enchantment, newLevel);
            totalCost += newLevel;
            anyApplied = true;
        }

        if (!anyApplied) {
            return;
        }

        EnchantmentHelper.setEnchantments(merged, result);
        accessor.enchantmentLevelBreak$getResultSlots().setItem(0, result);
        this.cost.set((int) Math.max(1L, Math.min(totalCost, MAX_ANVIL_COST)));
        ((RepairContainer) (Object) this).broadcastChanges();
    }

    @Unique
    private int calculateNewLevel(int leftLevel, int rightLevel) {
        long newLevel;
        if (Config.allowLevelStacking) {
            newLevel = (long) leftLevel + rightLevel;
        } else if (Config.allowVanillaLevelStacking && leftLevel == rightLevel) {
            newLevel = (long) leftLevel + 1;
        } else {
            newLevel = Math.max(leftLevel, rightLevel);
        }
        return (int) Math.min(newLevel, Config.maxEnchantmentLevel);
    }
}
