package igentuman.immersivenuclear.common.util.compat.crafttweaker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import igentuman.immersivenuclear.ImmersiveNuclear;
import net.minecraft.util.ResourceLocation;

public class TweakerUtils{
	public static final Logger log = LogManager.getLogger(ImmersiveNuclear.MODID + "/CT-Compat");
	
	public static ResourceLocation ctLoc(String name){
		return new ResourceLocation("crafttweaker", name);
	}
	
	public static ResourceLocation ipLoc(String name){
		return new ResourceLocation(ImmersiveNuclear.MODID, name);
	}
	
	public static ResourceLocation mcLoc(String name){
		return new ResourceLocation("minecraft", name);
	}
}
