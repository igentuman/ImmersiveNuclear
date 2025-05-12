/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.common.util;

import igentuman.immersivenuclear.ImmersiveNuclear;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static igentuman.immersivenuclear.ImmersiveNuclear.MODID;

public class IESounds
{
	private static final DeferredRegister<SoundEvent> REGISTER = DeferredRegister.create(
			Registries.SOUND_EVENT, MODID
	);

	public static final RegistryObject<SoundEvent> electromagnet = registerSound("electromagnet");


	public static void init()
	{
		REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	private static RegistryObject<SoundEvent> registerSound(String name)
	{
		return REGISTER.register(name, () -> SoundEvent.createVariableRangeEvent(ImmersiveNuclear.rl(name)));
	}
}
