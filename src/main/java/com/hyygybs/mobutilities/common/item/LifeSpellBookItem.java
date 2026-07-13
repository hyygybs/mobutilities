package com.hyygybs.mobutilities.common.item;

import com.google.common.collect.Multimap;
import com.hyygybs.mobutilities.common.compat.ironsspellbooks.LifeSpellbookAttributesCompat;
import io.redspace.ironsspellbooks.item.SpellBook;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class LifeSpellBookItem extends SpellBook implements LifeGearItem {
    public LifeSpellBookItem(Properties properties) {
        super(10, properties);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return false;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return getLifeBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return getLifeBarColor(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        appendLifeTooltip(stack, tooltip);
        tooltip.add(Component.translatable(
                "tooltip.mobutilities.life_spellbook.spell_power",
                String.format(Locale.ROOT, "%.2f", getSpellPowerBonus(stack))
        ).withStyle(ChatFormatting.DARK_AQUA));
        tooltip.add(Component.translatable(
                "tooltip.mobutilities.life_spellbook.cast_time_reduction",
                String.format(Locale.ROOT, "%.2f", getCastTimeReductionBonus(stack))
        ).withStyle(ChatFormatting.DARK_AQUA));
        tooltip.add(Component.translatable(
                "tooltip.mobutilities.life_spellbook.max_mana",
                String.format(Locale.ROOT, "%.0f", getMaxManaBonus(stack))
        ).withStyle(ChatFormatting.DARK_AQUA));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        return LifeSpellbookAttributesCompat.addSpellbookBonuses(super.getAttributeModifiers(slotContext, uuid, stack), stack, this);
    }
}
