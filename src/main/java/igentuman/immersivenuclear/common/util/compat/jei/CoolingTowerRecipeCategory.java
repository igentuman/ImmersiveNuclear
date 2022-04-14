package igentuman.immersivenuclear.common.util.compat.jei;

import java.util.ArrayList;
import java.util.List;

import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.api.crafting.CoolingTowerRecipe;
import igentuman.immersivenuclear.common.IPContent;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class CoolingTowerRecipeCategory extends IPRecipeCategory<CoolingTowerRecipe>{
	public static final ResourceLocation ID = new ResourceLocation(ImmersiveNuclear.MODID, "distillation");
	
	private final IDrawableStatic tankOverlay;
	public CoolingTowerRecipeCategory(IGuiHelper guiHelper){
		super(CoolingTowerRecipe.class, guiHelper, ID, "block.immersivenuclear.distillationtower");
		ResourceLocation background = new ResourceLocation(ImmersiveNuclear.MODID, "textures/gui/coolingtower.png");
		setBackground(guiHelper.createDrawable(background, 51, 0, 81, 77));
		setIcon(new ItemStack(IPContent.Multiblock.distillationtower));
		this.tankOverlay = guiHelper.createDrawable(background, 177, 31, 20, 51);
	}
	
	@Override
	public void setIngredients(CoolingTowerRecipe recipe, IIngredients ingredients){
		List<FluidStack> out = new ArrayList<>();
		for(FluidStack fluid:recipe.getFluidOutputs()){
			if(fluid != null)
				out.add(fluid);
		}
		
		ingredients.setInputs(VanillaTypes.FLUID, recipe.getInputFluid().getMatchingFluidStacks());
		ingredients.setOutputs(VanillaTypes.FLUID, out);
	}
	
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CoolingTowerRecipe recipe, IIngredients ingredients){
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
		
		if(recipe.getInputFluid() != null){
			int total = 0;
			List<FluidStack> list = recipe.getInputFluid().getMatchingFluidStacks();
			if(!list.isEmpty()){
				for(FluidStack f:list){
					total += f.getAmount();
				}
			}else{
				total = 100;
			}
			guiFluidStacks.init(0, true, 9, 19, 20, 51, total, false, this.tankOverlay);
			guiFluidStacks.set(0, list);
		}
		
		int total = 0;
		List<FluidStack> list = recipe.getFluidOutputs();
		if(!list.isEmpty()){
			for(FluidStack f:list){
				total += f.getAmount();
			}
		}else{
			total = 100;
		}
		guiFluidStacks.init(1, false, 61, 21, 16, 47, total, false, null);
		guiFluidStacks.set(1, list);
	}
}
