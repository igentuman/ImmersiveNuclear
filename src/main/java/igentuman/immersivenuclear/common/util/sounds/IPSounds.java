package igentuman.immersivenuclear.common.util.sounds;

import java.util.HashSet;
import java.util.Set;

import igentuman.immersivenuclear.ImmersiveNuclear;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = ImmersiveNuclear.MODID, bus = Bus.MOD)
public class IPSounds{
	static Set<SoundEvent> soundEvents = new HashSet<>();
	
	public final static SoundEvent COOLING_TOWER = register("cooling_tower");
	public final static SoundEvent TURBINE = register("turbine");
	public final static SoundEvent GEIGER_TICK = register("geiger_tick");
	public final static SoundEvent GEIGER_TICK_HIGH_RADIATION = register("geiger_high_radiation");

	static SoundEvent register(String name){
		ResourceLocation rl = new ResourceLocation(ImmersiveNuclear.MODID, name);
		SoundEvent event = new SoundEvent(rl);
		soundEvents.add(event.setRegistryName(rl));
		return event;
	}
	
	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event){
		ImmersiveNuclear.log.info("Loading sounds.");
		for(SoundEvent sound:soundEvents){
			event.getRegistry().register(sound);
		}
		soundEvents.clear();
	}
}
