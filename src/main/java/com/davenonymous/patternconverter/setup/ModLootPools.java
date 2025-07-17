package com.davenonymous.patternconverter.setup;

import com.davenonymous.patternconverter.PatternConverter;
import com.davenonymous.patternconverter.lib.loot.DropInventoryLootEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModLootPools {
	public static final DeferredRegister<LootPoolEntryType> LOOT_POOL_ENTRY_TYPES =
		DeferredRegister.create(Registries.LOOT_POOL_ENTRY_TYPE, PatternConverter.MODID);

	public static final Supplier<LootPoolEntryType> DROP_INVENTORY_LOOT =
		LOOT_POOL_ENTRY_TYPES.register("drop_inventory", () -> new LootPoolEntryType(DropInventoryLootEntry.CODEC));
}
