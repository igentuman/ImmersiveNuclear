/*
 * BluSunrize
 * Copyright (c) 2020
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 *
 */

package igentuman.immersivenuclear.common.blocks.metal;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.common.blocks.generic.ConnectorBlock;
import igentuman.immersivenuclear.common.register.INBlockEntities;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Properties;

public class TransformerEVBlock extends ConnectorBlock<TransformerEVBlockEntity>
{
	public TransformerEVBlock(Properties props)
	{
		super(props, INBlockEntities.TRANSFORMER_EV);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(IEProperties.FACING_HORIZONTAL, IEProperties.MULTIBLOCKSLAVE, IEProperties.MIRRORED, BlockStateProperties.WATERLOGGED);
	}

	@Override
	public boolean canIEBlockBePlaced(BlockState newState, BlockPlaceContext context)
	{
		return areAllReplaceable(context.getClickedPos(), context.getClickedPos().above(2), context);
	}
}
