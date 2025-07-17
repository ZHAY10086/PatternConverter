package com.davenonymous.patternconverter.setup;

import com.davenonymous.patternconverter.mods.PluginLoader;
import net.neoforged.bus.api.IEventBus;

public class Registration {
	public static void register(IEventBus modbus) {
		ModBlocks.BLOCKS.register(modbus);
		ModBlocks.BLOCK_ENTITIES.register(modbus);
		ModBlocks.BLOCK_TYPES.register(modbus);
		ModItems.ITEMS.register(modbus);
		ModContainers.CONTAINERS.register(modbus);
		ModDataComponents.DATA_COMPONENTS.register(modbus);
		ModLootPools.LOOT_POOL_ENTRY_TYPES.register(modbus);

		PluginLoader.loadPlugins();
	}
}
