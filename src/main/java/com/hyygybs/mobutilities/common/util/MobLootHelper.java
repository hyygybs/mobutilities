package com.hyygybs.mobutilities.common.util;

import com.hyygybs.mobutilities.MobUtilitiesConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class MobLootHelper {
    private static final UUID FAKE_PLAYER_UUID = UUID.fromString("f7db4fd1-6bc4-4d0e-b682-4315d52fc2fb");

    private MobLootHelper() {
    }

    public static Optional<MobContainerData> createRandomMob(ServerLevel level) {
        List<EntityType<?>> candidates = ForgeRegistries.ENTITY_TYPES.getValues().stream()
                .filter(EntityType::canSummon)
                .filter(type -> type.create(level) instanceof Mob mob && MobContainerData.canCapture(mob))
                .toList();

        if (candidates.isEmpty()) {
            return Optional.empty();
        }

        EntityType<?> entityType = candidates.get(level.random.nextInt(candidates.size()));
        Entity entity = entityType.create(level);
        if (!(entity instanceof LivingEntity livingEntity)) {
            return Optional.empty();
        }

        return Optional.of(MobContainerData.fromEntity(livingEntity));
    }

    public static List<ItemStack> generateLoot(ServerLevel level, MobContainerData data, boolean playerKill, int lootingUpgrades) {
        Entity entity = data.createEntity(level);
        if (!(entity instanceof LivingEntity livingEntity)) {
            return List.of();
        }

        livingEntity.setHealth(livingEntity.getMaxHealth());
        livingEntity.moveTo(0.0D, -9999.0D, 0.0D);

        LootParams.Builder builder = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, livingEntity)
                .withParameter(LootContextParams.ORIGIN, livingEntity.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().generic());

        if (playerKill) {
            FakePlayer fakePlayer = FakePlayerFactory.get(level, new com.mojang.authlib.GameProfile(FAKE_PLAYER_UUID, "[MobUtilities]"));
            builder.withOptionalParameter(LootContextParams.KILLER_ENTITY, fakePlayer);
            builder.withOptionalParameter(LootContextParams.LAST_DAMAGE_PLAYER, fakePlayer);
            builder.withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, fakePlayer);
        }

        LootTable lootTable = level.getServer().getLootData().getLootTable(livingEntity.getLootTable());
        List<ItemStack> drops = new ArrayList<>(lootTable.getRandomItems(builder.create(LootContextParamSets.ENTITY)));
        if (drops.isEmpty()) {
            ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(livingEntity.getType());
            if (key != null) {
                ResourceLocation eggId = ResourceLocation.fromNamespaceAndPath(key.getNamespace(), key.getPath() + "_spawn_egg");
                ItemStack spawnEggLike = ForgeRegistries.ITEMS.getValue(eggId) == null
                        ? ItemStack.EMPTY
                        : new ItemStack(ForgeRegistries.ITEMS.getValue(eggId));
                if (!spawnEggLike.isEmpty()) {
                    drops.add(spawnEggLike);
                }
            }
        }

        if (lootingUpgrades > 0) {
            RandomSource random = level.random;
            List<ItemStack> bonus = new ArrayList<>();
            for (ItemStack drop : drops) {
                if (drop.isEmpty()) {
                    continue;
                }
                int extra = 0;
                for (int i = 0; i < lootingUpgrades; i++) {
                    if (random.nextFloat() < 0.35F) {
                        extra++;
                    }
                }
                if (extra > 0) {
                    ItemStack extraStack = drop.copy();
                    extraStack.setCount(Math.min(extra, extraStack.getMaxStackSize()));
                    bonus.add(extraStack);
                }
            }
            drops.addAll(bonus);
        }

        return drops;
    }
}
