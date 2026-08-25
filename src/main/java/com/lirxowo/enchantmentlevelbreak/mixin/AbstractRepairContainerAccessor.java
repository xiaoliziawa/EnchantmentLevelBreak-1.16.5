package com.lirxowo.enchantmentlevelbreak.mixin;

import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.AbstractRepairContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractRepairContainer.class)
public interface AbstractRepairContainerAccessor {
    @Accessor("inputSlots")
    IInventory enchantmentLevelBreak$getInputSlots();

    @Accessor("resultSlots")
    CraftResultInventory enchantmentLevelBreak$getResultSlots();
}
