package com.hyygybs.mobutilities.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UpgradeItem extends Item {
    private final Type type;

    public UpgradeItem(Type type, Properties properties) {
        super(properties);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.mobutilities.upgrade." + type.getSerializedName()).withStyle(ChatFormatting.GRAY));
    }

    public enum Type {
        SPEED("speed"),
        SIMULATION("simulation"),
        LOOTING("looting"),
        SPECIAL("special");

        private final String serializedName;

        Type(String serializedName) {
            this.serializedName = serializedName;
        }

        public String getSerializedName() {
            return serializedName;
        }
    }
}
