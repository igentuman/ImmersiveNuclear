/*
 * BluSunrize
 * Copyright (c) 2023
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.common.register;

import igentuman.immersivenuclear.api.Lib;
import igentuman.immersivenuclear.common.register.INItems.ItemRegObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class INBannerPatterns
{
	private static final DeferredRegister<BannerPattern> REGISTER = DeferredRegister.create(
			Registries.BANNER_PATTERN, Lib.MODID
	);
	public static final List<BannerEntry> ALL_BANNERS = new ArrayList<>();
	public static final BannerEntry WOLF = addBanner("wolf", "wlf");

	public static void init()
	{
		REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	private static BannerEntry addBanner(String name, String hashName)
	{
		RegistryObject<BannerPattern> pattern = REGISTER.register(name, () -> new BannerPattern("ie_"+hashName));
		TagKey<BannerPattern> tag = TagKey.create(Registries.BANNER_PATTERN, pattern.getId());
		ItemRegObject<BannerPatternItem> item = INItems.register("bannerpattern_"+name, () -> new BannerPatternItem(
				tag, new Properties()
		));
		BannerEntry result = new BannerEntry(pattern, tag, item);
		ALL_BANNERS.add(result);
		return result;
	}

	public record BannerEntry(
			RegistryObject<BannerPattern> pattern,
			TagKey<BannerPattern> tag,
			INItems.ItemRegObject<BannerPatternItem> item
	)
	{
	}
}
