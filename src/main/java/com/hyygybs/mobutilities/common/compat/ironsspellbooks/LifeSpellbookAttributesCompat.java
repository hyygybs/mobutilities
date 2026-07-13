package com.hyygybs.mobutilities.common.compat.ironsspellbooks;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.hyygybs.mobutilities.common.item.LifeGearItem;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public final class LifeSpellbookAttributesCompat {
    private static final UUID LIFE_SPELL_POWER_UUID = UUID.fromString("31f57cbe-e400-4744-9585-62802f2f5e03");
    private static final UUID LIFE_CAST_TIME_REDUCTION_UUID = UUID.fromString("4c994e92-faf3-4e94-a096-0a2ef91ded32");
    private static final UUID LIFE_MAX_MANA_UUID = UUID.fromString("b7fb8012-f697-4a43-9636-0ddf3be02df3");

    private LifeSpellbookAttributesCompat() {
    }

    public static Multimap<Attribute, AttributeModifier> addSpellbookBonuses(
            Multimap<Attribute, AttributeModifier> baseModifiers,
            ItemStack stack,
            LifeGearItem lifeGearItem
    ) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(baseModifiers);
        double spellPowerBonus = lifeGearItem.getSpellPowerBonus(stack);
        double castTimeReductionBonus = lifeGearItem.getCastTimeReductionBonus(stack);
        double maxManaBonus = lifeGearItem.getMaxManaBonus(stack);
        if (spellPowerBonus > 0.0D) {
            builder.put(AttributeRegistry.SPELL_POWER.get(), new AttributeModifier(
                    LIFE_SPELL_POWER_UUID,
                    "Life spellbook spell power bonus",
                    spellPowerBonus,
                    AttributeModifier.Operation.ADDITION
            ));
        }
        if (castTimeReductionBonus > 0.0D) {
            builder.put(AttributeRegistry.CAST_TIME_REDUCTION.get(), new AttributeModifier(
                    LIFE_CAST_TIME_REDUCTION_UUID,
                    "Life spellbook cast time reduction bonus",
                    castTimeReductionBonus,
                    AttributeModifier.Operation.ADDITION
            ));
        }
        if (maxManaBonus > 0.0D) {
            builder.put(AttributeRegistry.MAX_MANA.get(), new AttributeModifier(
                    LIFE_MAX_MANA_UUID,
                    "Life spellbook max mana bonus",
                    maxManaBonus,
                    AttributeModifier.Operation.ADDITION
            ));
        }
        return builder.build();
    }
}
