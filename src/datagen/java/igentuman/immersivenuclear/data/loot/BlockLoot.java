/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.data.loot;

import blusunrize.immersiveengineering.api.EnumMetals;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import blusunrize.immersiveengineering.common.util.loot.*;
import blusunrize.immersiveengineering.data.loot.LootUtils;
import igentuman.immersivenuclear.common.register.INBlocks;
import igentuman.immersivenuclear.common.register.INBlocks.*;
import igentuman.immersivenuclear.common.register.INItems;
import igentuman.immersivenuclear.common.register.INItems.ItemRegObject;
import com.google.common.collect.ImmutableList;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class BlockLoot implements LootTableSubProvider
{
	private final Set<ResourceLocation> generatedTables = new HashSet<>();
	private BiConsumer<ResourceLocation, LootTable.Builder> out;

	private ResourceLocation toTableLoc(ResourceLocation in)
	{
		return new ResourceLocation(in.getNamespace(), "blocks/"+in.getPath());
	}

	@Override
	public void generate(BiConsumer<ResourceLocation, Builder> out)
	{
		this.out = out;
		/*
		register(WoodenDevices.CRATE, LootPool.lootPool().add(tileOrInv));
		register(WoodenDevices.REINFORCED_CRATE, tileDrop());
		register(WoodenDevices.SORTER, tileDrop());
		register(WoodenDevices.FLUID_SORTER, tileDrop());
		register(StoneDecoration.CORESAMPLE, tileDrop());
		register(MetalDevices.TOOLBOX, tileDrop());
		register(Cloth.SHADER_BANNER, singleItem(Items.WHITE_BANNER));
		register(Cloth.SHADER_BANNER_WALL, singleItem(Items.WHITE_BANNER));
		register(Cloth.STRIP_CURTAIN, tileDrop());
		for(BlockEntry<? extends IEEntityBlock<? extends CapacitorBlockEntity>> cap : ImmutableList.of(
				MetalDevices.CAPACITOR_LV, MetalDevices.CAPACITOR_MV, MetalDevices.CAPACITOR_HV, MetalDevices.CAPACITOR_CREATIVE
		))
			register(cap, tileDrop());
		register(Connectors.FEEDTHROUGH, tileDrop());
		register(MetalDevices.TURRET_CHEM, tileDrop());
		register(MetalDevices.TURRET_GUN, tileDrop(), dropInv());
		register(WoodenDevices.WOODEN_BARREL, tileDrop());
		register(WoodenDevices.LOGIC_UNIT, tileDrop());
		register(MetalDevices.BARREL, tileDrop());

		registerMultiblocks();

		registerSelfDropping(WoodenDevices.CRAFTING_TABLE, dropInv());
		registerSelfDropping(WoodenDevices.WORKBENCH, dropInv());
		registerSelfDropping(WoodenDevices.CIRCUIT_TABLE, dropInv());
		registerSelfDropping(WoodenDevices.ITEM_BATCHER, dropInv());
		registerSelfDropping(MetalDevices.CLOCHE, dropInv());
		registerSelfDropping(MetalDevices.CHARGING_STATION, dropInv());
		registerSlabs();
		registerSawdust();
		for(BlockEntry<ConveyorBlock> entry : MetalDevices.CONVEYORS.values())
		{
			ConveyorBlock block = entry.get();
			register(entry, singleItem(block).apply(ConveyorCoverLootFunction.builder()));
		}
		for(EnumMetals metal : EnumMetals.values())
			if(metal.shouldAddOre())
				registerOre(metal);
*/
		registerAllRemainingAsDefault();
	}

	private void registerMultiblocks()
	{

		//registerMultiblock(IEMultiblockLogic.MIXER);
	}


	private void registerAllRemainingAsDefault()
	{
		/*for(IEBlocks.BlockEntry<?> b : IEBlocks.BlockEntry.ALL_ENTRIES)
			if(!generatedTables.contains(toTableLoc(b.getId())))
				registerSelfDropping(b);*/
	}

	private void registerMultiblock(MultiblockRegistration<?> registration)
	{
		registerMultiblock(registration.block());
	}

	private void registerMultiblock(Supplier<? extends Block> b)
	{
		register(b, dropInv(), dropOriginalBlock());
	}

	private LootPool.Builder dropInv()
	{
		return createPoolBuilder()
				.add(DropInventoryLootEntry.builder());
	}

	private LootPool.Builder tileDrop()
	{
		return createPoolBuilder()
				.add(BEDropLootEntry.builder());
	}

	private LootPool.Builder dropOriginalBlock()
	{
		return createPoolBuilder()
				.add(LootUtils.getMultiblockDropBuilder());
	}

	private void register(Supplier<? extends Block> b, LootPool.Builder... pools)
	{
		LootTable.Builder builder = LootTable.lootTable();
		for(LootPool.Builder pool : pools)
			builder.withPool(pool);
		register(b, builder);
	}

	private void register(Supplier<? extends Block> b, LootTable.Builder table)
	{
		register(BuiltInRegistries.BLOCK.getKey(b.get()), table);
	}

	private void register(ResourceLocation name, LootTable.Builder table)
	{
		ResourceLocation loc = toTableLoc(name);
		if(!generatedTables.add(loc))
			throw new IllegalStateException("Duplicate loot table "+name);
		out.accept(loc, table.setParamSet(LootContextParamSets.BLOCK));
	}

	private void registerSelfDropping(Supplier<? extends Block> b, LootPool.Builder... pool)
	{
		LootPool.Builder[] withSelf = Arrays.copyOf(pool, pool.length+1);
		withSelf[withSelf.length-1] = singleItem(b.get());
		register(b, withSelf);
	}

	private Builder dropProvider(ItemLike in)
	{
		return LootTable.lootTable().withPool(singleItem(in));
	}

	private LootPool.Builder singleItem(ItemLike in)
	{
		return createPoolBuilder()
				.setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(in));
	}

	private LootPool.Builder createPoolBuilder()
	{
		return LootPool.lootPool().when(ExplosionCondition.survivesExplosion());
	}

	private void registerOre(EnumMetals metal)
	{
		/*registerOre(metal, INBlocks.Metals.ORES.get(metal));
		registerOre(metal, INBlocks.Metals.DEEPSLATE_ORES.get(metal));*/
	}

	private void registerOre(EnumMetals metal, IEBlocks.BlockEntry<?> oreBlock)
	{
		/*ItemRegObject<Item> rawOre = INItems.Metals.RAW_ORES.get(metal);
		LootTable.Builder ret = LootTable.lootTable().withPool(LootPool.lootPool()
				.setRolls(ConstantValue.exactly(1.0F))
				.add(LootItem.lootTableItem(oreBlock)
						// if silk touch
						.when(MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1)))))
						// else
						.otherwise(LootItem.lootTableItem(rawOre)
								.apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))
								.apply(ApplyExplosionDecay.explosionDecay())
						)
				)
		);
		register(oreBlock, ret);*/
	}

	private LootPool.Builder binBonusLootPool(ItemLike item, Enchantment ench, float prob, int extra)
	{
		return createPoolBuilder()
				.add(LootItem.lootTableItem(item))
				.apply(ApplyBonusCount.addBonusBinomialDistributionCount(ench, prob, extra));
	}

	private <T extends Comparable<T> & StringRepresentable> LootItemCondition.Builder propertyIs(
			Supplier<? extends Block> b, Property<T> prop, T value
	)
	{
		return LootItemBlockStatePropertyCondition.hasBlockStateProperties(b.get())
				.setProperties(
						StatePropertiesPredicate.Builder.properties().hasProperty(prop, value)
				);
	}
}
