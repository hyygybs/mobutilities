package com.hyygybs.mobutilities.common.compat.tconstruct;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;
import java.util.function.BiConsumer;

public final class ConstructsCastingAttributeCompat {
    private static final UUID WAND_SPELL_POWER_UUID = UUID.fromString("2a5910ce-8b45-4c45-a2a7-37b6048786f8");
    private static final UUID WAND_COOLDOWN_UUID = UUID.fromString("a14d7aab-84cd-49ad-ba88-d272e0a666bd");
    private static final UUID BOOK_SPELL_POWER_UUID = UUID.fromString("03074e08-8e5e-4c53-a55a-dfc2f9d62c23");
    private static final UUID BOOK_CAST_TIME_UUID = UUID.fromString("862619c0-1d12-4cb1-b39b-2bc24e632e16");
    private static final UUID BOOK_MAX_MANA_UUID = UUID.fromString("cb9c1ca7-298a-49a7-b942-24eb056a3fba");

    private ConstructsCastingAttributeCompat() {
    }

    public static void addWandAttributes(int level, BiConsumer<Attribute, AttributeModifier> consumer) {
        consumer.accept(AttributeRegistry.SPELL_POWER.get(), new AttributeModifier(
                WAND_SPELL_POWER_UUID,
                "Life growable wand spell power bonus",
                level * 0.05D,
                AttributeModifier.Operation.ADDITION
        ));
        consumer.accept(AttributeRegistry.COOLDOWN_REDUCTION.get(), new AttributeModifier(
                WAND_COOLDOWN_UUID,
                "Life growable wand cooldown reduction bonus",
                level * 0.05D,
                AttributeModifier.Operation.ADDITION
        ));
    }

    public static void addSpellbookAttributes(int level, BiConsumer<Attribute, AttributeModifier> consumer) {
        consumer.accept(AttributeRegistry.SPELL_POWER.get(), new AttributeModifier(
                BOOK_SPELL_POWER_UUID,
                "Life growable spellbook spell power bonus",
                level * 0.05D,
                AttributeModifier.Operation.ADDITION
        ));
        consumer.accept(AttributeRegistry.CAST_TIME_REDUCTION.get(), new AttributeModifier(
                BOOK_CAST_TIME_UUID,
                "Life growable spellbook cast time reduction bonus",
                level * 0.05D,
                AttributeModifier.Operation.ADDITION
        ));
        consumer.accept(AttributeRegistry.MAX_MANA.get(), new AttributeModifier(
                BOOK_MAX_MANA_UUID,
                "Life growable spellbook max mana bonus",
                level * 50.0D,
                AttributeModifier.Operation.ADDITION
        ));
    }
}
