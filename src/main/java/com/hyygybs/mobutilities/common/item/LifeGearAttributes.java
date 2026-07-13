package com.hyygybs.mobutilities.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public final class LifeGearAttributes {
    private static final UUID LIFE_ATTACK_DAMAGE_UUID = UUID.fromString("d0634d7c-cc2b-4f3d-aa18-443bf77c3116");
    private static final UUID LIFE_ATTACK_SPEED_UUID = UUID.fromString("5c6350df-a802-4a7e-b6c2-9701d2e2d6d1");
    private static final UUID LIFE_ARMOR_UUID = UUID.fromString("ed8c9c79-503a-4235-b6bd-e4c7411d7956");
    private static final UUID LIFE_ARMOR_TOUGHNESS_UUID = UUID.fromString("c4e74527-7d37-42eb-a87f-527b53f74da9");

    private LifeGearAttributes() {
    }

    public static Multimap<Attribute, AttributeModifier> addMeleeBonuses(
            Multimap<Attribute, AttributeModifier> baseModifiers,
            EquipmentSlot requestedSlot,
            EquipmentSlot targetSlot,
            ItemStack stack,
            LifeGearItem lifeGearItem
    ) {
        if (requestedSlot != targetSlot) {
            return baseModifiers;
        }

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(baseModifiers);
        double attackDamageBonus = lifeGearItem.getAttackDamageBonus(stack);
        double attackSpeedBonus = lifeGearItem.getAttackSpeedBonus(stack);
        if (attackDamageBonus > 0.0D) {
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                    LIFE_ATTACK_DAMAGE_UUID,
                    "Life gear attack damage bonus",
                    attackDamageBonus,
                    AttributeModifier.Operation.ADDITION
            ));
        }
        if (attackSpeedBonus > 0.0D) {
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(
                    LIFE_ATTACK_SPEED_UUID,
                    "Life gear attack speed bonus",
                    attackSpeedBonus,
                    AttributeModifier.Operation.ADDITION
            ));
        }
        return builder.build();
    }

    public static Multimap<Attribute, AttributeModifier> addArmorBonuses(
            Multimap<Attribute, AttributeModifier> baseModifiers,
            EquipmentSlot requestedSlot,
            EquipmentSlot targetSlot,
            ItemStack stack,
            LifeGearItem lifeGearItem
    ) {
        if (requestedSlot != targetSlot) {
            return baseModifiers;
        }

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(baseModifiers);
        double armorBonus = lifeGearItem.getArmorBonus(stack);
        double toughnessBonus = lifeGearItem.getArmorToughnessBonus(stack);
        if (armorBonus > 0.0D) {
            builder.put(Attributes.ARMOR, new AttributeModifier(
                    LIFE_ARMOR_UUID,
                    "Life gear armor bonus",
                    armorBonus,
                    AttributeModifier.Operation.ADDITION
            ));
        }
        if (toughnessBonus > 0.0D) {
            builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(
                    LIFE_ARMOR_TOUGHNESS_UUID,
                    "Life gear armor toughness bonus",
                    toughnessBonus,
                    AttributeModifier.Operation.ADDITION
            ));
        }
        return builder.build();
    }
}
