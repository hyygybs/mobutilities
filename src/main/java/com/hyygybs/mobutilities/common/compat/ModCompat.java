package com.hyygybs.mobutilities.common.compat;

import net.minecraftforge.fml.ModList;

public final class ModCompat {
    public static final String CURIOS_MOD_ID = "curios";
    public static final String IRONS_SPELLBOOKS_MOD_ID = "irons_spellbooks";
    public static final String CONSTRUCTS_CASTING_MOD_ID = "constructs_casting";
    public static final String TINKERS_CONSTRUCT_MOD_ID = "tconstruct";

    private ModCompat() {
    }

    public static boolean isLifeSpellbookCompatEnabled() {
        return ModList.get().isLoaded(CURIOS_MOD_ID) && ModList.get().isLoaded(IRONS_SPELLBOOKS_MOD_ID);
    }

    public static boolean isTinkersConstructCompatEnabled() {
        return ModList.get().isLoaded(TINKERS_CONSTRUCT_MOD_ID);
    }

    public static boolean isConstructsCastingCompatEnabled() {
        return ModList.get().isLoaded(TINKERS_CONSTRUCT_MOD_ID)
                && ModList.get().isLoaded(CONSTRUCTS_CASTING_MOD_ID)
                && ModList.get().isLoaded(IRONS_SPELLBOOKS_MOD_ID);
    }
}
