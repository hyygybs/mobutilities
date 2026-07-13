package com.hyygybs.mobutilities.common.compat.tconstruct;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.List;

public final class TinkersGrowableTooltipCompat {
    private static final String TOOLTIP_LEVEL_KEY = "tooltip.mobutilities.life_gear.level";
    private static final String TOOLTIP_EXPERIENCE_KEY = "tooltip.mobutilities.life_gear.experience";

    private TinkersGrowableTooltipCompat() {
    }

    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || !ToolStack.isInitialized(stack)) {
            return;
        }

        ToolStack tool = ToolStack.from(stack);
        if (tool.getModifierLevel(TinkersConstructCompat.GROWABLE_ID) <= 0) {
            return;
        }

        List<Component> tooltip = event.getToolTip();
        if (containsKey(tooltip, TOOLTIP_LEVEL_KEY) || containsKey(tooltip, TOOLTIP_EXPERIENCE_KEY)) {
            return;
        }

        int level = GrowableModifier.getLifeLevel(tool);
        int stored = GrowableModifier.getStoredExperience(tool);
        int needed = com.hyygybs.mobutilities.common.item.LifeGearItem.getExperienceForNextLevel(level);
        tooltip.add(Component.translatable(TOOLTIP_LEVEL_KEY, level));
        tooltip.add(Component.translatable(TOOLTIP_EXPERIENCE_KEY, stored, needed));
    }

    private static boolean containsKey(List<Component> tooltip, String key) {
        for (Component component : tooltip) {
            if (component.getContents() instanceof TranslatableContents translatable && key.equals(translatable.getKey())) {
                return true;
            }
        }
        return false;
    }
}
