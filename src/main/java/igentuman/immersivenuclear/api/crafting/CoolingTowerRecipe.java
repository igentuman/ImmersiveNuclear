package igentuman.immersivenuclear.api.crafting;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.common.cfg.IPServerConfig;
import igentuman.immersivenuclear.common.crafting.Serializers;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class CoolingTowerRecipe extends IPMultiblockRecipe{
	public static final IRecipeType<CoolingTowerRecipe> TYPE = IRecipeType.register(ImmersiveNuclear.MODID + ":distillationtower");
	public static Map<ResourceLocation, CoolingTowerRecipe> recipes = new HashMap<>();
	
	/** May return null! */
	public static CoolingTowerRecipe findRecipe(FluidStack input){
		if(!recipes.isEmpty()){
			for(CoolingTowerRecipe r:recipes.values()){
				if(r.input != null && r.input.testIgnoringAmount(input)){
					return r;
				}
			}
		}
		return null;
	}
	
	public static CoolingTowerRecipe loadFromNBT(CompoundNBT nbt){
		FluidStack input = FluidStack.loadFluidStackFromNBT(nbt.getCompound("input"));
		return findRecipe(input);
	}
	
	protected final FluidTagInput input;
	protected final FluidStack[] fluidOutput;
	protected final double[] chances;
	
	public CoolingTowerRecipe(ResourceLocation id, FluidStack[] fluidOutput, FluidTagInput input, int energy, int time, double[] chances){
		super(ItemStack.EMPTY, TYPE, id);
		this.fluidOutput = fluidOutput;
		this.chances = chances;
		
		this.input = input;
		this.fluidInputList = Collections.singletonList(input);
		this.fluidOutputList = Arrays.asList(this.fluidOutput);

		timeAndEnergy(time, energy);
		modifyTimeAndEnergy(IPServerConfig.REFINING.distillationTower_timeModifier::get, IPServerConfig.REFINING.distillationTower_energyModifier::get);
	}
	
	@Override
	protected IERecipeSerializer<CoolingTowerRecipe> getIESerializer(){
		return Serializers.DISTILLATION_SERIALIZER.get();
	}
	
	@Override
	public int getMultipleProcessTicks(){
		return 0;
	}
	
	@Override
	public NonNullList<ItemStack> getActualItemOutputs(TileEntity tile){
		return null;
	}
	
	public FluidTagInput getInputFluid(){
		return this.input;
	}
	
	public double[] chances(){
		return this.chances;
	}
}
