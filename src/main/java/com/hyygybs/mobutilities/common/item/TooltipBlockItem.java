package com.hyygybs.mobutilities.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TooltipBlockItem extends BlockItem {
    private final String tooltipKeyPrefix;
    private final int lineCount;

    public TooltipBlockItem(Block block, Properties properties, String tooltipKeyPrefix, int lineCount) {
        super(block, properties);
        this.tooltipKeyPrefix = tooltipKeyPrefix;
        this.lineCount = lineCount;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        for (int i = 1; i <= lineCount; i++) {
            tooltip.add(Component.translatable(tooltipKeyPrefix + "." + i).withStyle(ChatFormatting.GRAY));
        }
    }
}
