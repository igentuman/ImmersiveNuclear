package igentuman.immersivenuclear.data.models;

import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import static igentuman.immersivenuclear.api.Lib.MODID;


public abstract class TRSRItemModelProvider extends ModelProvider<TRSRModelBuilder>
{
	public TRSRItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper)
	{
		super(output, MODID, ITEM_FOLDER, TRSRModelBuilder::new, existingFileHelper);
	}
}