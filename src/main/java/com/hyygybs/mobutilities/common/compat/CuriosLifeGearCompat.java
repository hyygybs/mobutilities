package com.hyygybs.mobutilities.common.compat;

import com.hyygybs.mobutilities.common.item.LifeGearItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

public final class CuriosLifeGearCompat {
    private CuriosLifeGearCompat() {
    }

    public static void grantLifeGearExperience(Player player, int gained) {
        if (gained <= 0) {
            return;
        }

        CuriosApi.getCuriosInventory(player).ifPresent(curiosInventory ->
                curiosInventory.getCurios().values().forEach(handler -> grantLifeGearExperience(handler.getStacks(), gained)));
    }

    private static void grantLifeGearExperience(IDynamicStackHandler stackHandler, int gained) {
        for (int slot = 0; slot < stackHandler.getSlots(); slot++) {
            ItemStack stack = stackHandler.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof LifeGearItem lifeGearItem) {
                lifeGearItem.addLifeExperience(stack, gained);
            }
        }
    }
}
