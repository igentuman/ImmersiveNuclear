/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.data.tags;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import com.google.common.base.Preconditions;
import igentuman.immersivenuclear.api.INEnumMetals;
import igentuman.immersivenuclear.api.INTags;
import igentuman.immersivenuclear.common.register.INBlocks;
import igentuman.immersivenuclear.common.register.INBlocks.Metals;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static igentuman.immersivenuclear.api.Lib.MODID;

public class INBlockTags extends BlockTagsProvider
{

	public INBlockTags(PackOutput output, CompletableFuture<Provider> lookupProvider, ExistingFileHelper existing)
	{
		super(output, lookupProvider, MODID, existing);
	}

	@Override
	protected void addTags(Provider p_256380_)
	{

		for(INEnumMetals metal : INEnumMetals.values())
		{
			INTags.MetalTags tags = INTags.getTagsFor(metal);
			if(!metal.isVanillaMetal())
			{
				tag(tags.storage).add(INBlocks.Metals.STORAGE.get(metal).get());
				tag(Tags.Blocks.STORAGE_BLOCKS).addTag(tags.storage);
				if(metal.shouldAddOre())
				{
					Preconditions.checkNotNull(tags.ore);
					tag(tags.ore)
							.add(INBlocks.Metals.ORES.get(metal).get())
							.add(INBlocks.Metals.DEEPSLATE_ORES.get(metal).get());
					tag(Tags.Blocks.ORES).addTag(tags.ore);
					Preconditions.checkNotNull(tags.rawBlock);
					tag(tags.rawBlock).add(INBlocks.Metals.RAW_ORES.get(metal).get());
					tag(Tags.Blocks.STORAGE_BLOCKS).addTag(tags.rawBlock);
					tag(Tags.Blocks.ORES_IN_GROUND_STONE).add(Metals.ORES.get(metal).get());
					tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE).add(Metals.DEEPSLATE_ORES.get(metal).get());
					tag(Tags.Blocks.ORE_RATES_SINGULAR).add(Metals.ORES.get(metal).get())
							.add(Metals.DEEPSLATE_ORES.get(metal).get());
				}
			}
		}

		registerPickaxeMineable();
		registerAxeMineable();

