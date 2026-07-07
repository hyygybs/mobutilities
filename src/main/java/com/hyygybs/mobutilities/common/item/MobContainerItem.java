package com.hyygybs.mobutilities.common.item;

import com.hyygybs.mobutilities.common.registration.ModItems;
import com.hyygybs.mobutilities.common.util.MobContainerData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class MobContainerItem extends Item {
    private final boolean filledVariant;

    public MobContainerItem(boolean filledVariant, Properties properties) {
        super(properties);
        this.filledVariant = filledVariant;
    }

    public boolean isFilledVariant() {
        return filledVariant;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        if (filledVariant || !(interactionTarget instanceof Mob mob) || !MobContainerData.canCapture(interactionTarget)) {
            return InteractionResult.PASS;
        }
        Level level = player.level();
        if (level.isClientSide) {
            return InteractionResult.sidedSuccess(true);
        }

        MobContainerData data = MobContainerData.fromEntity(mob);
        ItemStack filledStack = new ItemStack(ModItems.MOB_CONTAINER_FILLED.get());
        data.writeTo(filledStack);

        stack.shrink(1);
        if (stack.isEmpty()) {
            player.setItemInHand(usedHand, filledStack);
        } else if (!player.getInventory().add(filledStack)) {
            player.drop(filledStack, false);
        }

        level.playSound(null, mob.blockPosition(), SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.PLAYERS, 0.8F, 1.15F);
        mob.discard();
        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!filledVariant) {
            return super.useOn(context);
        }

        Level level = context.getLevel();
        if (level.isClientSide) {
            return InteractionResult.sidedSuccess(true);
        }

        ItemStack stack = context.getItemInHand();
        Optional<MobContainerData> optionalData = MobContainerData.fromStack(stack);
        if (optionalData.isEmpty()) {
            return InteractionResult.FAIL;
        }

        BlockPos spawnPos = context.getClickedPos().relative(context.getClickedFace());
        Entity entity = optionalData.get().createEntity(level);
        if (entity == null) {
            return InteractionResult.FAIL;
        }

        entity.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, entity.getYRot(), entity.getXRot());
        if (!((ServerLevel) level).tryAddFreshEntityWithPassengers(entity)) {
            return InteractionResult.FAIL;
        }

        Player player = context.getPlayer();
        if (player != null && !player.getAbilities().instabuild) {
            ItemStack emptyStack = new ItemStack(ModItems.MOB_CONTAINER_EMPTY.get());
            stack.shrink(1);
            if (stack.isEmpty()) {
                player.setItemInHand(context.getHand(), emptyStack);
            } else if (!player.getInventory().add(emptyStack)) {
                player.drop(emptyStack, false);
            }
        }

        level.playSound(null, spawnPos, SoundEvents.BOTTLE_EMPTY, SoundSource.PLAYERS, 0.8F, 0.9F);
        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (filledVariant) {
            return InteractionResultHolder.pass(stack);
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        if (filledVariant) {
            tooltip.add(net.minecraft.network.chat.Component.translatable("tooltip.mobutilities.container.release_hint").withStyle(ChatFormatting.YELLOW));
            MobContainerData.fromStack(stack).ifPresent(data -> tooltip.addAll(data.createTooltip()));
        } else {
            tooltip.add(net.minecraft.network.chat.Component.translatable("tooltip.mobutilities.container.capture_hint").withStyle(ChatFormatting.GRAY));
        }
    }
}
