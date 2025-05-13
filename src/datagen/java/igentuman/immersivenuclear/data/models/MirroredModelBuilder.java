package igentuman.immersivenuclear.data.models;

import blusunrize.immersiveengineering.client.models.mirror.MirroredModelLoader;
import com.google.gson.JsonObject;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class MirroredModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T>
{
	public static <T extends ModelBuilder<T>>
	MirroredModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper)
	{
		return new MirroredModelBuilder<>(parent, existingFileHelper);
	}

	private NongeneratedModels.NongeneratedModel inner;

	protected MirroredModelBuilder(T parent, ExistingFileHelper existingFileHelper)
	{
		super(MirroredModelLoader.ID, parent, existingFileHelper);
	}

	public MirroredModelBuilder<T> inner(NongeneratedModels.NongeneratedModel inner)
	{
		this.inner = inner;
		return this;
	}

	@Override
	public JsonObject toJson(JsonObject json)
	{
		JsonObject result = super.toJson(json);
		result.add(MirroredModelLoader.INNER_MODEL, inner.toJson());
		return result;
	}
}