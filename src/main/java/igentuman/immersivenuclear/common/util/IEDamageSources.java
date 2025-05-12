/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.common.util;

import blusunrize.immersiveengineering.api.Lib.TurretDamageType;
import blusunrize.immersiveengineering.api.tool.IElectricEquipment;
import blusunrize.immersiveengineering.api.wires.utils.IElectricDamageSource;
import blusunrize.immersiveengineering.mixin.accessors.DamageSourcesAccess;
import igentuman.immersivenuclear.api.Lib;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class IEDamageSources
{
	public static class ElectricDamageSource extends DamageSource implements IElectricDamageSource
	{
		public IElectricEquipment.ElectricSource source;
		public float dmg;

		public ElectricDamageSource(Holder<DamageType> tag, IElectricEquipment.ElectricSource source, float amount)
		{
			super(tag);
			this.source = source;
			dmg = amount;
		}

		@Override
		public boolean apply(Entity e)
		{
			if(e instanceof LivingEntity living)
				IElectricEquipment.applyToEntity(living, this, source);
			if(dmg > 0)
				e.hurt(this, dmg);
			return dmg > 0;
		}

		@Override
		public float getDamage()
		{
			return dmg;
		}
	}


	/*public static DamageSource causeTeslaPrimaryDamage(Level level)
	{
		return sources(level).invokeSource(Lib.DamageTypes.TESLA_PRIMARY, null, null);
	}*/

	private static DamageSource maybeTurret(Entity shot, @Nullable Entity shooter, TurretDamageType type)
	{
		if(shooter==null)
			return sources(shot).invokeSource(type.turretType(), shot, null);
		return sources(shot).invokeSource(type.playerType(), shot, shooter);
	}

	private static DamageSourcesAccess sources(Level level)
	{
		return (DamageSourcesAccess)level.damageSources();
	}

	private static DamageSourcesAccess sources(Entity entity)
	{
		return sources(entity.level());
	}

	private static Holder<DamageType> typeHolder(Level level, ResourceKey<DamageType> typeKey)
	{
		final Registry<DamageType> registry = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
		return registry.getHolderOrThrow(typeKey);
	}
}