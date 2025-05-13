/*
 * BluSunrize
 * Copyright (c) 2023
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.data.blockstates;

import blusunrize.immersiveengineering.api.IEProperties;
import igentuman.immersivenuclear.api.wires.INWireType;
import igentuman.immersivenuclear.common.register.INBlocks.Connectors;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;

import static igentuman.immersivenuclear.ImmersiveNuclear.rl;

import static net.minecraft.client.renderer.RenderType.translucent;

public class ConnectorBlockStates extends ExtendedBlockstateProvider
{
	public ConnectorBlockStates(PackOutput output, ExistingFileHelper exFileHelper)
	{
		super(output, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels()
	{
		createAllRotatedBlock(Connectors.getEnergyConnector(INWireType.EV_CATEGORY, false), obj("block/connector/connector_ev.obj"));
		createAllRotatedBlock(
				Connectors.getEnergyConnector(INWireType.EV_CATEGORY, true),
				obj("block/connector/relay_ev.obj", translucent())
		);

		transformerModel("block/connector/transformer_ev", Connectors.TRANSFORMER_EV);
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
