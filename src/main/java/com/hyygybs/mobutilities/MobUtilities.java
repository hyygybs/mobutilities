package com.hyygybs.mobutilities;

import com.hyygybs.mobutilities.client.ClientSetup;
import com.hyygybs.mobutilities.common.registration.ModBlockEntities;
import com.hyygybs.mobutilities.common.registration.ModBlocks;
import com.hyygybs.mobutilities.common.registration.ModCreativeTabs;
import com.hyygybs.mobutilities.common.registration.ModItems;
import com.hyygybs.mobutilities.common.registration.ModMenus;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(MobUtilities.MOD_ID)
public class MobUtilities {
    public static final String MOD_ID = "mobutilities";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MobUtilities(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);

        context.registerConfig(ModConfig.Type.COMMON, MobUtilitiesConfig.SPEC);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientSetup.init(modEventBus));
        MinecraftForge.EVENT_BUS.register(this);
    }
}
