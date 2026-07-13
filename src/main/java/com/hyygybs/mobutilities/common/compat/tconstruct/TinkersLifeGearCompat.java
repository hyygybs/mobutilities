package com.hyygybs.mobutilities.common.compat.tconstruct;

import com.hyygybs.mobutilities.common.item.LifeGearItem;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public final class TinkersLifeGearCompat {
    private TinkersLifeGearCompat() {
    }

    public static void grantLifeGearExperience(Player player, int gained) {
        if (gained <= 0) {
            return;
        }

        grantLifeGearExperience(player.getMainHandItem(), gained);
        grantLifeGearExperience(player.getOffhandItem(), gained);
        for (ItemStack armorStack : player.getArmorSlots()) {
            grantLifeGearExperience(armorStack, gained);
        }
        grantHotbarLifeGearExperience(player, gained);
    }

    public static void grantLifeGearExperience(ItemStack stack, int gained) {
        if (gained <= 0 || stack.isEmpty() || !(stack.getItem() instanceof IModifiable)) {
            return;
        }

        ToolStack tool = ToolStack.from(stack);
        if (tool.getModifierLevel(TinkersConstructCompat.GROWABLE_ID) <= 0) {
            return;
        }

        int level = GrowableModifier.getLifeLevel(tool);
        int experience = GrowableModifier.getStoredExperience(tool) + gained;
        while (experience >= LifeGearItem.getExperienceForNextLevel(level)) {
            experience -= LifeGearItem.getExperienceForNextLevel(level);
            level++;
        }

        ModDataNBT persistentData = tool.getPersistentData();
        persistentData.putInt(GrowableModifier.LEVEL_KEY, level);
        persistentData.putInt(GrowableModifier.EXPERIENCE_KEY, experience);
        tool.rebuildStats();
        tool.updateStack(stack);
    }

    private static void grantHotbarLifeGearExperience(Player player, int gained) {
        int selectedSlot = player.getInventory().selected;
        for (int slot = 0; slot < Inventory.getSelectionSize(); slot++) {
            if (slot == selectedSlot) {
                continue;
            }
            grantLifeGearExperience(player.getInventory().getItem(slot), gained);
        }
    }
}
