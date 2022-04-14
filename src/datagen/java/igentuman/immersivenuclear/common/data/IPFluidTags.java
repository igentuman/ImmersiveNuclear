package igentuman.immersivenuclear.common.data;

import blusunrize.immersiveengineering.api.IETags;
import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.api.IPTags;
import igentuman.immersivenuclear.common.IPContent;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.FluidTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class IPFluidTags extends FluidTagsProvider{
	
	public IPFluidTags(DataGenerator gen, ExistingFileHelper exHelper){
		super(gen, ImmersiveNuclear.MODID, exHelper);
	}
	
	@Override
	protected void registerTags(){
		getOrCreateBuilder(IPTags.Fluids.steam)
			.add(IPContent.Fluids.steam)
			.add(IPContent.Fluids.high_quality_steam)
			.add(IPContent.Fluids.steam_vapor);

		getOrCreateBuilder(IPTags.Fluids.water)
				.add(IPContent.Fluids.technological_water);

		getOrCreateBuilder(IPTags.Fluids.sodium)
			.add(IPContent.Fluids.sodium)
			.add(IPContent.Fluids.heated_sodium);

		getOrCreateBuilder(IPTags.Fluids.lead)
				.add(IPContent.Fluids.melted_lead)
				.add(IPContent.Fluids.heated_melted_lead);

	}
}
