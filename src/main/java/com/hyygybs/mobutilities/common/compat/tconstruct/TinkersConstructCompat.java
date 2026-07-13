package com.hyygybs.mobutilities.common.compat.tconstruct;

import com.hyygybs.mobutilities.MobUtilities;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;

public final class TinkersConstructCompat {
    public static final ModifierId GROWABLE_ID = new ModifierId(MobUtilities.MOD_ID, "growable");
    private static final ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(MobUtilities.MOD_ID);
    public static final StaticModifier<GrowableModifier> GROWABLE = MODIFIERS.register("growable", GrowableModifier::new);

    private TinkersConstructCompat() {
    }

    public static void init(IEventBus eventBus) {
        MODIFIERS.register(eventBus);
        MinecraftForge.EVENT_BUS.addListener(TinkersGrowableTooltipCompat::onItemTooltip);
    }
}
