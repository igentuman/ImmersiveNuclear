/*
 * BluSunrize
 * Copyright (c) 2025
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.api.wires;

import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.WireType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class INWireType extends WireType
{
	public static final String EV_CATEGORY = "EV";

	@Override
	public String getUniqueName()
	{
		return "";
	}

	@Override
	public int getColour(Connection connection)
	{
		return 0;
	}

	@Override
	public double getSlack()
	{
		return 0;
	}

	@Override
	public int getMaxLength()
	{
		return 0;
	}

	@Override
	public ItemStack getWireCoil(Connection connection)
	{
		return null;
	}

	@Override
	public double getRenderDiameter()
	{
		return 0;
	}

	@Override
	public @NotNull String getCategory()
	{
		return "";
	}
}
