package com.hyygybs.mobutilities.common.registration;

import com.hyygybs.mobutilities.MobUtilities;
import com.hyygybs.mobutilities.common.menu.MachineMenu;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.inventory.MenuType;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MobUtilities.MOD_ID);

    public static final RegistryObject<MenuType<MachineMenu>> BLOOD_GENERATOR = MENUS.register("blood_energy_generator",
            () -> IForgeMenuType.create(MachineMenu.BloodGeneratorMenu::new));
    public static final RegistryObject<MenuType<MachineMenu>> SOUL_GENERATOR = MENUS.register("soul_energy_generator",
            () -> IForgeMenuType.create(MachineMenu.SoulGeneratorMenu::new));
    public static final RegistryObject<MenuType<MachineMenu>> RESOLVER = MENUS.register("resolver",
            () -> IForgeMenuType.create(MachineMenu.ResolverMenu::new));
    public static final RegistryObject<MenuType<MachineMenu>> REGENERATOR = MENUS.register("regenerator",
            () -> IForgeMenuType.create(MachineMenu.RegeneratorMenu::new));
    public static final RegistryObject<MenuType<MachineMenu>> MOB_FARM = MENUS.register("mob_farm",
            () -> IForgeMenuType.create(MachineMenu.MobFarmMenu::new));

    private ModMenus() {
    }
}
