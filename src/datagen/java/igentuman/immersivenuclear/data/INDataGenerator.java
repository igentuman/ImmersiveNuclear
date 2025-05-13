/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.data;

import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.api.Lib;
import igentuman.immersivenuclear.data.blockstates.BlockStates;
import igentuman.immersivenuclear.data.blockstates.ConnectorBlockStates;
import igentuman.immersivenuclear.data.blockstates.MultiblockStates;
import igentuman.immersivenuclear.data.loot.AllLoot;
import igentuman.immersivenuclear.data.tags.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = ImmersiveNuclear.MODID, bus = Bus.MOD)
public class INDataGenerator
{
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event)
	{
		ExistingFileHelper exHelper = event.getExistingFileHelper();
		DataGenerator gen = event.getGenerator();
		final var output = gen.getPackOutput();
		final var lookup = event.getLookupProvider();
		if(event.includeServer())
		{
			BlockTagsProvider blockTags = new INBlockTags(output, lookup, exHelper);
			gen.addProvider(true, blockTags);
			gen.addProvider(true, new INItemTags(output, lookup, blockTags.contentsGetter(), exHelper));
			gen.addProvider(true, new FluidTags(output, lookup, exHelper));
			gen.addProvider(true, new BlockEntityTags(output, lookup, exHelper));
			gen.addProvider(true, new BannerTags(output, lookup, exHelper));
			gen.addProvider(true, new PoiTags(output, lookup, exHelper));
			gen.addProvider(true, new EntityTypeTags(output, lookup, exHelper));
			gen.addProvider(true, new INRecipes(output));
			gen.addProvider(true, new AllLoot(output));
			gen.addProvider(true, new BlockStates(output, exHelper));
			MultiblockStates multiblocks = new MultiblockStates(output, exHelper);
			gen.addProvider(true, multiblocks);
			gen.addProvider(true, new ConnectorBlockStates(output, exHelper));
			gen.addProvider(true, new ItemModels(output, exHelper, multiblocks));
			gen.addProvider(true, new Advancements(output, lookup, exHelper));
			gen.addProvider(true, new StructureUpdater("structures/multiblocks", Lib.MODID, exHelper, output));
			gen.addProvider(true, new StructureUpdater("structures/village", Lib.MODID, exHelper, output));
			gen.addProvider(true, new DynamicModels(multiblocks, output, exHelper));
			for(final DataProvider provider : WorldGenerationProvider.makeProviders(output, lookup, exHelper))
				gen.addProvider(true, provider);
		}
	}
}
