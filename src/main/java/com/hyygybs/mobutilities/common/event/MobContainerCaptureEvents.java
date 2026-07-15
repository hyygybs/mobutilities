package com.hyygybs.mobutilities.common.event;

import com.hyygybs.mobutilities.MobUtilities;
import com.hyygybs.mobutilities.common.item.MobContainerItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MobUtilities.MOD_ID)
public final class MobContainerCaptureEvents {
    private MobContainerCaptureEvents() {
    }

    @SubscribeEvent
    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        handleCapture(event, event.getTarget());
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        handleCapture(event, event.getTarget());
    }

    private static void handleCapture(PlayerInteractEvent event, net.minecraft.world.entity.Entity target) {
        if (!(target instanceof LivingEntity livingEntity)) {
            return;
        }

        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        InteractionResult result = MobContainerItem.tryCaptureMob(stack, player, livingEntity, event.getHand());
        if (result.consumesAction()) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }
}
