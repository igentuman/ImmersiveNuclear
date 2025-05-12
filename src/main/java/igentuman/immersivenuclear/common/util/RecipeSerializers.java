/*
 * BluSunrize
 * Copyright (c) 2020
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 *
 */

package igentuman.immersivenuclear.common.util;

import blusunrize.immersiveengineering.common.crafting.serializers.SimpleRecipeSerializer;
import igentuman.immersivenuclear.ImmersiveNuclear;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;
import java.util.function.Supplier;

public class RecipeSerializers
{
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(
			ForgeRegistries.RECIPE_SERIALIZERS, ImmersiveNuclear.MODID
	);
/*
	public static final RegistryObject<SimpleRecipeSerializer<PowerpackRecipe>> POWERPACK_SERIALIZER = RECIPE_SERIALIZERS.register(
			"powerpack", special(PowerpackRecipe::new)
	);*/

	static
	{
		/*AlloyRecipe.SERIALIZER = RECIPE_SERIALIZERS.register(
				"alloy", AlloyRecipeSerializer::new
		);*/
	}

	private static <T extends Recipe<?>> Supplier<SimpleRecipeSerializer<T>> special(Function<ResourceLocation, T> create)
	{
		return () -> new SimpleRecipeSerializer<>(create);
	}
}
