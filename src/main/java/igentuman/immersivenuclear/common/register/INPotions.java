/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.common.register;

import blusunrize.immersiveengineering.api.IEApi;
import blusunrize.immersiveengineering.common.register.IEBlocks.StoneDecoration;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import igentuman.immersivenuclear.api.Lib;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;
import java.util.function.Consumer;

public class INPotions
{
	public static final DeferredRegister<MobEffect> REGISTER = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Lib.MODID);

	public static final RegistryObject<MobEffect> FLAMMABLE = REGISTER.register(
			"flammable", () -> new IEPotion(MobEffectCategory.HARMFUL, 0x8f3f1f, 0, false, 0, true, true)
	);


	static
	{
		IEApi.potions = ImmutableList.of(FLAMMABLE);
	}

	public static class IEPotion extends MobEffect
	{
		private static final Set<Block> concrete = ImmutableSet.<Block>builder()
				.add(StoneDecoration.CONCRETE.get())
				.add(StoneDecoration.CONCRETE_TILE.get())
				.add(StoneDecoration.CONCRETE_SPRAYED.get())
				.add(StoneDecoration.CONCRETE_THREE_QUARTER.get())
				.add(StoneDecoration.CONCRETE_SHEET.get())
				.add(StoneDecoration.CONCRETE_QUARTER.get())
				.add(StoneDecoration.CONCRETE_LEADED.get())
				.build();
		final int tickrate;
		final boolean halfTickRateWIthAmplifier;
		boolean showInInventory = true;
		boolean showInHud = true;

		public IEPotion(MobEffectCategory isBad, int colour, int tick, boolean halveTick, int icon, boolean showInInventory, boolean showInHud)
		{
			super(isBad, colour);
			this.showInInventory = showInInventory;
			this.showInHud = showInHud;
			this.tickrate = tick;
			this.halfTickRateWIthAmplifier = halveTick;
		}

		@Override
		public void initializeClient(Consumer<IClientMobEffectExtensions> consumer)
		{
			consumer.accept(new IClientMobEffectExtensions()
			{
				@Override
				public boolean isVisibleInGui(MobEffectInstance instance)
				{
					return showInHud;
				}

				@Override
				public boolean isVisibleInInventory(MobEffectInstance instance)
				{
					return showInInventory;
				}
			});
		}

		@Override
		public boolean isDurationEffectTick(int duration, int amplifier)
		{
			if(tickrate < 0)
				return false;
			int k = tickrate >> amplifier;
			return k <= 0||duration%k==0;
		}

		@Override
		public void applyEffectTick(LivingEntity living, int amplifier)
		{

		}
	}
}
