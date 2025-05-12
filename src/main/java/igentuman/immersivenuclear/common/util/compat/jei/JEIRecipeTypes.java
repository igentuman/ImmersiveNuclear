/*
 * BluSunrize
 * Copyright (c) 2023
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.common.util.compat.jei;

import blusunrize.immersiveengineering.api.crafting.AlloyRecipe;
import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.api.crafting.*;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.world.item.crafting.Recipe;

public class JEIRecipeTypes
{
	//public static final RecipeType<AlloyRecipe> ALLOY = create(IERecipeTypes.ALLOY);

	private static <T extends Recipe<?>>
	RecipeType<T> create(IERecipeTypes.TypeWithClass<T> type)
	{
		return new RecipeType<>(type.type().getId(), type.recipeClass());
	}
}
