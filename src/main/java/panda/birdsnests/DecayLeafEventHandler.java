package panda.birdsnests;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DecayLeafEventHandler {
	private static final Logger LOGGER = LogManager.getLogger();

	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent event) {
		ResourceLocation name = event.getName();
		ResourceLocation blockId = new ResourceLocation(name.getNamespace(), name.getPath().substring("blocks/".length()));

		// ✅ Try to match to a block loot table
		Block block = ForgeRegistries.BLOCKS.getValue(blockId);
		if (block == null) {
			return;
		}

		if (!block.defaultBlockState().is(BlockTags.LEAVES) && !blockId.getPath().contains("leaves")) {
			return;
		}

		// ✅ Respect config
		if (!Config.allowDecayDrops.get()) return;

		LootPool pool = LootPool.lootPool()
				.setRolls(ConstantValue.exactly(1))
				.when(LootItemRandomChanceCondition.randomChance(1.0F / Config.decayDropModifier.get()))
				.add(LootItem.lootTableItem(RegistryHandler.BIRDSNEST.get()))
				.build();

		event.getTable().addPool(pool);
	}
}