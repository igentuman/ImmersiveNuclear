package igentuman.immersivenuclear.common.util.compat.jei;

import java.util.ArrayList;

import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.api.crafting.CoolingTowerRecipe;
import igentuman.immersivenuclear.api.crafting.SulfurRecoveryRecipe;
import igentuman.immersivenuclear.client.gui.CoolingTowerScreen;
import igentuman.immersivenuclear.client.gui.HydrotreaterScreen;
import igentuman.immersivenuclear.common.IPContent;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class JEIStuff implements IModPlugin{
	private static final ResourceLocation ID = new ResourceLocation(ImmersiveNuclear.MODID, "main");
	
	@Override
	public ResourceLocation getPluginUid(){
		return ID;
	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registration){
		IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
		
		registration.addRecipeCategories(new CoolingTowerRecipeCategory(guiHelper));
		registration.addRecipeCategories(new SulfurRecoveryRecipeCategory(guiHelper));
	}
	
	@Override
	public void registerRecipes(IRecipeRegistration registration){
		registration.addRecipes(new ArrayList<>(CoolingTowerRecipe.recipes.values()), CoolingTowerRecipeCategory.ID);
		registration.addRecipes(new ArrayList<>(SulfurRecoveryRecipe.recipes.values()), SulfurRecoveryRecipeCategory.ID);
	}
	
	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration){
		registration.addRecipeCatalyst(new ItemStack(IPContent.Multiblock.distillationtower), CoolingTowerRecipeCategory.ID);
		registration.addRecipeCatalyst(new ItemStack(IPContent.Multiblock.hydrotreater), SulfurRecoveryRecipeCategory.ID);
	}
	
	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration){
		registration.addRecipeClickArea(CoolingTowerScreen.class, 85, 19, 18, 51, CoolingTowerRecipeCategory.ID);
		registration.addRecipeClickArea(HydrotreaterScreen.class, 55, 9, 32, 51, SulfurRecoveryRecipeCategory.ID);
	}
}
