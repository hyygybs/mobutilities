package com.hyygybs.mobutilities.common.util;

import com.hyygybs.mobutilities.MobUtilities;
import com.hyygybs.mobutilities.MobUtilitiesConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class MobLootHelper {
    private static final UUID FAKE_PLAYER_UUID = UUID.fromString("f7db4fd1-6bc4-4d0e-b682-4315d52fc2fb");
    private static final Set<ResourceLocation> LOGGED_RANDOM_MOB_FAILURES = new HashSet<>();

    private MobLootHelper() {
    }

    public static Optional<MobContainerData> createRandomMob(ServerLevel level) {
        List<MobContainerData> candidates = ForgeRegistries.ENTITY_TYPES.getValues().stream()
                .filter(EntityType::canSummon)
                .map(type -> createRandomMobCandidate(level, type))
                .flatMap(Optional::stream)
                .toList();

        if (candidates.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(candidates.get(level.random.nextInt(candidates.size())));
    }

    private static Optional<MobContainerData> createRandomMobCandidate(ServerLevel level, EntityType<?> type) {
        ResourceLocation entityId = ForgeRegistries.ENTITY_TYPES.getKey(type);
        try {
            Entity entity = type.create(level);
            if (!(entity instanceof LivingEntity livingEntity) || !(livingEntity instanceof Mob) || !MobContainerData.canCapture(livingEntity)) {
                return Optional.empty();
            }
            if (!hasEntityLootTable(level, livingEntity)) {
                return Optional.empty();
            }
            return Optional.of(MobContainerData.fromEntity(livingEntity));
        } catch (Throwable throwable) {
            if (entityId != null && LOGGED_RANDOM_MOB_FAILURES.add(entityId)) {
                MobUtilities.LOGGER.warn("Skipping invalid regenerator candidate entity type {}", entityId, throwable);
            }
            return Optional.empty();
        }
    }

    private static boolean hasEntityLootTable(ServerLevel level, LivingEntity livingEntity) {
        ResourceLocation lootTableId = livingEntity.getLootTable();
        if (lootTableId == null || lootTableId.getPath().isBlank()) {
            return false;
        }

        ResourceLocation lootTablePath = ResourceLocation.fromNamespaceAndPath(
                lootTableId.getNamespace(),
                "loot_tables/" + lootTableId.getPath() + ".json"
        );
        return level.getServer().getResourceManager().getResource(lootTablePath).isPresent();
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
                .withParameter(LootContextParams.ORIGIN, livingEntity.position());

        if (playerKill) {
            FakePlayer fakePlayer = FakePlayerFactory.get(level, new com.mojang.authlib.GameProfile(FAKE_PLAYER_UUID, "[MobUtilities]"));
            builder.withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().playerAttack(fakePlayer));
            builder.withOptionalParameter(LootContextParams.KILLER_ENTITY, fakePlayer);
            builder.withOptionalParameter(LootContextParams.LAST_DAMAGE_PLAYER, fakePlayer);
            builder.withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, fakePlayer);
        } else {
            builder.withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().generic());
        }

        ResourceLocation baseTableId = livingEntity.getLootTable();
        LootTable lootTable = LootTable.EMPTY;
        if (baseTableId != null) {
            ResourceLocation overrideTableId = ResourceLocation.fromNamespaceAndPath(
                    MobUtilities.MOD_ID,
                    "mob_farm/" + baseTableId.getNamespace() + "/" + baseTableId.getPath()
            );
            LootTable override = level.getServer().getLootData().getLootTable(overrideTableId);
            lootTable = override == LootTable.EMPTY ? level.getServer().getLootData().getLootTable(baseTableId) : override;
        }

        List<ItemStack> drops = new ArrayList<>(lootTable.getRandomItems(builder.create(LootContextParamSets.ENTITY)));

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
