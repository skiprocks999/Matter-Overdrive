package matteroverdrive.core.datagen.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import matteroverdrive.MatterOverdrive;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.SetContainerContents;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public abstract class AbstractLootTableProvider extends LootTableProvider {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	protected final Map<Block, LootTable.Builder> lootTables = new HashMap<>();
	private final DataGenerator generator;

	public AbstractLootTableProvider(DataGenerator dataGeneratorIn) {
		super(dataGeneratorIn);
		this.generator = dataGeneratorIn;
	}

	protected abstract void addTables();

	protected LootTable.Builder itemOnlyTable(String name, Block block, BlockEntityType<?> type) {
		LootPool.Builder builder = LootPool.lootPool().name(name).setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(block)
						.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
						.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("inventory",
								"BlockEntityTag.inventory", CopyNbtFunction.MergeStrategy.REPLACE))
						.apply(SetContainerContents.setContents(type)
								.withEntry(DynamicLoot.dynamicEntry(new ResourceLocation("minecraft", "contents")))));
		return LootTable.lootTable().withPool(builder);
	}

	protected LootTable.Builder itemAndEnergyTable(String name, Block block, BlockEntityType<?> type) {
		LootPool.Builder builder = LootPool.lootPool().name(name).setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(block)
						.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
						.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
								.copy("inventory", "BlockEntityTag.inventory", CopyNbtFunction.MergeStrategy.REPLACE)
								.copy("energy", "BlockEntityTag.energy", CopyNbtFunction.MergeStrategy.REPLACE))
						.apply(SetContainerContents.setContents(type)
								.withEntry(DynamicLoot.dynamicEntry(new ResourceLocation("minecraft", "contents")))));
		return LootTable.lootTable().withPool(builder);
	}

	protected LootTable.Builder energyTable(String name, Block block, BlockEntityType<?> type) {
		LootPool.Builder builder = LootPool.lootPool().name(name).setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(block)
						.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
						.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("energy",
								"BlockEntityTag.energy", CopyNbtFunction.MergeStrategy.REPLACE)));
		return LootTable.lootTable().withPool(builder);
	}

	protected LootTable.Builder itemEnergyMatterTable(String name, Block block, BlockEntityType<?> type) {
		LootPool.Builder builder = LootPool.lootPool().name(name).setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(block)
						.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
						.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
								.copy("inventory", "BlockEntityTag.inventory", CopyNbtFunction.MergeStrategy.REPLACE)
								.copy("energy", "BlockEntityTag.energy", CopyNbtFunction.MergeStrategy.REPLACE)
								.copy("matter", "BlockEntityTag.matter", CopyNbtFunction.MergeStrategy.REPLACE))
						.apply(SetContainerContents.setContents(type)
								.withEntry(DynamicLoot.dynamicEntry(new ResourceLocation("minecraft", "contents")))));
		return LootTable.lootTable().withPool(builder);
	}

	@Override
	public void run(HashCache cache) {
		addTables();

		Map<ResourceLocation, LootTable> tables = new HashMap<>();
		for (Map.Entry<Block, LootTable.Builder> entry : lootTables.entrySet()) {
			tables.put(entry.getKey().getLootTable(), entry.getValue().setParamSet(LootContextParamSets.BLOCK).build());
		}
		writeTables(cache, tables);
	}

	private void writeTables(HashCache cache, Map<ResourceLocation, LootTable> tables) {
		Path outputFolder = this.generator.getOutputFolder();
		tables.forEach((key, lootTable) -> {
			Path path = outputFolder.resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");
			try {
				DataProvider.save(GSON, cache, LootTables.serialize(lootTable), path);
			} catch (IOException e) {
				MatterOverdrive.LOGGER.error("Couldn't write loot table {}", path, e);
			}
		});
	}

	@Override
	public String getName() {
		return "MatterOverdrive LootTables";
	}

}
