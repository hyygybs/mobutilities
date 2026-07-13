package com.hyygybs.mobutilities.common.registration;

import com.hyygybs.mobutilities.MobUtilities;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MobUtilities.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN = CREATIVE_MODE_TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.mobutilities.main"))
                    .icon(() -> new ItemStack(ModItems.MOB_CONTAINER_EMPTY.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.MOB_CONTAINER_EMPTY.get());
                        output.accept(ModItems.MOB_CONTAINER_FILLED.get());
                        output.accept(ModItems.MATTER.get());
                        output.accept(ModItems.SPIRIT.get());
                        output.accept(ModItems.EXPERIENCE.get());
                        output.accept(ModItems.ELEMENT.get());
                        output.accept(ModItems.ORIGIN.get());
                        output.accept(ModItems.LIFE_INGOT.get());
                        output.accept(ModItems.LIFE_PICKAXE.get());
                        output.accept(ModItems.LIFE_AXE.get());
                        output.accept(ModItems.LIFE_SHOVEL.get());
                        output.accept(ModItems.LIFE_HOE.get());
                        output.accept(ModItems.LIFE_SWORD.get());
                        output.accept(ModItems.LIFE_BOW.get());
                        output.accept(ModItems.LIFE_HELMET.get());
                        output.accept(ModItems.LIFE_CHESTPLATE.get());
                        output.accept(ModItems.LIFE_LEGGINGS.get());
                        output.accept(ModItems.LIFE_BOOTS.get());
                        output.accept(ModItems.SPEED_UPGARDE.get());
                        output.accept(ModItems.SIMULATION_UPGARDE.get());
                        output.accept(ModItems.LOOT_UPGARDE.get());
                        output.accept(ModItems.SPECIAL_UPGARDE.get());
                        output.accept(ModItems.BLOOD_ENERGY_GENERATOR.get());
                        output.accept(ModItems.SOUL_ENERGY_GENERATOR.get());
                        output.accept(ModItems.MOB_FARM.get());
                        output.accept(ModItems.REGENERATOR.get());
                        output.accept(ModItems.RESOLVER.get());
                    })
                    .build());

    private ModCreativeTabs() {
    }
}
