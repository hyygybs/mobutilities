package com.hyygybs.mobutilities.common.util;

import com.hyygybs.mobutilities.MobUtilitiesConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record MobContainerData(
        ResourceLocation entityId,
        String descriptionId,
        Component displayName,
        float health,
        float maxHealth,
        float armor,
        boolean baby,
        boolean persistent,
        int matterValue,
        int spiritValue,
        int experienceValue,
        CompoundTag entityData
) {
    public static final String ROOT_TAG = "MobUtilitiesData";
    private static final String ENTITY_DATA_TAG = "EntityData";

    public static boolean canCapture(LivingEntity entity) {
        if (!(entity instanceof Mob)) {
            return false;
        }
        if (entity instanceof WitherBoss || entity instanceof EnderDragon) {
            return MobUtilitiesConfig.ALLOW_BOSSES.get();
        }
        return true;
    }

    public static MobContainerData fromEntity(LivingEntity entity) {
        EntityType<?> entityType = entity.getType();
        ResourceLocation entityId = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
        CompoundTag entityTag = new CompoundTag();
        entity.saveWithoutId(entityTag);
        entityTag.putString("id", entityId == null ? "minecraft:pig" : entityId.toString());

        int matter = Math.max(1, Mth.ceil(entity.getMaxHealth() / 4.0F));
        boolean noAi = entity instanceof Mob mob && mob.isNoAi();
        int spirit = Math.max(1, noAi ? 1 : Mth.ceil(entity.getMaxHealth() / 6.0F));
        boolean persistent = entity instanceof Mob mob && mob.isPersistenceRequired();
        if (entity instanceof Animal) {
            matter += 1;
        } else {
            spirit += 1;
        }
        int experience = Math.max(1, Mth.ceil((entity.getMaxHealth() + entity.getArmorValue()) / 5.0F));

        return new MobContainerData(
                entityId == null ? ResourceLocation.parse("minecraft:pig") : entityId,
                entity.getType().getDescriptionId(),
                entity.getDisplayName().copy(),
                entity.getHealth(),
                entity.getMaxHealth(),
                entity.getArmorValue(),
                entity.isBaby(),
                persistent,
                matter,
                spirit,
                experience,
                entityTag
        );
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("EntityId", entityId.toString());
        tag.putString("DescriptionId", descriptionId);
        tag.putString("DisplayName", Component.Serializer.toJson(displayName));
        tag.putFloat("Health", health);
        tag.putFloat("MaxHealth", maxHealth);
        tag.putFloat("Armor", armor);
        tag.putBoolean("Baby", baby);
        tag.putBoolean("Persistent", persistent);
        tag.putInt("MatterValue", matterValue);
        tag.putInt("SpiritValue", spiritValue);
        tag.putInt("ExperienceValue", experienceValue);
        tag.put(ENTITY_DATA_TAG, entityData.copy());
        return tag;
    }

    public static Optional<MobContainerData> load(CompoundTag parentTag) {
        if (!parentTag.contains(ROOT_TAG, Tag.TAG_COMPOUND)) {
            return Optional.empty();
        }

        CompoundTag tag = parentTag.getCompound(ROOT_TAG);
        if (!tag.contains("EntityId", Tag.TAG_STRING)) {
            return Optional.empty();
        }

        ResourceLocation entityId = ResourceLocation.parse(tag.getString("EntityId"));
        String descriptionId = tag.getString("DescriptionId");
        Component displayName = readComponent(tag.getString("DisplayName"));
        float health = tag.getFloat("Health");
        float maxHealth = tag.getFloat("MaxHealth");
        float armor = tag.getFloat("Armor");
        boolean baby = tag.getBoolean("Baby");
        boolean persistent = tag.getBoolean("Persistent");
        int matterValue = tag.getInt("MatterValue");
        int spiritValue = tag.getInt("SpiritValue");
        int experienceValue = tag.getInt("ExperienceValue");
        CompoundTag entityData = tag.getCompound(ENTITY_DATA_TAG).copy();

        return Optional.of(new MobContainerData(entityId, descriptionId, displayName, health, maxHealth, armor, baby, persistent,
                matterValue, spiritValue, experienceValue, entityData));
    }

    public void writeTo(ItemStack stack) {
        stack.getOrCreateTag().put(ROOT_TAG, save());
    }

    public static Optional<MobContainerData> fromStack(ItemStack stack) {
        if (!stack.hasTag()) {
            return Optional.empty();
        }
        return load(stack.getTag());
    }

    public List<Component> createTooltip() {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(copyLine("tooltip.mobutilities.container.mob", displayName).withStyle(ChatFormatting.GOLD));
        tooltip.add(copyLine("tooltip.mobutilities.container.health",
                Component.literal(String.format("%.1f / %.1f", health, maxHealth))).withStyle(ChatFormatting.GRAY));
        tooltip.add(copyLine("tooltip.mobutilities.container.armor",
                Component.literal(String.format("%.1f", armor))).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(baby ? "tooltip.mobutilities.container.baby" : "tooltip.mobutilities.container.adult")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(persistent ? "tooltip.mobutilities.container.persistent" : "tooltip.mobutilities.container.normal")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(copyLine("tooltip.mobutilities.container.matter", Component.literal(Integer.toString(matterValue))).withStyle(ChatFormatting.DARK_RED));
        tooltip.add(copyLine("tooltip.mobutilities.container.spirit", Component.literal(Integer.toString(spiritValue))).withStyle(ChatFormatting.AQUA));
        tooltip.add(copyLine("tooltip.mobutilities.container.experience", Component.literal(Integer.toString(experienceValue))).withStyle(ChatFormatting.GREEN));
        return tooltip;
    }

    private static MutableComponent copyLine(String key, Component value) {
        return Component.translatable(key, value);
    }

    private static Component readComponent(String json) {
        if (json == null || json.isBlank()) {
            return Component.empty();
        }
        Component parsed = Component.Serializer.fromJson(json);
        return parsed == null ? Component.empty() : parsed;
    }

    @Nullable
    public Entity createEntity(Level level) {
        CompoundTag tag = entityData.copy();
        if (!tag.contains("id", Tag.TAG_STRING)) {
            tag.putString("id", entityId.toString());
        }
        return EntityType.loadEntityRecursive(tag, level, entity -> entity);
    }
}
