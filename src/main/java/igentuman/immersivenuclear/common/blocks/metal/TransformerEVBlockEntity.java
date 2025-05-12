/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.common.blocks.metal;

import blusunrize.immersiveengineering.api.wires.WireType;
import blusunrize.immersiveengineering.common.blocks.metal.TransformerBlockEntity;
import igentuman.immersivenuclear.common.register.INBlockEntities;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import static igentuman.immersivenuclear.api.wires.INWireType.EV_CATEGORY;

public class TransformerEVBlockEntity extends TransformerBlockEntity
{
	public TransformerEVBlockEntity(BlockPos pos, BlockState state)
	{
		super(INBlockEntities.TRANSFORMER_EV.get(), pos, state);
		acceptableLowerWires = ImmutableSet.of(WireType.HV_CATEGORY, WireType.MV_CATEGORY);
	}

	@Override
	protected float getLowerOffset()
	{
		return super.getHigherOffset();
	}

	@Override
	protected float getHigherOffset()
	{
		return .75F;
	}

	@Override
	public String getHigherWiretype()
	{
		return EV_CATEGORY;
	}
}