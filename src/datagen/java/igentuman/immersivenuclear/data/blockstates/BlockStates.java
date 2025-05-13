/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.data.blockstates;

import blusunrize.immersiveengineering.api.utils.DirectionUtils;
import blusunrize.immersiveengineering.data.models.ModelProviderUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.api.INEnumMetals;
import igentuman.immersivenuclear.common.register.INBlocks;
import igentuman.immersivenuclear.common.register.INBlocks.Metals;
import igentuman.immersivenuclear.data.DataGenUtils;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.ExistingModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.client.model.generators.loaders.CompositeModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import static igentuman.immersivenuclear.ImmersiveNuclear.rl;
import static net.minecraft.client.renderer.RenderType.cutout;

public class BlockStates extends ExtendedBlockstateProvider
{
	private final ConfiguredModel EMPTY_MODEL;

	public BlockStates(PackOutput output, ExistingFileHelper exHelper)
	{
		super(output, exHelper);
		EMPTY_MODEL = new ConfiguredModel(
				new ExistingModelFile(modLoc("block/ie_empty"), existingFileHelper)
		);
	}

	@Override
	protected void registerStatesAndModels()
	{
		for(INEnumMetals m : INEnumMetals.values())
		{
			String name = m.tagName();
			if(!m.isVanillaMetal())
			{
				if(m.shouldAddOre())
				{
					cubeAll(Metals.ORES.get(m), modLoc("block/metal/ore_"+name));
					cubeAll(Metals.DEEPSLATE_ORES.get(m), modLoc("block/metal/deepslate_ore_"+name));
					cubeAll(Metals.RAW_ORES.get(m), modLoc("block/metal/raw_"+name));
				}
				ResourceLocation defaultStorageTexture = modLoc("block/metal/storage_"+name);
				INBlocks.BlockEntry<Block> storage = Metals.STORAGE.get(m);
				String storageName = name(storage);
				BlockModelBuilder storageModel;
				storageModel = models().cubeAll(storageName, defaultStorageTexture);
				simpleBlockAndItem(storage, storageModel);
			}
		}

	}


	public void fenceBlock(Supplier<? extends FenceBlock> b, ResourceLocation texture)
	{
		super.fenceBlock(b.get(), texture);
		itemModel(b,
				models().withExistingParent(BuiltInRegistries.BLOCK.getKey(b.get()).getPath()+"_inventory", mcLoc("block/fence_inventory"))
						.texture("texture", texture));
	}

	private void createMultistateSingleModel(Supplier<? extends Block> block, ConfiguredModel model)
	{
		getVariantBuilder(block.get()).partialState().setModels(model);
	}


	public ModelFile createMetalLadder(String name, @Nullable ResourceLocation bottomTop, @Nullable ResourceLocation sides)
	{
		Map<String, ResourceLocation> textures = new HashMap<>();
		ResourceLocation parent;
		if(bottomTop!=null)
		{
			Preconditions.checkNotNull(sides);
			parent = new ResourceLocation(ImmersiveNuclear.MODID, "block/ie_scaffoldladder");
			textures.put("top", bottomTop);
			textures.put("bottom", bottomTop);
			textures.put("side", sides);
		}
		else
			parent = new ResourceLocation(ImmersiveNuclear.MODID, "block/ie_ladder");
		textures.put("ladder", rl("block/metal_decoration/metal_ladder"));
		BlockModelBuilder ret = models().withExistingParent(name, parent);
		for(Entry<String, ResourceLocation> e : textures.entrySet())
			ret.texture(e.getKey(), e.getValue());
		ret.renderType(ModelProviderUtils.getName(cutout()));
		return ret;
	}

	private void createDirectionalBlock(Supplier<? extends Block> b, Property<Direction> prop, ModelFile model)
	{
		VariantBlockStateBuilder builder = getVariantBuilder(b.get());
		for(Direction d : DirectionUtils.BY_HORIZONTAL_INDEX)
			builder.partialState()
					.with(prop, d)
					.setModels(new ConfiguredModel(model, 0, getAngle(d, 180), true));
	}

	protected ModelFile createMultiLayer(String path, Map<RenderType, ResourceLocation> modelGetter, ResourceLocation particle)
	{
		CompositeModelBuilder<BlockModelBuilder> modelBuilder = models().getBuilder(path)
				.customLoader(CompositeModelBuilder::begin);

		for(Entry<RenderType, ResourceLocation> entry : modelGetter.entrySet())
		{
			ResourceLocation rl = entry.getValue();
			String layer = ModelProviderUtils.getName(entry.getKey());
			modelBuilder.child(
					layer,
					obj(new BlockModelBuilder(rl("temp"), existingFileHelper), rl, ImmutableMap.of()).renderType(layer)
			);
		}
		return modelBuilder.end()
				.parent(new ExistingModelFile(mcLoc("block/block"), existingFileHelper))
				.texture("particle", DataGenUtils.getTextureFromObj(particle, existingFileHelper));
	}

	private ModelFile quarter(String out, ResourceLocation texture)
	{
		return models().withExistingParent(out, modLoc("block/ie_quarter_block"))
				.texture("texture", texture);
	}

	private ModelFile threeQuarter(String out, ResourceLocation texture)
	{
		return models().withExistingParent(out, modLoc("block/ie_three_quarter_block"))
				.texture("texture", texture);
	}


	private ModelFile createRouterModel(ResourceLocation baseTexName, String outName)
	{
		BlockModelBuilder builder = models().withExistingParent(outName, modLoc("block/ie_six_sides"));
		for(Direction d : DirectionUtils.VALUES)
			builder.texture(d.getSerializedName(), new ResourceLocation(baseTexName.getNamespace(),
					baseTexName.getPath()+"_"+d.ordinal()));
		builder.texture("particle", new ResourceLocation(baseTexName.getNamespace(),
				baseTexName.getPath()+"_0"));
		return builder;
	}
}
