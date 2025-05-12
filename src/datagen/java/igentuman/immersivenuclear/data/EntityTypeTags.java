/*
 * BluSunrize
 * Copyright (c) 2022
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 *
 */

package igentuman.immersivenuclear.data;

import igentuman.immersivenuclear.api.IETags;
import igentuman.immersivenuclear.api.Lib;
import net.minecraft.core.HolderLookup.Provider;
import igentuman.immersivenuclear.common.register.INEntityTypes;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class EntityTypeTags extends EntityTypeTagsProvider
{

	public EntityTypeTags(PackOutput output, CompletableFuture<Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper)
	{
		super(output, lookupProvider, Lib.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider p_255894_)
	{
		tag(IETags.shaderbagBlacklist).add(EntityType.WITHER).add(EntityType.IRON_GOLEM);
		tag(net.minecraft.tags.EntityTypeTags.RAIDERS).add(INEntityTypes.FUSILIER.get(), INEntityTypes.COMMANDO.get(), INEntityTypes.BULWARK.get());
	}


	@Nonnull
	@Override
	public String getName()
	{
		return "IE entity tags";
	}
}
