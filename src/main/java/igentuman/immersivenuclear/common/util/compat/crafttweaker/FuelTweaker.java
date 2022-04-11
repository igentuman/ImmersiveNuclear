package igentuman.immersivenuclear.common.util.compat.crafttweaker;

import org.openzen.zencode.java.ZenCodeType.Method;
import org.openzen.zencode.java.ZenCodeType.Name;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;

import igentuman.immersivenuclear.api.energy.FuelHandler;
import net.minecraftforge.fluids.FluidStack;

@ZenRegister
@Name("mods.immersivenuclear.FuelRegistry")
public class FuelTweaker{
	
	@Method
	public static void registerGeneratorFuel(IFluidStack fluid, int fluxPerTick){
		if(fluid == null){
			CraftTweakerAPI.logError("§cGeneratorFuel fluid can not be null!§r");
			return;
		}
		
		if(fluxPerTick < 1){
			CraftTweakerAPI.logError("§cGeneratorFuel fluxPerTick has to be at least 1!§r");
			return;
		}
		
		FluidStack fstack = fluid.getInternal();
		FuelHandler.registerPortableGeneratorFuel(fstack.getFluid(), fluxPerTick, fstack.getAmount());
	}
	
	@Method
	public static void registerMotorboatFuel(IFluidStack fluid){
		if(fluid == null){
			CraftTweakerAPI.logError("§cMotorboatFuel fluid can not be null!§r");
			return;
		}
		
		FluidStack fstack = fluid.getInternal();
		FuelHandler.registerMotorboatFuel(fstack.getFluid(), fstack.getAmount());
	}
}
