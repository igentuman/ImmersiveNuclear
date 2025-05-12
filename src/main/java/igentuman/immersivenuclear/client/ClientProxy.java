/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.client;

import blusunrize.immersiveengineering.client.OptifineWarning;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.models.obj.IEOBJLoader;
import blusunrize.immersiveengineering.client.models.obj.callback.item.RevolverCallbacks;
import blusunrize.immersiveengineering.client.render.ConnectionRenderer;
import blusunrize.immersiveengineering.client.render.tile.DynamicModel;
import blusunrize.immersiveengineering.common.gui.IEBaseContainerOld;
import blusunrize.immersiveengineering.common.register.IEMenuTypes.ArgContainer;
import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.client.render.tile.ArcFurnaceRenderer;
import igentuman.immersivenuclear.common.CommonProxy;
import igentuman.immersivenuclear.common.config.IEClientConfig;
import igentuman.immersivenuclear.common.register.INBannerPatterns;
import igentuman.immersivenuclear.common.register.INMenuTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import static blusunrize.immersiveengineering.client.ClientUtils.mc;
import static igentuman.immersivenuclear.ImmersiveNuclear.MODID;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MODID, bus = Bus.MOD)
public class ClientProxy extends CommonProxy
{
	public static void modConstruction()
	{
//		IEOBJCallbacks.register(rl("workbench"), WorkbenchCallbacks.INSTANCE);


		// Apparently this runs in data generation runs... but registering model loaders causes NPEs there
		if(Minecraft.getInstance()!=null)
			initWithMC();
	}

	public static void initWithMC()
	{
		populateAPI();

		ClientEventHandler handler = new ClientEventHandler();
		MinecraftForge.EVENT_BUS.register(handler);
		ReloadableResourceManager reloadableManager = (ReloadableResourceManager)mc().getResourceManager();
		reloadableManager.registerReloadListener(handler);
		reloadableManager.registerReloadListener(new ConnectionRenderer());
	}

	@SubscribeEvent
	public static void registerTooltips(RegisterClientTooltipComponentFactoriesEvent ev)
	{
		//ev.register(RevolverServerTooltip.class, RevolverClientTooltip::new);
	}

	@SubscribeEvent
	public static void registerModelLoaders(ModelEvent.RegisterGeometryLoaders ev)
	{
		ev.register(IEOBJLoader.LOADER_NAME.getPath(), IEOBJLoader.instance);
		ArcFurnaceRenderer.ELECTRODES = new DynamicModel(ArcFurnaceRenderer.NAME);
//		WatermillRenderer.MODEL = new DynamicModel(WatermillRenderer.NAME);
	}

	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent ev)
	{
		if(IEClientConfig.stencilBufferEnabled.get())
			ev.enqueueWork(() -> Minecraft.getInstance().getMainRenderTarget().enableStencil());
		registerContainersAndScreens();

		IEManual.addIEManualEntries();
		INBannerPatterns.ALL_BANNERS.forEach(entry -> {
			ResourceKey<BannerPattern> pattern = Objects.requireNonNull(entry.pattern().getKey());
			Sheets.BANNER_MATERIALS.put(pattern, new Material(Sheets.BANNER_SHEET, BannerPattern.location(pattern, true)));
			Sheets.SHIELD_MATERIALS.put(pattern, new Material(Sheets.SHIELD_SHEET, BannerPattern.location(pattern, false)));
		});
		ev.enqueueWork(OptifineWarning::warnIfRequired);
	}

	private static <T extends Entity, T2 extends T> void registerEntityRenderingHandler(
			EntityRenderersEvent.RegisterRenderers ev, Supplier<EntityType<T2>> type, EntityRendererProvider<T> renderer
	)
	{
		ev.registerEntityRenderer(type.get(), renderer);
	}

	@SubscribeEvent
	public static void textureStichPost(TextureStitchEvent.Post event)
	{
		if(!event.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS))
			return;
		ImmersiveNuclear.proxy.clearRenderCaches();
		RevolverCallbacks.retrieveRevolverTextures(event.getAtlas());
	}

	/*private final Map<BlockPos, IEBlockEntitySound> tileSoundMap = new HashMap<>();*/

	@Override
	public void handleTileSound(
			Supplier<SoundEvent> soundEvent, BlockEntity tile, boolean tileActive, float volume, float pitch
	)
	{

	}


	@SubscribeEvent
	public static void registerLayers(EntityRenderersEvent.AddLayers ev)
	{

	}

	private static <T extends LivingEntity, M extends EntityModel<T>>
	void addIELayer(LivingEntityRenderer<T, M> render, EntityModelSet models)
	{
		//render.addLayer(new IEBipedLayerRenderer<>(render, models));
	}

	@Override
	public Level getClientWorld()
	{
		return mc().level;
	}

	@Override
	public Player getClientPlayer()
	{
		return mc().player;
	}

	@Override
	public void reInitGui()
	{
		Screen currentScreen = mc().screen;
		if(currentScreen instanceof IEContainerScreen)
			currentScreen.init(mc(), currentScreen.width, currentScreen.height);
	}



	@SubscribeEvent
	public static void registerRenders(EntityRenderersEvent.RegisterRenderers event)
	{
		registerBERenders(event);
		registerEntityRenders(event);
	}

	private static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event)
	{
		//registerEntityRenderingHandler(event, INEntityTypes.EXPLOSIVE, IEExplosiveRenderer::new);
	}

	private static void registerContainersAndScreens()
	{
		//MenuScreens.register(INMenuTypes.ALLOY_SMELTER.getType(), AlloySmelterScreen::new);
	}

	private static <T extends BlockEntity>
	void registerBERenderNoContext(
			RegisterRenderers event, Supplier<BlockEntityType<? extends T>> type, Supplier<BlockEntityRenderer<T>> render
	)
	{
		ClientProxy.registerBERenderNoContext(event, type.get(), render);
	}

	private static <T extends BlockEntity>
	void registerBERenderNoContext(
			RegisterRenderers event, BlockEntityType<? extends T> type, Supplier<BlockEntityRenderer<T>> render
	)
	{
		event.registerBlockEntityRenderer(type, $ -> render.get());
	}

	public static void registerBERenders(RegisterRenderers event)
	{
		// MULTIBLOCKS
		//registerBERenderNoContext(event, INMultiblockLogic.ARC_FURNACE.masterBE(), ArcFurnaceRenderer::new);
		//registerBERenderNoContext(event, INMultiblockLogic.MIXER.masterBE(), MixerRenderer::new);
	}

	public static <C extends AbstractContainerMenu, S extends Screen & MenuAccess<C>>
	void registerScreen(INMenuTypes.ItemContainerType<C> type, ScreenConstructor<C, S> factory)
	{
		MenuScreens.register(type.getType(), factory);
	}

	public static <C extends IEBaseContainerOld<?>, S extends Screen & MenuAccess<C>>
	void registerTileScreen(ArgContainer<?, C> type, ScreenConstructor<C, S> factory)
	{
		MenuScreens.register(type.getType(), factory);
	}

	public static void populateAPI()
	{

	}
}
