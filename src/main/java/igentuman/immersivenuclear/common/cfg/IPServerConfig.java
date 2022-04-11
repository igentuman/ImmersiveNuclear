package igentuman.immersivenuclear.common.cfg;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import com.electronwill.nightconfig.core.Config;
import com.google.common.base.Preconditions;

import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.api.energy.FuelHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig.ModConfigEvent;

@EventBusSubscriber(modid = ImmersiveNuclear.MODID, bus = Bus.MOD)
public class IPServerConfig{
	public static final Extraction EXTRACTION;
	public static final Refining REFINING;
	public static final Generation GENERATION;
	public static final Miscellaneous MISCELLANEOUS;
	
	public static final ForgeConfigSpec ALL;
	
	static{
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		
		EXTRACTION = new Extraction(builder);
		REFINING = new Refining(builder);
		GENERATION = new Generation(builder);
		MISCELLANEOUS = new Miscellaneous(builder);
		
		ALL = builder.build();
	}
	
	private static Config rawConfig;
	public static Config getRawConfig(){
		if(rawConfig == null){
			try{
				Field childConfig = ForgeConfigSpec.class.getDeclaredField("childConfig");
				childConfig.setAccessible(true);
				rawConfig = (Config) childConfig.get(ALL);
				Preconditions.checkNotNull(rawConfig);
			}catch(Exception x){
				throw new RuntimeException(x);
			}
		}
		return rawConfig;
	}
	
	public static class Extraction{
		public final ConfigValue<Double> reservoir_chance;
		public final ConfigValue<Integer> pumpjack_consumption;
		public final ConfigValue<Integer> pumpjack_speed;
		public final ConfigValue<Integer> pipe_check_ticks;
		public final BooleanValue required_pipes;
		Extraction(ForgeConfigSpec.Builder builder){
			builder.push("Extraction");
			
			reservoir_chance = builder
					.comment("The chance that a chunk contains a fluid reservoir", "Default: 0.5")
					.define("reservoir_chance", Double.valueOf(0.5));
			
			pumpjack_consumption = builder
					.comment("The Flux the Pumpjack requires each tick to pump", "Default: 1024")
					.define("pumpjack_consumption", Integer.valueOf(1024));
			
			pumpjack_speed = builder
					.comment("The amount of mB of oil a Pumpjack extracts per tick", "Default: 15")
					.define("pumpjack_speed", Integer.valueOf(15));
			
			required_pipes = builder
					.comment("Require a pumpjack to have pipes built down to Bedrock", "Default: false")
					.define("req_pipes", false);
			
			pipe_check_ticks = builder
					.comment("Number of ticks between checking for pipes below pumpjack if required", "Default: 100")
					.define("pipe_check_ticks", Integer.valueOf(100));
			
			builder.pop();
		}
	}
	
	public static class Refining{
		public final ConfigValue<Double> distillationTower_energyModifier;
		public final ConfigValue<Double> distillationTower_timeModifier;
		public final ConfigValue<Double> cokerUnit_energyModifier;
		public final ConfigValue<Double> cokerUnit_timeModifier;
		public final ConfigValue<Double> hydrotreater_energyModifier;
		public final ConfigValue<Double> hydrotreater_timeModifier;
		Refining(ForgeConfigSpec.Builder builder){
			builder.push("Refining");
			
			distillationTower_energyModifier = builder
					.comment("A modifier to apply to the energy costs of every Distillation Tower recipe", "Default: 1.0")
					.define("distillationTower_energyModifier", Double.valueOf(1.0));
			
			distillationTower_timeModifier = builder
					.comment("A modifier to apply to the time of every Distillation recipe. Can't be lower than 1", "Default: 1.0")
					.define("distillationTower_timeModifier", Double.valueOf(1.0));
			
			cokerUnit_energyModifier = builder
					.comment("A modifier to apply to the energy costs of every Coker Tower recipe", "Default: 1.0")
					.define("cokerUnit_energyModifier", Double.valueOf(1.0));
			
			cokerUnit_timeModifier = builder
					.comment("A modifier to apply to the time of every Coker recipe. Can't be lower than 1", "Default: 1.0")
					.define("cokerUnit_timeModifier", Double.valueOf(1.0));
			
			hydrotreater_energyModifier = builder
					.comment("A modifier to apply to the energy costs of every Sulfur Recovery Unit recipe", "Default: 1.0")
					.define("hydrotreater_energyModifier", Double.valueOf(1.0));
			
			hydrotreater_timeModifier = builder
					.comment("A modifier to apply to the time of every Sulfur Recovery Unit recipe. Can't be lower than 1", "Default: 1.0")
					.define("hydrotreater_timeModifier", Double.valueOf(1.0));
			
			builder.pop();
		}
	}
	
	public static class Generation{
		public final ConfigValue<List<? extends String>> fuels;
		Generation(ForgeConfigSpec.Builder builder){
			builder.push("Generation");
			
			fuels = builder
					.comment("List of Portable Generator fuels. Format: fluid_name, mb_used_per_tick, flux_produced_per_tick")
					.defineList("fuels", Arrays.asList(new String[]{
							"immersivenuclear:gasoline, 5, 256"
					}), o -> true);
			
			builder.pop();
		}
	}
	
	public static class Miscellaneous{
		public final ConfigValue<List<? extends String>> boat_fuels;
		public final BooleanValue autounlock_recipes;
		public final BooleanValue asphalt_speed;
		Miscellaneous(ForgeConfigSpec.Builder builder){
			builder.push("Miscellaneous");
			
			boat_fuels = builder
					.comment("List of Motorboat fuels. Format: fluid_name, mb_used_per_tick")
					.defineList("boat_fuels", Arrays.asList(new String[]{
							"immersivenuclear:gasoline, 1"
					}), o -> true);
			
			autounlock_recipes = builder
					.comment("Automatically unlock IP recipes for new players", "Default: true")
					.define("autounlock_recipes", true);
			
			asphalt_speed = builder
					.comment("Set to false to disable the asphalt block boosting player speed", "Default: true")
					.define("asphalt_speed", true);
			
			builder.pop();
		}
	}
	
	@SubscribeEvent
	public static void onConfigReload(ModConfigEvent ev){
		FuelHandler.onConfigReload(ev);
	}
}
