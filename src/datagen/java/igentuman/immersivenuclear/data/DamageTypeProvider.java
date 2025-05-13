/*
 * BluSunrize
 * Copyright (c) 2023
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.data;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.damagesource.DamageType;

public class DamageTypeProvider
{
	private static final float DEFAULT_EXHAUSTION = 0.1f;

	public static void bootstrap(BootstapContext<DamageType> ctx)
	{
//		ctx.register(DamageTypes.WIRE_SHOCK, new DamageType("ieWireShock", DEFAULT_EXHAUSTION));
	}

}
