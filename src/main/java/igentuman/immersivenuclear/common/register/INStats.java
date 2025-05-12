/*
 * BluSunrize
 * Copyright (c) 2022
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.common.register;

import igentuman.immersivenuclear.ImmersiveNuclear;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class INStats
{
	private static final DeferredRegister<ResourceLocation> REGISTER = DeferredRegister.create(
			Registries.CUSTOM_STAT, ImmersiveNuclear.MODID
	);

	//public static final RegistryObject<ResourceLocation> WIRE_DEATHS = registerCustomStat("wire_deaths", StatFormatter.DEFAULT);

	public static void modConstruction()
	{
		REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	public static void setup()
	{
	}

	private static RegistryObject<ResourceLocation> registerCustomStat(String name, StatFormatter formatter)
	{
		return REGISTER.register(name, () -> {
			ResourceLocation regName = ImmersiveNuclear.rl(name);
			return regName;
		});
	}
}
