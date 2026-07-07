package com.hyygybs.mobutilities.common.registration;

import com.hyygybs.mobutilities.MobUtilities;
import com.hyygybs.mobutilities.common.blockentity.MachineBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MobUtilities.MOD_ID);

    public static final RegistryObject<BlockEntityType<MachineBlockEntities.BloodGeneratorBlockEntity>> BLOOD_GENERATOR =
            BLOCK_ENTITY_TYPES.register("blood_energy_generator",
                    () -> BlockEntityType.Builder.of(MachineBlockEntities.BloodGeneratorBlockEntity::new, ModBlocks.BLOOD_ENERGY_GENERATOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<MachineBlockEntities.SoulGeneratorBlockEntity>> SOUL_GENERATOR =
            BLOCK_ENTITY_TYPES.register("soul_energy_generator",
                    () -> BlockEntityType.Builder.of(MachineBlockEntities.SoulGeneratorBlockEntity::new, ModBlocks.SOUL_ENERGY_GENERATOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<MachineBlockEntities.MobFarmBlockEntity>> MOB_FARM =
            BLOCK_ENTITY_TYPES.register("mob_farm",
                    () -> BlockEntityType.Builder.of(MachineBlockEntities.MobFarmBlockEntity::new, ModBlocks.MOB_FARM.get()).build(null));
    public static final RegistryObject<BlockEntityType<MachineBlockEntities.RegeneratorBlockEntity>> REGENERATOR =
            BLOCK_ENTITY_TYPES.register("regenerator",
                    () -> BlockEntityType.Builder.of(MachineBlockEntities.RegeneratorBlockEntity::new, ModBlocks.REGENERATOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<MachineBlockEntities.ResolverBlockEntity>> RESOLVER =
            BLOCK_ENTITY_TYPES.register("resolver",
                    () -> BlockEntityType.Builder.of(MachineBlockEntities.ResolverBlockEntity::new, ModBlocks.RESOLVER.get()).build(null));

    private ModBlockEntities() {
    }
}
