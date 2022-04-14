package igentuman.immersivenuclear.common.crafting.serializers;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import igentuman.immersivenuclear.api.crafting.CoolingTowerRecipe;
import igentuman.immersivenuclear.api.crafting.builders.CoolingTowerRecipeBuilder;
import igentuman.immersivenuclear.common.IPContent;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fluids.FluidStack;

public class CoolingTowerRecipeSerializer extends IERecipeSerializer<CoolingTowerRecipe>{
	@Override
	public CoolingTowerRecipe readFromJson(ResourceLocation recipeId, JsonObject json){
		FluidTagInput input = FluidTagInput.deserialize(JSONUtils.getJsonObject(json, "input"));
		JsonArray fluidResults = JSONUtils.getJsonArray(json, "results");
		JsonArray itemResults = JSONUtils.getJsonArray(json, "byproducts");
		
		FluidStack[] fluidOutput = new FluidStack[fluidResults.size()];
		for(int i = 0;i < fluidOutput.length;i++)
			fluidOutput[i] = ApiUtils.jsonDeserializeFluidStack(fluidResults.get(i).getAsJsonObject());
		
		List<ItemStack> byproducts = new ArrayList<>(0);
		List<Double> chances = new ArrayList<>(0);
		for(int i = 0;i < itemResults.size();i++){
			Tuple<ItemStack, Double> chancedStack = CoolingTowerRecipeBuilder.deserializeItemStackWithChance(itemResults.get(i).getAsJsonObject());
			
			byproducts.add(chancedStack.getA());
			chances.add(chancedStack.getB());
		}
		
		if(byproducts.size() != chances.size()){
			int d = Math.abs(chances.size() - byproducts.size());
			throw new com.google.gson.JsonSyntaxException(d + " byproduct" + (d > 1 ? "s have" : " has") + " a missing value or too many.");
		}
		
		ItemStack[] array0 = byproducts.toArray(new ItemStack[0]);
		double[] array1 = new double[chances.size()];
		for(int i = 0;i < chances.size();i++)
			array1[i] = chances.get(i);
		
		int energy = JSONUtils.getInt(json, "energy");
		int time = JSONUtils.getInt(json, "time");
		
		return new CoolingTowerRecipe(recipeId, fluidOutput, input, energy, time, array1);
	}
	
	@Override
	public CoolingTowerRecipe read(ResourceLocation recipeId, PacketBuffer buffer){
		FluidStack[] fluidOutput = new FluidStack[buffer.readInt()];
		for(int i = 0;i < fluidOutput.length;i++)
			fluidOutput[i] = buffer.readFluidStack();
		
		ItemStack[] byproducts = new ItemStack[buffer.readInt()];
		for(int i = 0;i < byproducts.length;i++)
			byproducts[i] = buffer.readItemStack();
		
		double[] chances = new double[buffer.readInt()];
		for(int i = 0;i < chances.length;i++)
			chances[i] = buffer.readDouble();
		
		FluidTagInput input = FluidTagInput.read(buffer);
		int energy = buffer.readInt();
		int time = buffer.readInt();
		
		return new CoolingTowerRecipe(recipeId, fluidOutput, input, energy, time, chances);
	}
	
	@Override
	public void write(PacketBuffer buffer, CoolingTowerRecipe recipe){
		buffer.writeInt(recipe.getFluidOutputs().size());
		for(FluidStack stack:recipe.getFluidOutputs())
			buffer.writeFluidStack(stack);
		
		buffer.writeInt(recipe.getItemOutputs().size());
		for(ItemStack stack:recipe.getItemOutputs())
			buffer.writeItemStack(stack);
		
		buffer.writeInt(recipe.chances().length);
		for(double d:recipe.chances())
			buffer.writeDouble(d);
		
		recipe.getInputFluid().write(buffer);
		buffer.writeInt(recipe.getTotalProcessEnergy());
		buffer.writeInt(recipe.getTotalProcessTime());
	}
	
	@Override
	public ItemStack getIcon(){
		return new ItemStack(IPContent.Multiblock.coolingtower);
	}
}
