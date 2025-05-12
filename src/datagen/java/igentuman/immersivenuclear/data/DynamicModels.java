/*
 * BluSunrize
 * Copyright (c) 2023
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.data;

import blusunrize.immersiveengineering.client.render.tile.DieselGeneratorRenderer;
import igentuman.immersivenuclear.api.Lib;
import igentuman.immersivenuclear.client.render.tile.*;
import igentuman.immersivenuclear.data.DynamicModels.SimpleModelBuilder;
import igentuman.immersivenuclear.data.blockstates.MultiblockStates;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.client.model.generators.loaders.ObjModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import java.util.Map.Entry;

import static igentuman.immersivenuclear.ImmersiveNuclear.rl;

public class DynamicModels extends ModelProvider<SimpleModelBuilder>
{
	private final MultiblockStates multiblocks;

	public DynamicModels(MultiblockStates multiblocks, PackOutput output, ExistingFileHelper existingFileHelper)
	{
		super(output, Lib.MODID, "dynamic", rl -> new SimpleModelBuilder(rl, existingFileHelper), existingFileHelper);
		this.multiblocks = multiblocks;
	}

	@Override
	protected void registerModels()
	{

		/*getBuilder(DieselGeneratorRenderer.NAME)
				.customLoader(ObjModelBuilder::begin)
				.modelLocation(rl("models/block/metal_multiblock/diesel_generator_fan.obj"))
				.flipV(true)
				.end();*/

		for(Entry<Block, ModelFile> multiblock : multiblocks.unsplitModels.entrySet())
			withExistingParent(BuiltInRegistries.BLOCK.getKey(multiblock.getKey()).getPath(), multiblock.getValue().getLocation());
	}

	@Nonnull
	@Override
	public String getName()
	{
		return "Dynamic models";
	}

	public static class SimpleModelBuilder extends ModelBuilder<SimpleModelBuilder>
	{

		public SimpleModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper)
		{
			super(outputLocation, existingFileHelper);
		}
	}
}
