/*
 * BluSunrize
 * Copyright (c) 2023
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.data.blockstates;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import com.google.common.base.Preconditions;
import igentuman.immersivenuclear.data.models.NongeneratedModels;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder.PartialBlockstate;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MultiblockStates extends ExtendedBlockstateProvider
{
	private static final List<Vec3i> CUBE_THREE = BlockPos.betweenClosedStream(-1, -1, -1, 1, 1, 1)
			.map(BlockPos::immutable)
			.collect(Collectors.toList());
	private static final List<Vec3i> CUBE_TWO = BlockPos.betweenClosedStream(0, 0, -1, 1, 1, 0)
			.map(BlockPos::immutable)
			.collect(Collectors.toList());

	public final Map<Block, ModelFile> unsplitModels = new HashMap<>();
	public ModelFile blastFurnaceOff;
	public ModelFile blastFurnaceOn;
	public ModelFile cokeOvenOff;
	public ModelFile cokeOvenOn;
	public ModelFile alloySmelterOff;
	public ModelFile alloySmelterOn;

	public MultiblockStates(PackOutput output, ExistingFileHelper exFileHelper)
	{
		super(output, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels()
	{
		createMetalMultiblocks();

		/*createMultiblock(
				MetalDevices.TESLA_COIL,
				split(
						innerObj("block/metal_device/teslacoil.obj"),
						ImmutableList.of(BlockPos.ZERO, new BlockPos(0, 0, -1))
				),
				null, IEProperties.FACING_ALL, null
		);
		createMultiblock(
				MetalDevices.BLAST_FURNACE_PREHEATER,
				split(innerObj("block/metal_device/blastfurnace_preheater.obj"), COLUMN_THREE)
		);*/
	}

	private void createMetalMultiblocks()
	{
		/*createMultiblock(innerObj("block/metal_multiblock/sawmill.obj"), IEMultiblocks.SAWMILL);
		createMultiblock(innerObj("block/metal_multiblock/excavator.obj"), IEMultiblocks.EXCAVATOR);
		createMultiblock(innerObj("block/metal_multiblock/crusher.obj"), IEMultiblocks.CRUSHER);
		createMultiblock(innerObj("block/metal_multiblock/metal_press.obj"), IEMultiblocks.METAL_PRESS);
		createMultiblock(innerObj("block/metal_multiblock/assembler.obj"), IEMultiblocks.ASSEMBLER);
		createMultiblock(innerObj("block/metal_multiblock/arc_furnace.obj"), IEMultiblocks.ARC_FURNACE);

		createMultiblock(innerObj("block/blastfurnace_advanced.obj"), IEMultiblocks.ADVANCED_BLAST_FURNACE);
		createMultiblock(innerObj("block/metal_multiblock/silo.obj"), IEMultiblocks.SILO);
		createMultiblock(innerObj("block/metal_multiblock/tank.obj", cutoutMipped()), IEMultiblocks.SHEETMETAL_TANK);

		createMultiblock(innerObj("block/metal_multiblock/fermenter.obj"), IEMultiblocks.FERMENTER);
		createMultiblock(innerObj("block/metal_multiblock/squeezer.obj"), IEMultiblocks.SQUEEZER);
		createMultiblock(innerObj("block/metal_multiblock/mixer.obj"), IEMultiblocks.MIXER);
		createMultiblock(innerObj("block/metal_multiblock/refinery.obj"), IEMultiblocks.REFINERY);
		createMultiblock(innerObj("block/metal_multiblock/diesel_generator.obj", cutoutMipped()), IEMultiblocks.DIESEL_GENERATOR);*/

		/*createMultiblock(WoodenDevices.CIRCUIT_TABLE,
				split(innerObj("block/wooden_device/circuit_table.obj"), ImmutableList.of(
						ModWorkbenchBlockEntity.MASTER_POS, ModWorkbenchBlockEntity.DUMMY_POS
				)),
				null, null);
		createMultiblock(MetalDevices.SAMPLE_DRILL,
				split(
						innerObj("block/metal_device/core_drill.obj", cutout()),
						ImmutableList.of(BlockPos.ZERO, BlockPos.ZERO.above(), BlockPos.ZERO.above(2))
				),
				null, null);*/
		/*createMultiblock(innerObj("block/metal_multiblock/auto_workbench.obj"), IEMultiblocks.AUTO_WORKBENCH);*/
	}

	@Nonnull
	@Override
	public String getName()
	{
		return "Multiblock models/block states";
	}


	private void createMultiblock(NongeneratedModels.NongeneratedModel unsplitModel, IETemplateMultiblock multiblock)
	{
		createMultiblock(unsplitModel, multiblock, false);
	}

	private void createDynamicMultiblock(NongeneratedModels.NongeneratedModel unsplitModel, IETemplateMultiblock multiblock)
	{
		createMultiblock(unsplitModel, multiblock, true);
	}

	private void createMultiblock(NongeneratedModels.NongeneratedModel unsplitModel, IETemplateMultiblock multiblock, boolean dynamic)
	{
		/*final ModelFile mainModel = split(unsplitModel, multiblock, false, dynamic);
		if(multiblock.getBlock().getStateDefinition().getProperties().contains(IEProperties.MIRRORED))
			createMultiblock(
					multiblock::getBlock,
					mainModel,
					split(mirror(unsplitModel, innerModels), multiblock, true, dynamic),
					IEProperties.FACING_HORIZONTAL, IEProperties.MIRRORED
			);
		else
			createMultiblock(multiblock::getBlock, mainModel, null, IEProperties.FACING_HORIZONTAL, null);*/
	}

	private void createMultiblock(Supplier<? extends Block> b, ModelFile masterModel)
	{
		createMultiblock(b, masterModel, null, IEProperties.FACING_HORIZONTAL, null);
	}

	private void createMultiblock(Supplier<? extends Block> b, ModelFile masterModel, @Nullable ModelFile mirroredModel,
								  @Nullable Property<Boolean> mirroredState)
	{
		createMultiblock(b, masterModel, mirroredModel, IEProperties.FACING_HORIZONTAL, mirroredState);
	}

	private void createMultiblock(Supplier<? extends Block> b, ModelFile masterModel, @Nullable ModelFile mirroredModel,
								  EnumProperty<Direction> facing, @Nullable Property<Boolean> mirroredState)
	{
		unsplitModels.put(b.get(), masterModel);
		Preconditions.checkArgument((mirroredModel==null)==(mirroredState==null));
		VariantBlockStateBuilder builder = getVariantBuilder(b.get());
		boolean[] possibleMirrorStates;
		if(mirroredState!=null)
			possibleMirrorStates = new boolean[]{false, true};
		else
			possibleMirrorStates = new boolean[1];
		for(boolean mirrored : possibleMirrorStates)
			for(Direction dir : facing.getPossibleValues())
			{
				final int angleY;
				final int angleX;
				if(facing.getPossibleValues().contains(Direction.UP))
				{
					angleX = -90*dir.getStepY();
					if(dir.getAxis()!=Axis.Y)
						angleY = getAngle(dir, 180);
					else
						angleY = 0;
				}
				else
				{
					angleY = getAngle(dir, 180);
					angleX = 0;
				}
				ModelFile model = mirrored?mirroredModel: masterModel;
				PartialBlockstate partialState = builder.partialState()
						.with(facing, dir);
				if(mirroredState!=null)
					partialState = partialState.with(mirroredState, mirrored);
				partialState.setModels(new ConfiguredModel(model, angleX, angleY, true));
			}
	}


	private void loadTemplateFor(TemplateMultiblock multiblock)
	{
		final ResourceLocation name = multiblock.getUniqueName();
		if(TemplateMultiblock.SYNCED_CLIENT_TEMPLATES.containsKey(name))
			return;
		final String filePath = "structures/"+name.getPath()+".nbt";
		int slash = filePath.indexOf('/');
		String prefix = filePath.substring(0, slash);
		ResourceLocation shortLoc = new ResourceLocation(
				name.getNamespace(),
				filePath.substring(slash+1)
		);
		try
		{
			final Resource resource = existingFileHelper.getResource(shortLoc, PackType.SERVER_DATA, "", prefix);
			try(final InputStream input = resource.open())
			{
				final CompoundTag nbt = NbtIo.readCompressed(input);
				final StructureTemplate template = new StructureTemplate();
				template.load(BuiltInRegistries.BLOCK.asLookup(), nbt);
				TemplateMultiblock.SYNCED_CLIENT_TEMPLATES.put(name, template);
			}
		} catch(IOException e)
		{
			throw new RuntimeException("Failed on "+name, e);
		}
	}
}
