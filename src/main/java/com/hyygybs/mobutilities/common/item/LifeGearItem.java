package com.hyygybs.mobutilities.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface LifeGearItem {
    String LEVEL_TAG = "LifeLevel";
    String EXPERIENCE_TAG = "LifeExperience";

    default int getLifeLevel(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag == null ? 0 : Math.max(0, tag.getInt(LEVEL_TAG));
    }

    default int getStoredExperience(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag == null ? 0 : Math.max(0, tag.getInt(EXPERIENCE_TAG));
    }

    default void addLifeExperience(ItemStack stack, int amount) {
        if (amount <= 0 || stack.isEmpty()) {
            return;
        }

        int level = getLifeLevel(stack);
        int experience = getStoredExperience(stack) + amount;
        while (experience >= getExperienceForNextLevel(level)) {
            experience -= getExperienceForNextLevel(level);
            level++;
        }

        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(LEVEL_TAG, level);
        tag.putInt(EXPERIENCE_TAG, experience);
    }

    default boolean isLifeBarVisible(ItemStack stack) {
        return false;
    }

    default int getLifeBarWidth(ItemStack stack) {
        int needed = getExperienceForNextLevel(getLifeLevel(stack));
        if (needed <= 0) {
            return 13;
        }
        float progress = getStoredExperience(stack) / (float) needed;
        return Mth.clamp(Math.round(progress * 13.0F), 0, 13);
    }

    default int getLifeBarColor(ItemStack stack) {
        return 0x55FF55;
    }

    default void appendLifeTooltip(ItemStack stack, List<Component> tooltip) {
        int level = getLifeLevel(stack);
        int stored = getStoredExperience(stack);
        int needed = getExperienceForNextLevel(level);
        tooltip.add(Component.translatable("tooltip.mobutilities.life_gear.level", level).withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("tooltip.mobutilities.life_gear.experience", stored, needed).withStyle(ChatFormatting.GRAY));
    }

    default double getAttackDamageBonus(ItemStack stack) {
        return getLifeLevel(stack) * 0.5D;
    }

    default double getAttackSpeedBonus(ItemStack stack) {
        return getLifeLevel(stack) * 0.05D;
    }

    default double getProjectileDamageBonus(ItemStack stack) {
        return getLifeLevel(stack) * 0.5D;
    }

    default double getChargeSpeedMultiplier(ItemStack stack) {
        return 1.0D + (getLifeLevel(stack) * 0.05D);
    }

    default double getArmorBonus(ItemStack stack) {
        return getLifeLevel(stack);
    }

    default double getArmorToughnessBonus(ItemStack stack) {
        return getLifeLevel(stack);
    }

    default double getSpellPowerBonus(ItemStack stack) {
        return getLifeLevel(stack) * 0.05D;
    }

    default double getCastTimeReductionBonus(ItemStack stack) {
        return getLifeLevel(stack) * 0.05D;
    }

    default double getMaxManaBonus(ItemStack stack) {
        return getLifeLevel(stack) * 50.0D;
    }

    static int getExperienceForNextLevel(int level) {
        if (level >= 30) {
            return 112 + ((level - 30) * 9);
        }
        if (level >= 15) {
            return 37 + ((level - 15) * 5);
        }
        return 7 + (level * 2);
    }
}
