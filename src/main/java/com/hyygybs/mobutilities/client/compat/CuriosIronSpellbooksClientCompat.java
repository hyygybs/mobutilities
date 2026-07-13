package com.hyygybs.mobutilities.client.compat;

import com.hyygybs.mobutilities.common.registration.ModItems;
import io.redspace.ironsspellbooks.render.SpellBookCurioRenderer;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public final class CuriosIronSpellbooksClientCompat {
    private CuriosIronSpellbooksClientCompat() {
    }

    public static void registerLifeSpellbookRenderer() {
        CuriosRendererRegistry.register(ModItems.LIFE_SPELLBOOK.get(), SpellBookCurioRenderer::new);
    }
}
