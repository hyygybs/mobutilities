package com.hyygybs.mobutilities.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LifeBowItem extends BowItem implements LifeGearItem {
    public LifeBowItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return isLifeBarVisible(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return getLifeBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return getLifeBarColor(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        appendLifeTooltip(stack, tooltip);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        if (!(livingEntity instanceof Player player)) {
            return;
        }

        boolean infiniteArrows = player.getAbilities().instabuild;
        ItemStack projectileStack = player.getProjectile(stack);
        if (projectileStack.isEmpty() && !infiniteArrows) {
            return;
        }

        if (projectileStack.isEmpty()) {
            projectileStack = new ItemStack(Items.ARROW);
        }

        int useTicks = this.getUseDuration(stack) - timeLeft;
        useTicks = Math.max(1, (int) Math.round(useTicks * getChargeSpeedMultiplier(stack)));
        float power = BowItem.getPowerForTime(useTicks);
        if (power < 0.1F) {
            return;
        }

        if (!level.isClientSide) {
            ArrowItem arrowItem = projectileStack.getItem() instanceof ArrowItem arrow ? arrow : (ArrowItem) Items.ARROW;
            AbstractArrow abstractArrow = arrowItem.createArrow(level, projectileStack, player);
            abstractArrow = customArrow(abstractArrow);
            abstractArrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 3.0F, 1.0F);
            abstractArrow.setBaseDamage(abstractArrow.getBaseDamage() + getProjectileDamageBonus(stack));
            if (power == 1.0F) {
                abstractArrow.setCritArrow(true);
            }

            level.addFreshEntity(abstractArrow);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F,
                1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);

        if (!infiniteArrows) {
            projectileStack.shrink(1);
            if (projectileStack.isEmpty()) {
                player.getInventory().removeItem(projectileStack);
            }
        }

        stack.hurtAndBreak(1, player, bowUser -> bowUser.broadcastBreakEvent(player.getUsedItemHand()));
        player.awardStat(Stats.ITEM_USED.get(this));
    }
}
