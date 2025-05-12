/*
 * BluSunrize
 * Copyright (c) 2020
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.data;

import blusunrize.immersiveengineering.api.IETags;
import igentuman.immersivenuclear.api.Lib;
import igentuman.immersivenuclear.common.register.INFluids;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

class FluidTags extends FluidTagsProvider
{
	public FluidTags(PackOutput output, CompletableFuture<Provider> lookupProvider, ExistingFileHelper existingFileHelper)
	{
		super(output, lookupProvider, Lib.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider p_256366_)
	{
		tag(IETags.fluidCreosote).add(INFluids.CREOSOTE.getStill());
		tag(IETags.fluidPlantoil).add(INFluids.PLANTOIL.getStill());

		tag(IETags.drillFuel).addTag(IETags.fluidBiodiesel);
		//tag(Fluids.GASEOUS).add(INFluids.ACETALDEHYDE.getStill());
	}
}
