package com.hyygybs.mobutilities.common.item;

import com.google.common.collect.Multimap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LifeSwordItem extends SwordItem implements LifeGearItem {
    public LifeSwordItem(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
        super(tier, attackDamageModifier, attackSpeedModifier, properties);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return isLifeBarVisible(stack);
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
        appendLifeTooltip(stack, tooltip);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return LifeGearAttributes.addMeleeBonuses(super.getAttributeModifiers(slot, stack), slot, EquipmentSlot.MAINHAND, stack, this);
    }
}
