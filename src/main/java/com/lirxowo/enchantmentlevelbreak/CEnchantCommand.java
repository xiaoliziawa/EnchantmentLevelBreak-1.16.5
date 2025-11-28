package com.lirxowo.enchantmentlevelbreak;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import java.util.Map;

public class CEnchantCommand {
    private static final SuggestionProvider<CommandSource> SUGGEST_ENCHANTMENTS = (context, builder) ->
            ISuggestionProvider.suggestResource(Registry.ENCHANTMENT.keySet(), builder);

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("cenchant")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("enchantment", ResourceLocationArgument.id())
                        .suggests(SUGGEST_ENCHANTMENTS)
                        .executes(context -> enchantItem(context.getSource(),
                                Registry.ENCHANTMENT.get(ResourceLocationArgument.getId(context, "enchantment")),
                                1))
                        .then(Commands.argument("level", IntegerArgumentType.integer(1))
                                .executes(context -> enchantItem(context.getSource(),
                                        Registry.ENCHANTMENT.get(ResourceLocationArgument.getId(context, "enchantment")),
                                        IntegerArgumentType.getInteger(context, "level"))))));
    }

    private static int enchantItem(CommandSource source, Enchantment enchantment, int level) throws CommandSyntaxException {
        PlayerEntity player = source.getPlayerOrException();
        ItemStack itemStack = player.getMainHandItem();

        if (itemStack.isEmpty()) {
            source.sendFailure(new StringTextComponent("You must be holding an item to enchant"));
            return 0;
        }

        if (enchantment == null) {
            source.sendFailure(new StringTextComponent("Invalid enchantment"));
            return 0;
        }

        Map<Enchantment, Integer> currentEnchantments = EnchantmentHelper.getEnchantments(itemStack);

        currentEnchantments.remove(enchantment);

        currentEnchantments.put(enchantment, level);

        EnchantmentHelper.setEnchantments(currentEnchantments, itemStack);

        source.sendSuccess(new StringTextComponent("Applied " + enchantment.getFullname(level).getString() + " to the item"), true);

        return 1;
    }
}
