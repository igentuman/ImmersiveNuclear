/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.common.world;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.common.world.IECountPlacement;
import blusunrize.immersiveengineering.common.world.IEHeightProvider;
import blusunrize.immersiveengineering.common.world.IEOreFeature;
import igentuman.immersivenuclear.ImmersiveNuclear;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class IEWorldGen
{
	private static final DeferredRegister<Feature<?>> FEATURE_REGISTER = DeferredRegister.create(ForgeRegistries.FEATURES, ImmersiveEngineering.MODID);

	public static final RegistryObject<IEOreFeature> IE_CONFIG_ORE = FEATURE_REGISTER.register(
			"ie_ore", IEOreFeature::new
	);

	private static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_REGISTER = DeferredRegister.create(
			Registries.PLACEMENT_MODIFIER_TYPE, ImmersiveNuclear.MODID
	);
	public static RegistryObject<PlacementModifierType<IECountPlacement>> IE_COUNT_PLACEMENT = PLACEMENT_REGISTER.register(
			"ie_count", () -> () -> IECountPlacement.CODEC
	);

	private static final DeferredRegister<HeightProviderType<?>> HEIGHT_REGISTER = DeferredRegister.create(
			Registries.HEIGHT_PROVIDER_TYPE, ImmersiveNuclear.MODID
	);
	public static RegistryObject<HeightProviderType<IEHeightProvider>> IE_HEIGHT_PROVIDER = HEIGHT_REGISTER.register(
			"ie_range", () -> () -> IEHeightProvider.CODEC
	);

	public static void init()
	{
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		FEATURE_REGISTER.register(bus);
		PLACEMENT_REGISTER.register(bus);
		HEIGHT_REGISTER.register(bus);
	}
}
