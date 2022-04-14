package igentuman.immersivenuclear.client;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import igentuman.immersivenuclear.client.gui.CoolingTowerScreen;
import igentuman.immersivenuclear.client.gui.HydrotreaterScreen;
import igentuman.immersivenuclear.client.render.MultiblockDistillationTowerRenderer;
import igentuman.immersivenuclear.client.render.debugging.DebugRenderHandler;
import igentuman.immersivenuclear.common.CommonProxy;
import igentuman.immersivenuclear.common.IPContent;
import igentuman.immersivenuclear.common.IPTileTypes;
import igentuman.immersivenuclear.common.cfg.IPServerConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import com.electronwill.nightconfig.core.Config;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.client.manual.ManualElementMultiblock;
import blusunrize.immersiveengineering.client.models.ModelCoresample;
import blusunrize.immersiveengineering.common.blocks.IEBlocks;
import blusunrize.immersiveengineering.common.blocks.metal.MetalScaffoldingType;
import blusunrize.immersiveengineering.common.gui.GuiHandler;
import blusunrize.lib.manual.ManualElementCrafting;
import blusunrize.lib.manual.ManualElementTable;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualEntry.EntryData;
import blusunrize.lib.manual.ManualInstance;
import blusunrize.lib.manual.TextSplitter;
import blusunrize.lib.manual.Tree.InnerNode;
import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.api.crafting.CoolingTowerRecipe;
import igentuman.immersivenuclear.api.crafting.FlarestackHandler;
import igentuman.immersivenuclear.api.energy.FuelHandler;
import igentuman.immersivenuclear.common.crafting.RecipeReloadListener;
import igentuman.immersivenuclear.common.multiblocks.DistillationTowerMultiblock;
import igentuman.immersivenuclear.common.multiblocks.HydroTreaterMultiblock;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.ScreenManager.IScreenFactory;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ImmersiveNuclear.MODID)
public class ClientProxy extends CommonProxy {
	@SuppressWarnings("unused")
	private static final Logger log = LogManager.getLogger(ImmersiveNuclear.MODID + "/ClientProxy");
	public static final String CAT_IP = "inuclear";
	
	public static final KeyBinding keybind_preview_flip = new KeyBinding("key.immersivenuclear.projector.flip", InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_M, "key.categories.immersivenuclear");
	
	@Override
	public void setup(){
		//RenderingRegistry.registerEntityRenderingHandler(INuclearEntity.TYPE, MotorboatRenderer::new);
	}
	
	@Override
	public void registerContainersAndScreens(){
		super.registerContainersAndScreens();
		
		registerScreen(new ResourceLocation(ImmersiveNuclear.MODID, "distillationtower"), CoolingTowerScreen::new);
		registerScreen(new ResourceLocation(ImmersiveNuclear.MODID, "hydrotreater"), HydrotreaterScreen::new);
	}
	
