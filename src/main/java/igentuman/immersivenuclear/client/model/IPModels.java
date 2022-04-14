package igentuman.immersivenuclear.client.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import igentuman.immersivenuclear.ImmersiveNuclear;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

/** A central place for all of ImmersiveNuclears Models that arent OBJ's */
@Mod.EventBusSubscriber(modid = ImmersiveNuclear.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public class IPModels{
	@SubscribeEvent
	public static void init(FMLConstructModEvent event){

	}
	
	private static final Map<String, IPModel> MODELS = new HashMap<>();
	
	/**
	 * @param id The String-ID of the Model.
	 */
	public static void add(String id, IPModel model){
		if(MODELS.containsKey(id)){
			ImmersiveNuclear.log.error("Duplicate ID, \"{}\" already used by {}. Skipping.", id, MODELS.get(id).getClass());
		}else{
			model.init();
			MODELS.put(id, model);
		}
	}
	
	/**
	 * @param id The String-ID of the Model.
	 * @return The Model assigned to <code>id</code> or <code>null</code>
	 */
	public static Supplier<IPModel> getSupplier(String id){
		return () -> MODELS.get(id);
	}
	
	/**
	 * @return An unmodifiable collection of all added Models
	 */
	public static Collection<IPModel> getModels(){
		return Collections.unmodifiableCollection(MODELS.values());
	}
}
