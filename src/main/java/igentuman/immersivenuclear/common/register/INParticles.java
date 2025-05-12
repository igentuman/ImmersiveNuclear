/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.common.register;

import igentuman.immersivenuclear.api.Lib;
import blusunrize.immersiveengineering.client.fx.*;
import igentuman.immersivenuclear.ImmersiveNuclear;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class INParticles
{
	public static final DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create(
			ForgeRegistries.PARTICLE_TYPES, Lib.MODID
	);

	public static final RegistryObject<SimpleParticleType> SPARKS = REGISTER.register(
			"sparks", () -> new SimpleParticleType(false)
	);

	@EventBusSubscriber(modid = ImmersiveNuclear.MODID, bus = Bus.MOD, value = Dist.CLIENT)
	private static class Client
	{
		@SubscribeEvent
		public static void registerParticleFactories(RegisterParticleProvidersEvent event)
		{
			event.registerSpriteSet(INParticles.SPARKS.get(), SparksParticle.Factory::new);
		}
	}
}
