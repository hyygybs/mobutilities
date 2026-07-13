package com.hyygybs.mobutilities.common.compat.tconstruct;

import com.hyygybs.mobutilities.common.compat.ModCompat;
import com.hyygybs.mobutilities.common.item.LifeGearItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.DurabilityDisplayModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

public class GrowableModifier extends Modifier implements DurabilityDisplayModifierHook, TooltipModifierHook, ConditionalStatModifierHook, AttributesModifierHook {
    static final ResourceLocation LEVEL_KEY = ResourceLocation.fromNamespaceAndPath("mobutilities", "life_level");
    static final ResourceLocation EXPERIENCE_KEY = ResourceLocation.fromNamespaceAndPath("mobutilities", "life_experience");
    private static final int EXPERIENCE_BAR_COLOR = 0x55FF55;
    private static final String CONSTRUCTS_CASTING_MOD_ID = "constructs_casting";
    private static final ToolStatId CC_SPELL_POWER = new ToolStatId(CONSTRUCTS_CASTING_MOD_ID, "spell_power");
    private static final ToolStatId CC_COOLDOWN_REDUCTION = new ToolStatId(CONSTRUCTS_CASTING_MOD_ID, "cooldown_reduction");
    private static final ToolStatId CC_MAX_MANA = new ToolStatId(CONSTRUCTS_CASTING_MOD_ID, "max_mana");
    private static final ToolStatId CC_SPELL_SLOTS = new ToolStatId(CONSTRUCTS_CASTING_MOD_ID, "spell_slots");

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.DURABILITY_DISPLAY, ModifierHooks.TOOLTIP, ModifierHooks.CONDITIONAL_STAT, ModifierHooks.ATTRIBUTES);
    }

    @Override
    public Boolean showDurabilityBar(IToolStackView tool, ModifierEntry modifier) {
        return Boolean.TRUE;
    }

    @Override
    public int getDurabilityWidth(IToolStackView tool, ModifierEntry modifier) {
        int needed = LifeGearItem.getExperienceForNextLevel(getLifeLevel(tool));
        return DurabilityDisplayModifierHook.getWidthFor(getStoredExperience(tool), needed);
    }

    @Override
    public int getDurabilityRGB(IToolStackView tool, ModifierEntry modifier) {
        return EXPERIENCE_BAR_COLOR;
    }

    @Override
    public void addTooltip(
            IToolStackView tool,
            ModifierEntry modifier,
            Player player,
            List<Component> tooltip,
            TooltipKey tooltipKey,
            TooltipFlag tooltipFlag
    ) {
        int level = getLifeLevel(tool);
        int stored = getStoredExperience(tool);
        int needed = LifeGearItem.getExperienceForNextLevel(level);
        tooltip.add(Component.translatable("tooltip.mobutilities.life_gear.level", level).withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("tooltip.mobutilities.life_gear.experience", stored, needed).withStyle(ChatFormatting.GRAY));

        if (level <= 0) {
            return;
        }

        if (tool.getStats().hasStat(ToolStats.ATTACK_DAMAGE)) {
            tooltip.add(Component.translatable(
                    "tooltip.mobutilities.tconstruct.growable.attack_damage",
                    formatBonus(level * 0.5D)
            ).withStyle(ChatFormatting.RED));
        }
        if (tool.getStats().hasStat(ToolStats.ATTACK_SPEED)) {
            tooltip.add(Component.translatable(
                    "tooltip.mobutilities.tconstruct.growable.attack_speed",
                    formatBonus(level * 0.05D)
            ).withStyle(ChatFormatting.RED));
        }
        if (tool.getStats().hasStat(ToolStats.PROJECTILE_DAMAGE)) {
            tooltip.add(Component.translatable(
                    "tooltip.mobutilities.tconstruct.growable.projectile_damage",
                    formatBonus(level * 0.5D)
            ).withStyle(ChatFormatting.GOLD));
        }
        if (tool.getStats().hasStat(ToolStats.DRAW_SPEED)) {
            tooltip.add(Component.translatable(
                    "tooltip.mobutilities.tconstruct.growable.draw_speed",
                    formatBonus(level * 0.05D)
            ).withStyle(ChatFormatting.GOLD));
        }
        if (tool.getStats().hasStat(ToolStats.ARMOR)) {
            tooltip.add(Component.translatable(
                    "tooltip.mobutilities.tconstruct.growable.armor",
                    formatBonus(level)
            ).withStyle(ChatFormatting.BLUE));
        }
        if (tool.getStats().hasStat(ToolStats.ARMOR_TOUGHNESS)) {
            tooltip.add(Component.translatable(
                    "tooltip.mobutilities.tconstruct.growable.armor_toughness",
                    formatBonus(level)
            ).withStyle(ChatFormatting.BLUE));
        }
        if (isConstructsCastingWand(tool)) {
            tooltip.add(Component.translatable(
                    "tooltip.mobutilities.tconstruct.growable.cc_spell_power",
                    formatBonus(level * 0.05D)
            ).withStyle(ChatFormatting.DARK_AQUA));
            tooltip.add(Component.translatable(
                    "tooltip.mobutilities.tconstruct.growable.cc_cooldown_reduction",
                    formatBonus(level * 0.05D)
            ).withStyle(ChatFormatting.LIGHT_PURPLE));
        }
        if (isConstructsCastingSpellbook(tool)) {
            tooltip.add(Component.translatable(
                    "tooltip.mobutilities.tconstruct.growable.cc_spell_power",
                    formatBonus(level * 0.05D)
            ).withStyle(ChatFormatting.DARK_AQUA));
            tooltip.add(Component.translatable(
                    "tooltip.mobutilities.tconstruct.growable.cc_cast_time_reduction",
                    formatBonus(level * 0.05D)
            ).withStyle(ChatFormatting.LIGHT_PURPLE));
            tooltip.add(Component.translatable(
                    "tooltip.mobutilities.tconstruct.growable.cc_max_mana",
                    formatBonus(level * 50.0D)
            ).withStyle(ChatFormatting.BLUE));
        }
    }

    @Override
    public float modifyStat(IToolStackView tool, ModifierEntry modifier, net.minecraft.world.entity.LivingEntity entity, FloatToolStat stat, float baseValue, float multiplier) {
        int level = getLifeLevel(tool);
        if (level <= 0) {
            return baseValue;
        }
        if (stat == ToolStats.ATTACK_DAMAGE) {
            return baseValue + (level * 0.5F);
        }
        if (stat == ToolStats.ATTACK_SPEED) {
            return baseValue + (level * 0.05F);
        }
        if (stat == ToolStats.PROJECTILE_DAMAGE) {
            return baseValue + (level * 0.5F);
        }
        if (stat == ToolStats.DRAW_SPEED) {
            return baseValue + (level * 0.05F);
        }
        if (stat == ToolStats.ARMOR) {
            return baseValue + level;
        }
        if (stat == ToolStats.ARMOR_TOUGHNESS) {
            return baseValue + level;
        }
        return baseValue;
    }

    @Override
    public void addAttributes(
            IToolStackView tool,
            ModifierEntry modifier,
            EquipmentSlot slot,
            BiConsumer<Attribute, AttributeModifier> consumer
    ) {
        int level = getLifeLevel(tool);
        if (level <= 0 || !ModCompat.isConstructsCastingCompatEnabled() || !isConstructsCastingItem(tool)) {
            return;
        }

        if (isConstructsCastingWand(tool) && slot.getType() == EquipmentSlot.Type.HAND) {
            ConstructsCastingAttributeCompat.addWandAttributes(level, consumer);
        }

        if (isConstructsCastingSpellbook(tool) && slot == EquipmentSlot.LEGS) {
            ConstructsCastingAttributeCompat.addSpellbookAttributes(level, consumer);
        }
    }

    static int getLifeLevel(IToolStackView tool) {
        return Math.max(0, tool.getPersistentData().getInt(LEVEL_KEY));
    }

    static int getStoredExperience(IToolStackView tool) {
        return Math.max(0, tool.getPersistentData().getInt(EXPERIENCE_KEY));
    }

    private static String formatBonus(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    private static boolean isConstructsCastingItem(IToolStackView tool) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(tool.getItem());
        return itemId != null && CONSTRUCTS_CASTING_MOD_ID.equals(itemId.getNamespace());
    }

    private static boolean isConstructsCastingWand(IToolStackView tool) {
        return isConstructsCastingItem(tool)
                && hasToolStat(tool, CC_SPELL_POWER)
                && hasToolStat(tool, CC_COOLDOWN_REDUCTION)
                && !isConstructsCastingSpellbook(tool);
    }

    private static boolean isConstructsCastingSpellbook(IToolStackView tool) {
        return isConstructsCastingItem(tool)
                && (hasToolStat(tool, CC_SPELL_SLOTS) || hasToolStat(tool, CC_MAX_MANA));
    }

    private static boolean hasToolStat(IToolStackView tool, ToolStatId statId) {
        for (IToolStat<?> stat : tool.getStats().getContainedStats()) {
            if (stat.getName().equals(statId)) {
                return true;
            }
        }
        return false;
    }
}
