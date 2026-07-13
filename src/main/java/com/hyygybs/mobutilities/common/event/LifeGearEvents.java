package com.hyygybs.mobutilities.common.event;

import com.hyygybs.mobutilities.MobUtilities;
import com.hyygybs.mobutilities.common.compat.CuriosLifeGearCompat;
import com.hyygybs.mobutilities.common.compat.ModCompat;
import com.hyygybs.mobutilities.common.compat.tconstruct.TinkersLifeGearCompat;
import com.hyygybs.mobutilities.common.item.LifeGearItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MobUtilities.MOD_ID)
public final class LifeGearEvents {
    private static final String TRACKED_EXPERIENCE_TAG = "TrackedPlayerTotalExperience";

    private LifeGearEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }

        Player player = event.player;
        CompoundTag persistentData = player.getPersistentData();
        CompoundTag mobUtilitiesData = persistentData.getCompound(Player.PERSISTED_NBT_TAG);
        int currentTotalExperience = player.totalExperience;
        int trackedExperience = mobUtilitiesData.getInt(TRACKED_EXPERIENCE_TAG);

        if (currentTotalExperience > trackedExperience) {
            int gained = currentTotalExperience - trackedExperience;
            grantLifeGearExperience(player.getMainHandItem(), gained);
            grantLifeGearExperience(player.getOffhandItem(), gained);
            for (ItemStack armorStack : player.getArmorSlots()) {
                grantLifeGearExperience(armorStack, gained);
            }
            grantHotbarLifeGearExperience(player, gained);
            if (ModCompat.isLifeSpellbookCompatEnabled()) {
                CuriosLifeGearCompat.grantLifeGearExperience(player, gained);
            }
            if (ModCompat.isTinkersConstructCompatEnabled()) {
                TinkersLifeGearCompat.grantLifeGearExperience(player, gained);
            }
        }

        mobUtilitiesData.putInt(TRACKED_EXPERIENCE_TAG, currentTotalExperience);
        persistentData.put(Player.PERSISTED_NBT_TAG, mobUtilitiesData);
    }

    private static void grantLifeGearExperience(ItemStack stack, int gained) {
        if (gained <= 0 || stack.isEmpty() || !(stack.getItem() instanceof LifeGearItem lifeGearItem)) {
            return;
        }
        lifeGearItem.addLifeExperience(stack, gained);
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
