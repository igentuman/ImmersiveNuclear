package igentuman.immersivenuclear.common;

import java.util.ArrayList;
import java.util.List;

import igentuman.immersivenuclear.common.blocks.IPBlockBase;
import igentuman.immersivenuclear.common.blocks.stone.AsphaltBlock;
import igentuman.immersivenuclear.common.util.IPEffects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler;
import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler.ChemthrowerEffect_Potion;
import blusunrize.immersiveengineering.common.util.IEPotions;
import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.api.IPTags;
import igentuman.immersivenuclear.api.crafting.FlarestackHandler;
import igentuman.immersivenuclear.api.crafting.LubricantHandler;
import igentuman.immersivenuclear.common.blocks.metal.BlockDummy;
import igentuman.immersivenuclear.common.blocks.metal.CoolingTowerBlock;
import igentuman.immersivenuclear.common.blocks.metal.GasGeneratorBlock;
import igentuman.immersivenuclear.common.blocks.metal.HydrotreaterBlock;
import igentuman.immersivenuclear.common.fluids.DieselFluid;
import igentuman.immersivenuclear.common.fluids.IPFluid;
import igentuman.immersivenuclear.common.fluids.NapalmFluid;
import igentuman.immersivenuclear.common.items.DebugItem;
import igentuman.immersivenuclear.common.items.IPItemBase;
import igentuman.immersivenuclear.common.items.OilCanItem;
import igentuman.immersivenuclear.common.multiblocks.DistillationTowerMultiblock;
import igentuman.immersivenuclear.common.multiblocks.HydroTreaterMultiblock;
import igentuman.immersivenuclear.common.particle.FlareFire;
import igentuman.immersivenuclear.common.particle.IPParticleTypes;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Effect;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = ImmersiveNuclear.MODID, bus = Bus.MOD)
public class IPContent{
	public static final Logger log = LogManager.getLogger(ImmersiveNuclear.MODID + "/Content");
	
	public static final List<Block> registeredIPBlocks = new ArrayList<>();
	public static final List<Item> registeredIPItems = new ArrayList<>();
	public static final List<Fluid> registeredIPFluids = new ArrayList<>();
	
	public static class Multiblock{
		public static Block distillationtower;
		public static Block pumpjack;
		public static Block cokerunit;
		public static Block hydrotreater;
	}
	
	public static class Fluids{
		public static IPFluid crudeOil;
		public static IPFluid diesel;
		public static IPFluid diesel_sulfur;
		public static IPFluid lubricant;
		public static IPFluid gasoline;
		public static IPFluid napalm;
	}
	
	public static class Blocks{
		public static IPBlockBase asphalt;
		public static IPBlockBase petcoke;
		
		public static IPBlockBase gas_generator;
		public static IPBlockBase auto_lubricator;
		public static IPBlockBase flarestack;
		
		public static BlockDummy dummyOilOre;
		public static BlockDummy dummyPipe;
		public static BlockDummy dummyConveyor;
	}
	
	public static class Items{
		public static IPItemBase bitumen;
		public static IPItemBase projector;
		public static IPItemBase speedboat;
		public static IPItemBase oil_can;
		public static IPItemBase petcoke;
		public static IPItemBase petcokedust;
	}
	
	public static DebugItem debugItem;
	
	/** block/item/fluid population */
	public static void populate(){
		IPContent.debugItem = new DebugItem();
		
		Fluids.diesel = new DieselFluid("diesel");
		Fluids.diesel_sulfur = new DieselFluid("diesel_sulfur");
		Fluids.lubricant = new IPFluid("lubricant", 925, 1000);
		Fluids.gasoline = new IPFluid("gasoline", 789, 1200);
		Fluids.napalm = new NapalmFluid();
		
		Blocks.dummyOilOre = new BlockDummy("dummy_oil_ore");
		Blocks.dummyPipe = new BlockDummy("dummy_pipe");
		Blocks.dummyConveyor = new BlockDummy("dummy_conveyor");
		
		Blocks.gas_generator = new GasGeneratorBlock();
		
		AsphaltBlock asphalt = new AsphaltBlock();
		Blocks.asphalt = asphalt;
		
		Multiblock.distillationtower = new CoolingTowerBlock();
		Multiblock.hydrotreater = new HydrotreaterBlock();
		
		Items.bitumen = new IPItemBase("bitumen");
		Items.oil_can = new OilCanItem("oil_can");
		Items.petcoke = new IPItemBase("petcoke"){
			@Override
			public int getBurnTime(ItemStack itemStack){
				return 3200;
			}
		};
		Items.petcokedust = new IPItemBase("petcoke_dust");
	}
	
