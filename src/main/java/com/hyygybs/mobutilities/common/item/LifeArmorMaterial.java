package com.hyygybs.mobutilities.common.item;

import com.hyygybs.mobutilities.MobUtilities;
import com.hyygybs.mobutilities.common.registration.ModItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public enum LifeArmorMaterial implements ArmorMaterial {
    INSTANCE;

    private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};
    private static final int[] DEFENSE = new int[]{3, 6, 8, 3};

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return BASE_DURABILITY[indexOf(type)] * 33;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return DEFENSE[indexOf(type)];
    }

    @Override
    public int getEnchantmentValue() {
        return 10;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_DIAMOND;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(ModItems.LIFE_INGOT.get());
    }

    @Override
    public String getName() {
        return MobUtilities.MOD_ID + ":life";
    }

    @Override
    public float getToughness() {
        return 2.0F;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.0F;
    }

    private static int indexOf(ArmorItem.Type type) {
        return switch (type) {
            case BOOTS -> 0;
            case LEGGINGS -> 1;
            case CHESTPLATE -> 2;
            case HELMET -> 3;
        };
    }
}
