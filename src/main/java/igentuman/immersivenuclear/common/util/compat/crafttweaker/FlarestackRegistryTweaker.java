package igentuman.immersivenuclear.common.util.compat.crafttweaker;

import org.openzen.zencode.java.ZenCodeType.Method;
import org.openzen.zencode.java.ZenCodeType.Name;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.impl.tag.MCTag;

import igentuman.immersivenuclear.api.crafting.FlarestackHandler;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.ITag;

@ZenRegister
@Name("mods.immersivenuclear.Flarestack")
public class FlarestackRegistryTweaker{
	
	/**
	 * Adds a fluid tag to the Flarestacks "burnable fluids" list
	 * 
	 * @param tag The fluidtag to be added
	 * 
	 * @docParam tag <tag:fluids:minecraft:water>
	 */
	@SuppressWarnings("unchecked")
	@Method
	public static void register(MCTag<Fluid> tag){
		if(tag == null){
			CraftTweakerAPI.logError("§cFlarestackHandler: Expected fluidtag as input fluid!§r");
			return;
		}
		
		FlarestackHandler.register((ITag<Fluid>) tag.getInternal());
	}
}