	@SuppressWarnings("unchecked")
	public <C extends Container, S extends Screen & IHasContainer<C>> void registerScreen(ResourceLocation name, IScreenFactory<C, S> factory){
		ContainerType<C> type = (ContainerType<C>) GuiHandler.getContainerType(name);
		ScreenManager.registerFactory(type, factory);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void completed(){
		DeferredWorkQueue.runLater(() -> ManualHelper.addConfigGetter(str -> {
			switch(str){
				case "distillationtower_operationcost":{
					return Integer.valueOf((int) (2048 * IPServerConfig.REFINING.distillationTower_energyModifier.get()));
				}
				case "hydrotreater_operationcost":{
					return Integer.valueOf((int) (512 * IPServerConfig.REFINING.hydrotreater_energyModifier.get()));
				}
				case "portablegenerator_flux":{
					return FuelHandler.getFluxGeneratedPerTick(IPContent.Fluids.gasoline.getFluid());
				}
				default:
					break;
			}
			
			// Last resort
			Config cfg = IPServerConfig.getRawConfig();
			if(cfg.contains(str)){
				return cfg.get(str);
			}
			return null;
		}));
		
		setupManualPages();
	}
	
	@Override
	public void preInit(){
	}
	
	@Override
	public void preInitEnd(){
	}
	
	@Override
	public void init(){
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
		MinecraftForge.EVENT_BUS.register(new RecipeReloadListener(null));
		
		MinecraftForge.EVENT_BUS.register(new DebugRenderHandler());
		
		keybind_preview_flip.setKeyConflictContext(KeyConflictContext.IN_GAME);
		ClientRegistry.registerKeyBinding(keybind_preview_flip);
		
		ClientRegistry.bindTileEntityRenderer(IPTileTypes.TOWER.get(), MultiblockDistillationTowerRenderer::new);
	}
	
	/** ImmersiveNuclear's Manual Category */
	private static InnerNode<ResourceLocation, ManualEntry> IP_CATEGORY;
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onModelBakeEvent(ModelBakeEvent event){
		ModelResourceLocation mLoc = new ModelResourceLocation(IEBlocks.StoneDecoration.coresample.getRegistryName(), "inventory");
		IBakedModel model = event.getModelRegistry().get(mLoc);
		if(model instanceof ModelCoresample){
			// It'll be a while until that is in working conditions again
			// event.getModelRegistry().put(mLoc, new ModelCoresampleExtended());
		}
	}
	
	@Override
	public void renderTile(TileEntity te, IVertexBuilder iVertexBuilder, MatrixStack transform, IRenderTypeBuffer buffer){
		TileEntityRenderer<TileEntity> tesr = TileEntityRendererDispatcher.instance.getRenderer((TileEntity) te);
		transform.push();
		transform.rotate(new Quaternion(0, -90, 0, true));
		transform.translate(0, 1, -4);
		tesr.render(te, 0, transform, buffer, 0xF000F0, 0);
		transform.pop();
	}
	
	@Override
	public void drawUpperHalfSlab(MatrixStack transform, ItemStack stack){
		
		// Render slabs on top half
		BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();
		BlockState state = IEBlocks.MetalDecoration.steelScaffolding.get(MetalScaffoldingType.STANDARD).getDefaultState();
		IBakedModel model = blockRenderer.getBlockModelShapes().getModel(state);
		
		IRenderTypeBuffer.Impl buffers = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
		
		transform.push();
		transform.translate(0.0F, 0.5F, 1.0F);
		blockRenderer.getBlockModelRenderer().renderModel(transform.getLast(), buffers.getBuffer(RenderType.getSolid()), state, model, 1.0F, 1.0F, 1.0F, -1, -1, EmptyModelData.INSTANCE);
		transform.pop();
	}
	
	@Override
	public World getClientWorld(){
		return Minecraft.getInstance().world;
	}
	
	@Override
	public PlayerEntity getClientPlayer(){
		return Minecraft.getInstance().player;
	}
	
	@Override
	public void handleEntitySound(SoundEvent soundEvent, Entity entity, boolean active, float volume, float pitch){
		// TODO Restore sound for the Motorboat
	}
	
	@Override
	public void handleTileSound(SoundEvent soundEvent, TileEntity te, boolean active, float volume, float pitch){
		// TODO
	}
	
	public void setupManualPages(){
		ManualInstance man = ManualHelper.getManual();
		
		IP_CATEGORY = man.getRoot().getOrCreateSubnode(modLoc("main"), 100);
		distillation(modLoc("distillationtower"), 1);
		hydrotreater(modLoc("hydrotreater"), 3);

		man.addEntry(IP_CATEGORY, modLoc("asphalt"), 5);

		man.addEntry(IP_CATEGORY, modLoc("napalm"), 7);
		generator(modLoc("portablegenerator"), 8);
		flarestack(modLoc("flarestack"), 10);
	}
	
	private static void flarestack(ResourceLocation location, int priority){
		ManualInstance man = ManualHelper.getManual();
		
		ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
		builder.addSpecialElement("flarestack0", 0, new ManualElementCrafting(man, new ItemStack(IPContent.Blocks.flarestack)));
		builder.addSpecialElement("flarestack1", 0, () -> {
			Set<ITag<Fluid>> fluids = FlarestackHandler.getSet();
			List<ITextComponent[]> list = new ArrayList<ITextComponent[]>();
			for(ITag<Fluid> tag:fluids){
				if(tag instanceof INamedTag){
					List<Fluid> fl = ((INamedTag<Fluid>) tag).getAllElements();
					for(Fluid f:fl){
						ITextComponent[] entry = new ITextComponent[]{
								StringTextComponent.EMPTY, new FluidStack(f, 1).getDisplayName()
						};
						
						list.add(entry);
					}
				}
			}
			
			return new ManualElementTable(man, list.toArray(new ITextComponent[0][]), false);
		});
		builder.readFromFile(location);
		man.addEntry(IP_CATEGORY, builder.create(), priority);
	}
	

	
	private static void generator(ResourceLocation location, int priority){
		ManualInstance man = ManualHelper.getManual();
		
		ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
		builder.addSpecialElement("portablegenerator0", 0, new ManualElementCrafting(man, new ItemStack(IPContent.Blocks.gas_generator)));
		builder.readFromFile(location);
		man.addEntry(IP_CATEGORY, builder.create(), priority);
	}
	

	
	private static void distillation(ResourceLocation location, int priority){
		ManualInstance man = ManualHelper.getManual();
		
		ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
		builder.addSpecialElement("distillationtower0", 0, () -> new ManualElementMultiblock(man, DistillationTowerMultiblock.INSTANCE));
		builder.addSpecialElement("distillationtower1", 0, () -> {
			Collection<CoolingTowerRecipe> recipeList = CoolingTowerRecipe.recipes.values();
			List<ITextComponent[]> list = new ArrayList<ITextComponent[]>();
			for(CoolingTowerRecipe recipe:recipeList){
				boolean first = true;
				for(FluidStack output:recipe.getFluidOutputs()){
					ITextComponent outputName = output.getDisplayName();
					
					ITextComponent[] entry = new ITextComponent[]{
							first ? new StringTextComponent(recipe.getInputFluid().getAmount() + "mB ").appendSibling(recipe.getInputFluid().getMatchingFluidStacks().get(0).getDisplayName()) : StringTextComponent.EMPTY,
									new StringTextComponent(output.getAmount() + "mB ").appendSibling(outputName)
					};
					
					list.add(entry);
					first = false;
				}
			}
			
			return new ManualElementTable(man, list.toArray(new ITextComponent[0][]), false);
		});
		builder.readFromFile(location);
		man.addEntry(IP_CATEGORY, builder.create(), priority);
	}

	protected static void hydrotreater(ResourceLocation location, int priority){
		ManualInstance man = ManualHelper.getManual();
		
		ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
		builder.addSpecialElement("hydrotreater0", 0, () -> new ManualElementMultiblock(man, HydroTreaterMultiblock.INSTANCE));
		builder.readFromFile(location);
		man.addEntry(IP_CATEGORY, builder.create(), priority);
	}
	
	protected static EntryData createContentTest(TextSplitter splitter){
		return new EntryData("title", "subtext", "content");
	}
	
	static final DecimalFormat FORMATTER = new DecimalFormat("#,###.##");

}
