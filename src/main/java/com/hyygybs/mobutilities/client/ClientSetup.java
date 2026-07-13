package com.hyygybs.mobutilities.client;

import com.hyygybs.mobutilities.client.compat.CuriosIronSpellbooksClientCompat;
import com.hyygybs.mobutilities.client.screen.MachineScreen;
import com.hyygybs.mobutilities.common.registration.ModItems;
import com.hyygybs.mobutilities.common.registration.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public final class ClientSetup {
    private ClientSetup() {
    }

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(ClientSetup::onClientSetup);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.BLOOD_GENERATOR.get(), MachineScreen::new);
            MenuScreens.register(ModMenus.SOUL_GENERATOR.get(), MachineScreen::new);
            MenuScreens.register(ModMenus.RESOLVER.get(), MachineScreen::new);
            MenuScreens.register(ModMenus.REGENERATOR.get(), MachineScreen::new);
            MenuScreens.register(ModMenus.MOB_FARM.get(), MachineScreen::new);
            if (ModItems.LIFE_SPELLBOOK_COMPAT_ENABLED) {
                CuriosIronSpellbooksClientCompat.registerLifeSpellbookRenderer();
            }
        });
    }
}
