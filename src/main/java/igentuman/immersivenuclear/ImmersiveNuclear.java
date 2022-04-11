package igentuman.immersivenuclear;

import igentuman.immersivenuclear.api.crafting.pumpjack.PumpjackHandler;
import igentuman.immersivenuclear.client.ClientProxy;
import igentuman.immersivenuclear.common.cfg.IPClientConfig;
import igentuman.immersivenuclear.common.cfg.IPCommonConfig;
import igentuman.immersivenuclear.common.cfg.IPServerConfig;
import igentuman.immersivenuclear.common.util.commands.ReservoirCommand;
import igentuman.immersivenuclear.common.util.loot.IPLootFunctions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import igentuman.immersivenuclear.common.CommonEventHandler;
import igentuman.immersivenuclear.common.CommonProxy;
import igentuman.immersivenuclear.common.IPContent;
import igentuman.immersivenuclear.common.IPSaveData;
import igentuman.immersivenuclear.common.IPTileTypes;
import igentuman.immersivenuclear.common.crafting.RecipeReloadListener;
import igentuman.immersivenuclear.common.crafting.Serializers;
import igentuman.immersivenuclear.common.network.IPPacketHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ImmersiveNuclear.MODID)
public class ImmersiveNuclear {
	public static final String MODID = "immersivenuclear";
	
	public static final Logger log = LogManager.getLogger(MODID);
	
	public static final ItemGroup creativeTab = new ItemGroup(MODID){
		@Override
		public ItemStack createIcon(){
			return new ItemStack(IPContent.Fluids.crudeOil.getFilledBucket());
		}
	};
	
	public static CommonProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
	
	public static ImmersiveNuclear INSTANCE;
	
	public ImmersiveNuclear(){
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, IPServerConfig.ALL);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, IPClientConfig.ALL);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, IPCommonConfig.ALL);
		
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
		
		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
		MinecraftForge.EVENT_BUS.addListener(this::serverAboutToStart);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarted);
		MinecraftForge.EVENT_BUS.addListener(this::registerCommand);
		MinecraftForge.EVENT_BUS.addListener(this::addReloadListeners);
		
		Serializers.RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
		
		IPContent.populate();
		IPLootFunctions.modConstruction();
		
		IPTileTypes.REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
		
		proxy.registerContainersAndScreens();
	}
	
	public void setup(FMLCommonSetupEvent event){
		proxy.setup();
		
		// ---------------------------------------------------------------------------------------------------------------------------------------------
		
		proxy.preInit();
		
		IPContent.preInit();
		IPPacketHandler.preInit();
		
		proxy.preInitEnd();
		
		// ---------------------------------------------------------------------------------------------------------------------------------------------
		
		IPContent.init();
		
		MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
		
		proxy.init();
		
		// ---------------------------------------------------------------------------------------------------------------------------------------------
		
		proxy.postInit();
		
		PumpjackHandler.recalculateChances();
	}
	
	public void loadComplete(FMLLoadCompleteEvent event){
		proxy.completed();
	}
	
	public void serverAboutToStart(FMLServerAboutToStartEvent event){
		proxy.serverAboutToStart();
	}
	
	public void serverStarting(FMLServerStartingEvent event){
		proxy.serverStarting();
	}
	
	public void registerCommand(RegisterCommandsEvent event){
		LiteralArgumentBuilder<CommandSource> ip = Commands.literal("ip");
		
		ip.then(ReservoirCommand.create());
		
		event.getDispatcher().register(ip);
	}
	
	public void addReloadListeners(AddReloadListenerEvent event){
		event.addListener(new RecipeReloadListener(event.getDataPackRegistries()));
	}
	
	public void serverStarted(FMLServerStartedEvent event){
		proxy.serverStarted();
		
		ServerWorld world = event.getServer().getWorld(World.OVERWORLD);
		if(!world.isRemote){
			IPSaveData worldData = world.getSavedData().getOrCreate(IPSaveData::new, IPSaveData.dataName);
			IPSaveData.setInstance(worldData);
		}
		
		PumpjackHandler.recalculateChances();
	}
}
