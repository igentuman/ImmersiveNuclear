/*
 * BluSunrize
 * Copyright (c) 2023
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.data.blockstates;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.wires.WireType;
import igentuman.immersivenuclear.client.models.connection.FeedthroughLoader;
import igentuman.immersivenuclear.client.models.obj.callback.block.*;
import igentuman.immersivenuclear.common.register.INBlocks.Cloth;
import igentuman.immersivenuclear.common.register.INBlocks.Connectors;
import igentuman.immersivenuclear.data.models.SpecialModelBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;

import static igentuman.immersivenuclear.ImmersiveNuclear.rl;
import static net.minecraft.client.renderer.RenderType.*;

public class ConnectorBlockStates extends ExtendedBlockstateProvider
{
	public ConnectorBlockStates(PackOutput output, ExistingFileHelper exFileHelper)
	{
		super(output, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels()
	{
		createAllRotatedBlock(
				Connectors.getEnergyConnector(WireType.LV_CATEGORY, false), obj(
						"block/connector/connector_lv", rl("block/connector/connector_lv.obj"),
						ImmutableMap.of("texture", modLoc("block/connector/connector_lv")),
						models()
				)
		);
		createAllRotatedBlock(Connectors.getEnergyConnector(WireType.LV_CATEGORY, true), obj(
				"block/connector/relay_lv", rl("block/connector/connector_lv.obj"),
				ImmutableMap.of("texture", modLoc("block/connector/relay_lv")),
				models()
		));

		createAllRotatedBlock(Connectors.getEnergyConnector(WireType.MV_CATEGORY, false), obj(
				"block/connector/connector_mv", rl("block/connector/connector_mv.obj"),
				ImmutableMap.of("texture", modLoc("block/connector/connector_mv")),
				models()
		));
		createAllRotatedBlock(Connectors.getEnergyConnector(WireType.MV_CATEGORY, true), obj(
				"block/connector/relay_mv", rl("block/connector/connector_mv.obj"),
				ImmutableMap.of("texture", modLoc("block/connector/relay_mv")),
				models()
		));

		createAllRotatedBlock(Connectors.getEnergyConnector(WireType.HV_CATEGORY, false), obj("block/connector/connector_hv.obj"));
		createAllRotatedBlock(
				Connectors.getEnergyConnector(WireType.HV_CATEGORY, true),
				obj("block/connector/relay_hv.obj", translucent())
		);

		createAllRotatedBlock(Connectors.CONNECTOR_BUNDLED, obj("block/connector/connector_bundled.obj", cutout()));
		ModelFile feedthroughModelFile = models().getBuilder("block/connector/feedthrough")
				.customLoader(SpecialModelBuilder.forLoader(FeedthroughLoader.LOCATION))
				.end();
		createAllRotatedBlock(Connectors.FEEDTHROUGH, feedthroughModelFile);

		transformerModel("block/connector/transformer_mv", Connectors.TRANSFORMER);
		transformerModel("block/connector/transformer_hv", Connectors.TRANSFORMER_HV);
		createHorizontalRotatedBlock(Connectors.POST_TRANSFORMER, obj("block/connector/transformer_post.obj"), 0);

		ModelFile ctModel = split(innerObj("block/connector/e_meter.obj"), ImmutableList.of(BlockPos.ZERO, new BlockPos(0, -1, 0)));
		createHorizontalRotatedBlock(Connectors.CURRENT_TRANSFORMER, ctModel, 0);

	}

	private void transformerModel(String baseName, Supplier<? extends Block> transformer)
	{
		ModelFile leftModel = split(innerObj(baseName+"_left.obj"), COLUMN_THREE);
		ModelFile rightModel = split(mirror(innerObj(baseName+"_left.obj"), innerModels), COLUMN_THREE);
		createRotatedBlock(
				transformer,
				state -> state.getSetStates().get(IEProperties.MIRRORED)==Boolean.TRUE?rightModel: leftModel,
				IEProperties.FACING_HORIZONTAL,
				List.of(IEProperties.MIRRORED),
				0, 0
		);
	}

	@Nonnull
	@Override
	public String getName()
	{
		return "Connector models/block states";
	}
}
