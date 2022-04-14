package igentuman.immersivenuclear.common.data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import blusunrize.immersiveengineering.api.EnumMetals;
import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.GeneratorFuelBuilder;
import blusunrize.immersiveengineering.api.crafting.builders.MixerRecipeBuilder;
import blusunrize.immersiveengineering.common.blocks.IEBlocks;
import blusunrize.immersiveengineering.common.crafting.fluidaware.IngredientFluidStack;
import blusunrize.immersiveengineering.common.items.IEItems;
import blusunrize.immersiveengineering.data.recipebuilder.FluidAwareShapedRecipeBuilder;
import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.api.IPTags;
import igentuman.immersivenuclear.api.crafting.builders.CoolingTowerRecipeBuilder;
import igentuman.immersivenuclear.api.crafting.builders.SulfurRecoveryRecipeBuilder;
import igentuman.immersivenuclear.common.IPContent;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

public class IPRecipes extends RecipeProvider{
	private final Map<String, Integer> PATH_COUNT = new HashMap<>();
	
	protected Consumer<IFinishedRecipe> out;
	public IPRecipes(DataGenerator generatorIn){
		super(generatorIn);
	}
	
	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> out){
		this.out = out;
		
		itemRecipes();
		blockRecipes();
		hydrotreaterRecipes();
		coolingTowerRecipes();

		MixerRecipeBuilder.builder(IPContent.Fluids.steam, 500)
			.addFluidTag(IPTags.Fluids.gasoline, 500)
			.addInput(new IngredientWithSize(IETags.getTagsFor(EnumMetals.ALUMINUM).dust, 3))
			.setEnergy(3200)
			.build(this.out, rl("mixer/napalm"));
		
		GeneratorFuelBuilder.builder(IPTags.Fluids.diesel, 320)
			.build(this.out, rl("fuels/diesel"));
		GeneratorFuelBuilder.builder(IPTags.Fluids.diesel_sulfur, 320)
			.build(this.out, rl("fuels/diesel_sulfur"));
	}
	

	
	private void coolingTowerRecipes(){
		// setEnergy and setTime are 2048 and 1 by default. But still allows to be customized.
		
		CoolingTowerRecipeBuilder.builder(new FluidStack[]{
				new FluidStack(IPContent.Fluids.steam_vapor, 9),
						FluidStack.EMPTY,
						FluidStack.EMPTY,
				})
			.addInput(IPTags.Fluids.crudeOil, 75)
			.setTimeAndEnergy(1, 256)
			.build(this.out, rl("coolingtower/technological_water"));
	}

	
	private void hydrotreaterRecipes(){
		SulfurRecoveryRecipeBuilder.builder(new FluidStack(IPContent.Fluids.steam_vapor, 7), 512, 1)
			.addInputFluid(new FluidTagInput(IPTags.Fluids.diesel_sulfur, 7))
			.addSecondaryInputFluid(FluidTags.WATER, 7)
			.addItemWithChance(new ItemStack(IEItems.Ingredients.dustSulfur), 0.02)
			.build(out, rl("hydrotreater/sulfur_recovery"));
	}

	private void blockRecipes(){
		FluidAwareShapedRecipeBuilder.builder(IPContent.Blocks.asphalt, 8)
			.key('C', IPContent.Items.bitumen)
			.key('S', Tags.Items.SAND)
			.key('G', Tags.Items.GRAVEL)
			.key('B', new IngredientFluidStack(FluidTags.WATER, FluidAttributes.BUCKET_VOLUME))
			.patternLine("SCS")
			.patternLine("GBG")
			.patternLine("SCS")
			.addCriterion("has_bitumen", hasItem(IPContent.Items.bitumen))
			.addCriterion("has_slag", hasItem(IEItems.Ingredients.slag))
			.build(this.out, rl("asphalt"));
		
		FluidAwareShapedRecipeBuilder.builder(IPContent.Blocks.asphalt, 12)
			.key('C', IPContent.Items.bitumen)
			.key('S', IEItems.Ingredients.slag)
			.key('G', Tags.Items.GRAVEL)
			.key('B', new IngredientFluidStack(FluidTags.WATER, FluidAttributes.BUCKET_VOLUME))
			.patternLine("SCS")
			.patternLine("GBG")
			.patternLine("SCS")
			.addCriterion("has_bitumen", hasItem(IPContent.Items.bitumen))
			.addCriterion("has_slag", hasItem(IEItems.Ingredients.slag))
			.build(this.out, rl("asphalt"));
		

		
		ShapedRecipeBuilder.shapedRecipe(IPContent.Blocks.gas_generator)
			.key('P', IETags.getTagsFor(EnumMetals.IRON).plate)
			.key('G', IEBlocks.MetalDecoration.generator)
			.key('C', IEBlocks.MetalDevices.capacitorLV)
			.patternLine("PPP")
			.patternLine("PGC")
			.patternLine("PPP")
			.addCriterion("has_iron_plate", hasItem(IETags.getTagsFor(EnumMetals.IRON).plate))
			.addCriterion("has_"+toPath(IEBlocks.MetalDevices.capacitorLV), hasItem(IEBlocks.MetalDevices.capacitorLV))
			.addCriterion("has_"+toPath(IEBlocks.MetalDecoration.generator), hasItem(IEBlocks.MetalDecoration.generator))
			.build(this.out, rl("gas_generator"));
		

		ShapedRecipeBuilder.shapedRecipe(IPContent.Blocks.flarestack)
			.key('I', IETags.getTagsFor(EnumMetals.IRON).plate)
			.key('C', IEItems.Ingredients.componentSteel)
			.key('P', IEBlocks.MetalDevices.fluidPipe)
			.key('A', IEBlocks.MetalDevices.fluidPlacer)
			.key('F', Items.FLINT_AND_STEEL)
			.patternLine("IFI")
			.patternLine("CAC")
			.patternLine("IPI")
			.addCriterion("has_bitumen", hasItem(IPContent.Items.bitumen))
			.addCriterion("has_"+toPath(IEBlocks.MetalDevices.fluidPipe), hasItem(IEBlocks.MetalDevices.fluidPipe))
			.build(this.out, rl("flarestack"));
	}
	
	private void itemRecipes(){
		ShapedRecipeBuilder.shapedRecipe(IPContent.Items.oil_can)
			.key('R', Tags.Items.DYES_RED)
			.key('P', IETags.getTagsFor(EnumMetals.IRON).plate)
			.key('B', Items.BUCKET)
			.patternLine(" R ")
			.patternLine("PBP")
			.addCriterion("has_rose_red", hasItem(Items.RED_DYE))
			.addCriterion("has_iron_plate", hasItem(IETags.getTagsFor(EnumMetals.IRON).plate))
			.build(out);
		

	}
	
	private ResourceLocation rl(String str){
		if(PATH_COUNT.containsKey(str)){
			int count = PATH_COUNT.get(str) + 1;
			PATH_COUNT.put(str, count);
			return new ResourceLocation(ImmersiveNuclear.MODID, str + count);
		}
		PATH_COUNT.put(str, 1);
		return new ResourceLocation(ImmersiveNuclear.MODID, str);
	}
	
	private String toPath(IItemProvider src){
		return src.asItem().getRegistryName().getPath();
	}
	
}
