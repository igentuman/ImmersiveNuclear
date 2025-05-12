/*
 *  BluSunrize
 *  Copyright (c) 2021
 *
 *  This code is licensed under "Blu's License of Common Sense"
 *  Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.common.register;

import blusunrize.immersiveengineering.common.entities.IEExplosiveEntity;
import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.api.Lib;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class INEntityTypes
{
	public static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(
			ForgeRegistries.ENTITY_TYPES, Lib.MODID
	);

	public static final RegistryObject<EntityType<IEExplosiveEntity>> EXPLOSIVE = register(
			"explosive",
			() -> Builder.<IEExplosiveEntity>of(IEExplosiveEntity::new, MobCategory.MISC)
					.fireImmune()
					.sized(0.98F, 0.98F)
	);


	private static <T extends Entity>
	RegistryObject<EntityType<T>> register(String name, Supplier<Builder<T>> prepare)
	{
		return REGISTER.register(name, () -> prepare.get().build(ImmersiveNuclear.MODID+":"+name));
	}
}