	public static void preInit(){
	}
	
	public static void init(){
		//blockFluidCrudeOil.setPotionEffects(new PotionEffect(IEPotions.flammable, 100, 1));
		//blockFluidDiesel.setPotionEffects(new PotionEffect(IEPotions.flammable, 100, 1));
		//blockFluidLubricant.setPotionEffects(new PotionEffect(IEPotions.slippery, 100, 1));
		//blockFluidNapalm.setPotionEffects(new PotionEffect(IEPotions.flammable, 140, 2));
		
		ChemthrowerHandler.registerEffect(IPTags.Fluids.lubricant, new ChemthrowerEffect_Potion(null, 0, IEPotions.slippery, 60, 1));

		ChemthrowerHandler.registerFlammable(IPTags.Fluids.crudeOil);
		ChemthrowerHandler.registerEffect(IPTags.Fluids.crudeOil, new ChemthrowerEffect_Potion(null, 0, IEPotions.flammable, 60, 1));
		
		ChemthrowerHandler.registerFlammable(IPTags.Fluids.gasoline);
		ChemthrowerHandler.registerEffect(IPTags.Fluids.gasoline, new ChemthrowerEffect_Potion(null, 0, IEPotions.flammable, 60, 1));
		
		ChemthrowerHandler.registerFlammable(IPTags.Fluids.napalm);
		ChemthrowerHandler.registerEffect(IPTags.Fluids.napalm, new ChemthrowerEffect_Potion(null, 0, IEPotions.flammable, 60, 2));
		
		MultiblockHandler.registerMultiblock(DistillationTowerMultiblock.INSTANCE);
		MultiblockHandler.registerMultiblock(HydroTreaterMultiblock.INSTANCE);
		
		//ConfigUtils.addFuel(IPServerConfig.GENERATION.fuels.get());
		//ConfigUtils.addBoatFuel(IPServerConfig.MISCELLANEOUS.boat_fuels.get());
		
		LubricantHandler.register(IPTags.Fluids.lubricant, 3);
		LubricantHandler.register(IETags.fluidPlantoil, 12);
		
		FlarestackHandler.register(IPTags.Utility.burnableInFlarestack);

	}
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event){
		for(Block block:registeredIPBlocks){
			try{
				event.getRegistry().register(block);
			}catch(Throwable e){
				log.error("Failed to register a block. ({})", block);
				throw e;
			}
		}
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event){
		for(Item item:registeredIPItems){
			try{
				event.getRegistry().register(item);
			}catch(Throwable e){
				log.error("Failed to register an item. ({}, {})", item, item.getRegistryName());
				throw e;
			}
		}
	}
	
	@SubscribeEvent
	public static void registerFluids(RegistryEvent.Register<Fluid> event){
		for(Fluid fluid:registeredIPFluids){
			try{
				event.getRegistry().register(fluid);
			}catch(Throwable e){
				log.error("Failed to register a fluid. ({}, {})", fluid, fluid.getRegistryName());
				throw e;
			}
		}
	}
	
	@SubscribeEvent
	public static void registerEntityTypes(RegistryEvent.Register<EntityType<?>> event){
		try{
			//event.getRegistry().register(INuclearEntity.TYPE);
		}catch(Throwable e){
			log.error("Failed to register Speedboat Entity. {}", e.getMessage());
			throw e;
		}
	}
	
	@SubscribeEvent
	public static void registerEffects(RegistryEvent.Register<Effect> event){
		IPEffects.init();
	}
	
	@SubscribeEvent
	public static void registerParticles(RegistryEvent.Register<ParticleType<?>> event){
		event.getRegistry().register(IPParticleTypes.FLARE_FIRE);
	}
	
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerParticleFactories(ParticleFactoryRegisterEvent event){
		ParticleManager manager = Minecraft.getInstance().particles;
		
		manager.registerFactory(IPParticleTypes.FLARE_FIRE, FlareFire.Factory::new);
	}
}
