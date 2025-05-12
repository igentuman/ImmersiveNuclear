/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.common.util.compat.jei;

import blusunrize.immersiveengineering.api.crafting.AlloyRecipe;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import blusunrize.immersiveengineering.common.util.IELogger;
import blusunrize.immersiveengineering.common.util.compat.jei.IEFluidTooltipCallback;
import igentuman.immersivenuclear.api.Lib;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;
import java.util.function.Predicate;

@JeiPlugin
public class JEIHelper implements IModPlugin
{
	private static final ResourceLocation UID = new ResourceLocation(Lib.MODID, "main");
	public static final ResourceLocation JEI_GUI = new ResourceLocation(Lib.MODID, "textures/gui/jei_elements.png");
	public static IDrawableStatic slotDrawable;
	public static IRecipeSlotTooltipCallback fluidTooltipCallback = new IEFluidTooltipCallback();

	@Override
	public ResourceLocation getPluginUid()
	{
		return UID;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration subtypeRegistry)
	{

		/*subtypeRegistry.registerSubtypeInterpreter(
				VanillaTypes.ITEM_STACK, Misc.SHADER.asItem(), (stack, $) -> ItemNBTHelper.getString(stack, ShaderItem.SHADER_NAME_KEY)
		);*/

	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry)
	{
		//Recipes
		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
		/*registry.addRecipeCategories(
				new AlloySmelterRecipeCategory(guiHelper)
		);*/

		slotDrawable = guiHelper.getSlotDrawable();
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration)
	{
		IELogger.info("Adding recipes to JEI!!");
		//registration.addRecipes(JEIRecipeTypes.ALLOY, getRecipes(AlloyRecipe.RECIPES));
	}

	private <T extends Recipe<?>> List<T> getRecipes(CachedRecipeList<T> cachedList)
	{
		return getFiltered(cachedList, $ -> true);
	}

	private <T extends Recipe<?>> List<T> getFiltered(CachedRecipeList<T> cachedList, Predicate<T> include)
	{
		return cachedList.getRecipes(Minecraft.getInstance().level).stream()
				.filter(include)
				.toList();
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration)
	{
		//registration.addRecipeTransferHandler(new AssemblerRecipeTransferHandler(registration.getTransferHelper()), RecipeTypes.CRAFTING);

	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
	{
		//registration.addRecipeCatalyst(INMultiblockLogic.ALLOY_SMELTER.iconStack(), JEIRecipeTypes.ALLOY);
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration)
	{
		//registration.addRecipeClickArea(AlloySmelterScreen.class, 84, 35, 22, 16, JEIRecipeTypes.ALLOY);
		//registration.addGhostIngredientHandler(IEContainerScreen.class, new IEGhostItemHandler());
	}
}