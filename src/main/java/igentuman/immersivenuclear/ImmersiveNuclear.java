/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear;

import blusunrize.immersiveengineering.api.IEApi;
import blusunrize.immersiveengineering.common.network.IMessage;
import igentuman.immersivenuclear.api.Lib;
import igentuman.immersivenuclear.client.ClientProxy;
import igentuman.immersivenuclear.common.CommonProxy;
import igentuman.immersivenuclear.common.EventHandler;
import igentuman.immersivenuclear.common.INContent;
import igentuman.immersivenuclear.common.config.IEClientConfig;
import igentuman.immersivenuclear.common.config.IECommonConfig;
import igentuman.immersivenuclear.common.config.IEServerConfig;
import igentuman.immersivenuclear.common.util.RecipeSerializers;
import igentuman.immersivenuclear.common.util.commands.CommandHandler;
import igentuman.immersivenuclear.common.util.compat.IECompatModules;
import igentuman.immersivenuclear.common.world.IEWorldGen;
import igentuman.immersivenuclear.common.world.Villages;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT;
import static net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER;

@Mod(ImmersiveNuclear.MODID)
public class ImmersiveNuclear
{
	public static final String MODID = Lib.MODID;
	public static final String MODNAME = "Immersive Engineering";
	public static final String VERSION = IEApi.getCurrentVersion();
	public static final CommonProxy proxy = DistExecutor.safeRunForDist(bootstrapErrorToXCPInDev(() -> ClientProxy::new), bootstrapErrorToXCPInDev(() -> CommonProxy::new));

	public static final SimpleChannel packetHandler = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(MODID, "main"))
			.networkProtocolVersion(() -> VERSION)
			.serverAcceptedVersions(VERSION::equals)
			.clientAcceptedVersions(VERSION::equals)
			.simpleChannel();

	// Complete hack: DistExecutor::safeRunForDist intentionally tries to access the "wrong" supplier in dev, which
	// throws an error (rather than an exception) on J16 due to trying to load a client-only class. So we need to
	// replace the error with an exception in dev.
	public static <T>
	Supplier<T> bootstrapErrorToXCPInDev(Supplier<T> in)
	{
		if(FMLLoader.isProduction())
			return in;
		return () -> {
			try
			{
				return in.get();
			} catch(BootstrapMethodError e)
			{
				throw new RuntimeException(e);
			}
		};
	}

	public ImmersiveNuclear()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMCs);
		MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
		RecipeSerializers.RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
		Villages.Registers.POINTS_OF_INTEREST.register(FMLJavaModLoadingContext.get().getModEventBus());
		Villages.Registers.PROFESSIONS.register(FMLJavaModLoadingContext.get().getModEventBus());
		ModLoadingContext.get().registerConfig(Type.COMMON, IECommonConfig.CONFIG_SPEC);
		ModLoadingContext.get().registerConfig(Type.CLIENT, IEClientConfig.CONFIG_SPEC);
		ModLoadingContext.get().registerConfig(Type.SERVER, IEServerConfig.CONFIG_SPEC);
		INContent.modConstruction();
		DistExecutor.safeRunWhenOn(Dist.CLIENT, bootstrapErrorToXCPInDev(() -> ClientProxy::modConstruction));
		//IngredientSerializers.init();

		IEWorldGen.init();
		IECompatModules.onModConstruction();
	}

	public void setup(FMLCommonSetupEvent event)
	{
		IEApi.prefixToIngotMap.put("ingots", new Integer[]{1, 1});
		IEApi.prefixToIngotMap.put("nuggets", new Integer[]{1, 9});
		IEApi.prefixToIngotMap.put("storage_blocks", new Integer[]{9, 1});
		IEApi.prefixToIngotMap.put("plates", new Integer[]{1, 1});
		IEApi.prefixToIngotMap.put("wires", new Integer[]{1, 2});
		IEApi.prefixToIngotMap.put("gears", new Integer[]{4, 1});
		IEApi.prefixToIngotMap.put("rods", new Integer[]{1, 2});
		IEApi.prefixToIngotMap.put("fences", new Integer[]{5, 3});
		IEApi.prefixToIngotMap.put("cut_blocks", new Integer[]{9, 4});
		IEApi.prefixToIngotMap.put("cut_stairs", new Integer[]{9, 4});
		IEApi.prefixToIngotMap.put("cut_slabs", new Integer[]{9, 8});


		new ThreadContributorSpecialsDownloader();

		INContent.commonSetup(event);

		MinecraftForge.EVENT_BUS.register(new EventHandler());

		IECompatModules.onCommonSetup();


	}

	private int messageId = 0;

	private <T extends IMessage> void registerMessage(Class<T> packetType, Function<FriendlyByteBuf, T> decoder)
	{
		registerMessage(packetType, decoder, Optional.empty());
	}

	private <T extends IMessage> void registerMessage(
			Class<T> packetType, Function<FriendlyByteBuf, T> decoder, NetworkDirection direction
	)
	{
		registerMessage(packetType, decoder, Optional.of(direction));
	}

	private final Set<Class<?>> knownPacketTypes = new HashSet<>();

	private <T extends IMessage> void registerMessage(
			Class<T> packetType, Function<FriendlyByteBuf, T> decoder, Optional<NetworkDirection> direction
	)
	{
		if(!knownPacketTypes.add(packetType))
			throw new IllegalStateException("Duplicate packet type: "+packetType.getName());
		packetHandler.registerMessage(messageId++, packetType, IMessage::toBytes, decoder, (t, ctx) -> {
			t.process(ctx);
			ctx.get().setPacketHandled(true);
		}, direction);
	}

	public void enqueueIMCs(InterModEnqueueEvent event)
	{
		IECompatModules.doModulesIMCs();
		INContent.clearLastFuture();
	}

	public void registerCommands(RegisterCommandsEvent event)
	{
		//TODO do client commands exist yet? I don't think so
		CommandHandler.registerServer(event.getDispatcher());
	}

	public static ResourceLocation rl(String path)
	{
		return new ResourceLocation(MODID, path);
	}

	public static class ThreadContributorSpecialsDownloader extends Thread
	{
		public static ThreadContributorSpecialsDownloader activeThread;

		public ThreadContributorSpecialsDownloader()
		{
			setName("Immersive Engineering Contributors Thread");
			setDaemon(true);
			start();
			activeThread = this;
		}

		@Override
		public void run()
		{

		}
	}
}
