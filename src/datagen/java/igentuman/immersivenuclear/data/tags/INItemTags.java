/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.data.tags;

import com.google.common.base.Preconditions;
import igentuman.immersivenuclear.api.INEnumMetals;
import igentuman.immersivenuclear.api.INTags;
import igentuman.immersivenuclear.common.register.INItems;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static igentuman.immersivenuclear.api.Lib.MODID;

public class INItemTags extends ItemTagsProvider
{

	public INItemTags(
			PackOutput output,
			CompletableFuture<Provider> lookupProvider,
			CompletableFuture<TagLookup<Block>> blocks,
			ExistingFileHelper existingFileHelper
	)
	{
		super(output, lookupProvider, blocks, MODID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider p_256380_)
	{
		INTags.forAllBlocktags(this::copy);
		for(INEnumMetals metal : INEnumMetals.values())
		{
			INTags.MetalTags tags = INTags.getTagsFor(metal);
			if(metal.shouldAddNugget())
			{
				tag(tags.nugget).add(INItems.Metals.NUGGETS.get(metal).get());
				tag(Tags.Items.NUGGETS).addTag(tags.nugget);
			}
			if(!metal.isVanillaMetal())
			{
				tag(tags.ingot).add(INItems.Metals.INGOTS.get(metal).get());
				tag(Tags.Items.INGOTS).addTag(tags.ingot);
			}
			if(metal.shouldAddOre())
			{
				Preconditions.checkNotNull(tags.rawOre);
				tag(tags.rawOre).add(INItems.Metals.RAW_ORES.get(metal).get());
				tag(Tags.Items.RAW_MATERIALS).addTag(tags.rawOre);
			}
			if(!metal.isIsotope()) {
				tag(tags.plate).add(INItems.Metals.PLATES.get(metal).get());
				tag(INTags.plates).addTag(tags.plate);
				tag(tags.dust).add(INItems.Metals.DUSTS.get(metal).get());
				tag(Tags.Items.DUSTS).addTag(tags.dust);
			}
		}
	}
}
