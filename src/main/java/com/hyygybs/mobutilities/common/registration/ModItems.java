package com.hyygybs.mobutilities.common.registration;

import com.hyygybs.mobutilities.MobUtilities;
import com.hyygybs.mobutilities.common.item.MobContainerItem;
import com.hyygybs.mobutilities.common.item.TooltipBlockItem;
import com.hyygybs.mobutilities.common.item.TooltipItem;
import com.hyygybs.mobutilities.common.item.UpgradeItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MobUtilities.MOD_ID);

    public static final RegistryObject<Item> MATTER = ITEMS.register("matter",
            () -> new TooltipItem(new Item.Properties(), "tooltip.mobutilities.item.matter", 2));
    public static final RegistryObject<Item> SPIRIT = ITEMS.register("spirit",
            () -> new TooltipItem(new Item.Properties(), "tooltip.mobutilities.item.spirit", 2));
    public static final RegistryObject<Item> EXPERIENCE = ITEMS.register("experience",
            () -> new TooltipItem(new Item.Properties(), "tooltip.mobutilities.item.experience", 2));

    public static final RegistryObject<Item> MOB_CONTAINER_EMPTY = ITEMS.register("mob_container_empty",
            () -> new MobContainerItem(false, new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> MOB_CONTAINER_FILLED = ITEMS.register("mob_container_filled",
            () -> new MobContainerItem(true, new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SPEED_UPGARDE = ITEMS.register("speed_upgarde",
            () -> new UpgradeItem(UpgradeItem.Type.SPEED, new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> SIMULATION_UPGARDE = ITEMS.register("simulation_upgarde",
            () -> new UpgradeItem(UpgradeItem.Type.SIMULATION, new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> LOOT_UPGARDE = ITEMS.register("loot_upgarde",
            () -> new UpgradeItem(UpgradeItem.Type.LOOTING, new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> SPECIAL_UPGARDE = ITEMS.register("special_upgarde",
            () -> new UpgradeItem(UpgradeItem.Type.SPECIAL, new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> BLOOD_ENERGY_GENERATOR = ITEMS.register("blood_energy_generator",
            () -> new TooltipBlockItem(ModBlocks.BLOOD_ENERGY_GENERATOR.get(), new Item.Properties(),
                    "tooltip.mobutilities.machine.blood_energy_generator", 2));
    public static final RegistryObject<Item> SOUL_ENERGY_GENERATOR = ITEMS.register("soul_energy_generator",
            () -> new TooltipBlockItem(ModBlocks.SOUL_ENERGY_GENERATOR.get(), new Item.Properties(),
                    "tooltip.mobutilities.machine.soul_energy_generator", 2));
    public static final RegistryObject<Item> MOB_FARM = ITEMS.register("mob_farm",
            () -> new TooltipBlockItem(ModBlocks.MOB_FARM.get(), new Item.Properties(),
                    "tooltip.mobutilities.machine.mob_farm", 2));
    public static final RegistryObject<Item> REGENERATOR = ITEMS.register("regenerator",
            () -> new TooltipBlockItem(ModBlocks.REGENERATOR.get(), new Item.Properties(),
                    "tooltip.mobutilities.machine.regenerator", 2));
    public static final RegistryObject<Item> RESOLVER = ITEMS.register("resolver",
            () -> new TooltipBlockItem(ModBlocks.RESOLVER.get(), new Item.Properties(),
                    "tooltip.mobutilities.machine.resolver", 2));

    private ModItems() {
    }
}
