package com.hyygybs.mobutilities.common.registration;

import com.hyygybs.mobutilities.MobUtilities;
import com.hyygybs.mobutilities.common.block.MachineBlock;
import com.hyygybs.mobutilities.common.blockentity.MachineBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MobUtilities.MOD_ID);

    public static final RegistryObject<Block> BLOOD_ENERGY_GENERATOR = BLOCKS.register("blood_energy_generator",
            () -> new MachineBlock(commonProperties(), MachineBlockEntities.BloodGeneratorBlockEntity::new));
    public static final RegistryObject<Block> SOUL_ENERGY_GENERATOR = BLOCKS.register("soul_energy_generator",
            () -> new MachineBlock(commonProperties(), MachineBlockEntities.SoulGeneratorBlockEntity::new));
    public static final RegistryObject<Block> MOB_FARM = BLOCKS.register("mob_farm",
            () -> new MachineBlock(commonProperties(), MachineBlockEntities.MobFarmBlockEntity::new));
    public static final RegistryObject<Block> REGENERATOR = BLOCKS.register("regenerator",
            () -> new MachineBlock(commonProperties(), MachineBlockEntities.RegeneratorBlockEntity::new));
    public static final RegistryObject<Block> RESOLVER = BLOCKS.register("resolver",
            () -> new MachineBlock(commonProperties(), MachineBlockEntities.ResolverBlockEntity::new));

    private ModBlocks() {
    }

    private static BlockBehaviour.Properties commonProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(3.5F, 6.0F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops();
    }
}