		//checkAllRegisteredForBreaking();

	}
	private void registerAxeMineable()
	{
		IntrinsicTagAppender<Block> tag = tag(BlockTags.MINEABLE_WITH_AXE);
		/*registerMineable(
				tag,
				WoodenDevices.CRAFTING_TABLE,
				WoodenDevices.WORKBENCH,
				WoodenDevices.CIRCUIT_TABLE,
				WoodenDevices.GUNPOWDER_BARREL,
				WoodenDevices.WOODEN_BARREL,
				WoodenDevices.TURNTABLE,
				WoodenDevices.CRATE,
				WoodenDevices.REINFORCED_CRATE,
				WoodenDevices.SORTER,
				WoodenDevices.ITEM_BATCHER,
				WoodenDevices.FLUID_SORTER,
				WoodenDevices.WINDMILL,
				WoodenDevices.WATERMILL,
				WoodenDevices.TREATED_WALLMOUNT,
				WoodenDevices.LOGIC_UNIT,
				WoodenDecoration.TREATED_FENCE,
				WoodenDecoration.TREATED_SCAFFOLDING,
				WoodenDecoration.TREATED_POST,
				WoodenDecoration.SAWDUST,
				WoodenDecoration.FIBERBOARD,
				Cloth.SHADER_BANNER,
				Cloth.SHADER_BANNER_WALL
		);
		for(BlockEntry<?> treatedWood : WoodenDecoration.TREATED_WOOD.values())
			registerMineable(tag, treatedWood);*/
	}
	
	private void registerMineable(IntrinsicTagAppender<Block> tag, MultiblockRegistration<?>... entries)
	{
		for(MultiblockRegistration<?> entry : entries)
			tag.add(entry.block().get());
	}

	private <T extends Block> void registerMineable(IntrinsicTagAppender<Block> tag, Map<?, INBlocks.BlockEntry<T>> entries)
	{
		registerMineable(tag, new ArrayList<>(entries.values()));
	}

	private void registerMineable(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> tag, INBlocks.BlockEntry<?>... entries) {
		this.registerMineable(tag, Arrays.asList(entries));
	}


	private void registerMineable(IntrinsicTagAppender<Block> tag, List<INBlocks.BlockEntry<?>> entries)
	{
		entries.sort(Comparator.comparing(INBlocks.BlockEntry::getId));
		for(INBlocks.BlockEntry<?> entry : entries)
		{
			tag.add(entry.get());

		}
	}

	private void registerPickaxeMineable()
	{
		IntrinsicTagAppender<Block> tag = tag(BlockTags.MINEABLE_WITH_PICKAXE);

		/*registerMineable(
				tag,
				//INMultiblockLogic.ALLOY_SMELTER,
		);
		registerMineable(
				tag,
				MetalDevices.CAPACITOR_EV,
				MetalDecoration.EV_COIL,
				Connectors.TRANSFORMER_EV
		);
*/
		setOreMiningLevel(INEnumMetals.BORON, Tiers.IRON);
		setOreMiningLevel(INEnumMetals.THORIUM, Tiers.IRON);
		setStorageMiningLevel(INEnumMetals.BORON, Tiers.IRON);
		setStorageMiningLevel(INEnumMetals.ZIRCONIUM, Tiers.IRON);
		setStorageMiningLevel(INEnumMetals.THORIUM, Tiers.IRON);
		setStorageMiningLevel(INEnumMetals.URANIUM233, Tiers.IRON);
		setStorageMiningLevel(INEnumMetals.URANIUM235, Tiers.IRON);
		setStorageMiningLevel(INEnumMetals.URANIUM238, Tiers.IRON);
		setStorageMiningLevel(INEnumMetals.PLUTONIUM239, Tiers.IRON);
		setStorageMiningLevel(INEnumMetals.PLUTONIUM240, Tiers.IRON);
		setStorageMiningLevel(INEnumMetals.PLUTONIUM241, Tiers.IRON);
		setStorageMiningLevel(INEnumMetals.STAINLESS_STEEL, Tiers.IRON);
	}

	private void setOreMiningLevel(INEnumMetals metal, Tiers level)
	{
		final INBlocks.BlockEntry<Block> ore = Metals.ORES.get(metal);
		final INBlocks.BlockEntry<Block> deepslateOre = Metals.DEEPSLATE_ORES.get(metal);
		final INBlocks.BlockEntry<Block> rawOre = Metals.RAW_ORES.get(metal);
		this.setMiningLevel(ore, level);
		this.setMiningLevel(deepslateOre, level);
		this.setMiningLevel(rawOre, level);
		this.registerMineable(this.tag(BlockTags.MINEABLE_WITH_PICKAXE), ore, deepslateOre, rawOre);
	}


	private void setStorageMiningLevel(INEnumMetals metal, Tiers level)
	{
		final INBlocks.BlockEntry<Block> storage = Metals.STORAGE.get(metal);
		setMiningLevel(storage, level);
		registerMineable(tag(BlockTags.MINEABLE_WITH_PICKAXE), storage);
	}

	private void setMiningLevel(Supplier<Block> block, Tiers level)
	{
		TagKey<Block> tag = switch(level)
		{
			case STONE -> BlockTags.NEEDS_STONE_TOOL;
			case IRON -> BlockTags.NEEDS_IRON_TOOL;
			case DIAMOND -> BlockTags.NEEDS_DIAMOND_TOOL;
			default -> throw new IllegalArgumentException("No tag available for "+level.name());
		};
		tag(tag).add(block.get());
	}

}
