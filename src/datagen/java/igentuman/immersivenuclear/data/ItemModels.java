/*
 * BluSunrize
 * Copyright (c) 2019
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.data;

import blusunrize.immersiveengineering.mixin.accessors.ItemModelGeneratorsAccess;
import blusunrize.immersiveengineering.mixin.accessors.TrimModelDataAccess;
import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.api.INEnumMetals;
import igentuman.immersivenuclear.api.wires.INWireType;
import igentuman.immersivenuclear.common.register.INBlocks.Metals;
import igentuman.immersivenuclear.common.register.INBlocks.*;
import igentuman.immersivenuclear.common.register.INFluids;
import igentuman.immersivenuclear.common.register.INItems;
import igentuman.immersivenuclear.common.register.INItems.*;
import igentuman.immersivenuclear.data.blockstates.MultiblockStates;
import igentuman.immersivenuclear.data.models.*;
import com.google.common.base.Preconditions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.minecraftforge.client.model.generators.loaders.ObjModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;

import static igentuman.immersivenuclear.ImmersiveNuclear.rl;
import static net.minecraft.client.renderer.RenderType.translucent;

public class ItemModels extends TRSRItemModelProvider
{
	private final MultiblockStates blockStates;

	public ItemModels(PackOutput output, ExistingFileHelper existingFileHelper, MultiblockStates blockStates)
	{
		super(output, existingFileHelper);
		this.blockStates = blockStates;
	}

	private ResourceLocation forgeLoc(String s)
	{
		return new ResourceLocation("forge", s);
	}

	@Override
	protected void registerModels()
	{
		for(INEnumMetals m : INEnumMetals.values())
			createMetalModels(m);
		createItemModels();
		createMetalModels();
		createStoneModels();
		createConnectorModels();
	}

	private void createMetalModels()
	{



	}


	private void createItemModels()
	{
		addItemModels("metal_", INItems.Metals.INGOTS.values().stream().filter(i -> ImmersiveNuclear.MODID.equals(i.getId().getNamespace())).toArray(ItemLike[]::new));
		addItemModels("metal_", INItems.Metals.NUGGETS.values().stream().filter(i -> ImmersiveNuclear.MODID.equals(i.getId().getNamespace())).toArray(ItemLike[]::new));
		addItemModels("metal_", INItems.Metals.RAW_ORES.values().stream().filter(i -> ImmersiveNuclear.MODID.equals(i.getId().getNamespace())).toArray(ItemLike[]::new));
		addItemModels("metal_", INItems.Metals.DUSTS.values().toArray(new ItemLike[0]));
		addItemModels("metal_", INItems.Metals.PLATES.values().toArray(new ItemLike[0]));
/*
		addItemModels("", INItems.Misc.WIRE_COILS.values().toArray(new ItemLike[0]));
		addItemModels("", INItems.Misc.GRAPHITE_ELECTRODE);
		addItemModels("", INItems.Misc.TOOL_UPGRADES.values().toArray(new ItemLike[0]));
		addItemModels("", Molds.MOLD_PLATE, Molds.MOLD_GEAR, Molds.MOLD_ROD, Molds.MOLD_BULLET_CASING, Molds.MOLD_WIRE, Molds.MOLD_PACKING_4, Molds.MOLD_PACKING_9, Molds.MOLD_UNPACKING);
		addItemModels("bullet_", Ingredients.EMPTY_CASING, Ingredients.EMPTY_SHELL);

		addItemModels("", INItems.Misc.FARADAY_SUIT.values());
//		addItemModels("", IEItems.Tools.STEEL_ARMOR.values());
		for(Entry<Type, ItemRegObject<SteelArmorItem>> armorPiece : INItems.Tools.STEEL_ARMOR.entrySet())
			addTrimmedArmorModel(armorPiece.getValue().get());

		addItemModel("blueprint", INItems.Misc.BLUEPRINT);

		addItemModels("", INItems.Misc.LOGIC_CIRCUIT_BOARD);
		addItemModels("", INItems.Misc.FERTILIZER);
		addItemModel("banner_pattern", INBannerPatterns.WOLF.item());
		addItemModels("", INItems.Misc.ICON_BIRTHDAY, INItems.Misc.ICON_LUCKY,
				INItems.Misc.ICON_DRILLBREAK, INItems.Misc.ICON_RAVENHOLM, INItems.Misc.ICON_FRIED, INItems.Misc.ICON_BTTF);

		withExistingParent(name(SpawnEggs.EGG_FUSILIER), new ResourceLocation("minecraft:item/template_spawn_egg"));
		withExistingParent(name(SpawnEggs.EGG_COMMANDO), new ResourceLocation("minecraft:item/template_spawn_egg"));
		withExistingParent(name(SpawnEggs.EGG_BULWARK), new ResourceLocation("minecraft:item/template_spawn_egg"));

		obj(Tools.VOLTMETER, rl("item/voltmeter.obj"))
				.transforms(rl("item/voltmeter"));
		obj(Tools.TOOLBOX, rl("item/toolbox.obj"))
				.transforms(rl("item/toolbox"));*/

		INFluids.ALL_ENTRIES.forEach(this::createBucket);


	}

	private void createBucket(INFluids.FluidEntry entry)
	{
		withExistingParent(name(entry.getBucket()), forgeLoc("item/bucket"))
				.customLoader(DynamicFluidContainerModelBuilder::begin)
				.fluid(entry.getStill());
	}

	private void createStoneModels()
	{

	}

	private void createConnectorModels()
	{
		obj(Connectors.getEnergyConnector(INWireType.EV_CATEGORY, false), rl("block/connector/connector_ev.obj"))
				.transforms(rl("item/connector"));
		obj(Connectors.getEnergyConnector(INWireType.EV_CATEGORY, true), rl("block/connector/relay_ev.obj"))
				.transforms(rl("item/connector"));
		obj(Connectors.TRANSFORMER_EV, rl("block/connector/transformer_ev_left.obj"))
				.transforms(rl("item/transformer"));
	}

	private TRSRModelBuilder obj(ItemLike item, ResourceLocation model)
	{
		Preconditions.checkArgument(existingFileHelper.exists(model, PackType.CLIENT_RESOURCES, "", "models"));
		return getBuilder(item)
				.customLoader(ObjModelBuilder::begin)
				.flipV(true)
				.modelLocation(new ResourceLocation(model.getNamespace(), "models/"+model.getPath()))
				.end();
	}

	private IEOBJBuilder<TRSRModelBuilder> ieObjBuilder(ItemLike item, ResourceLocation model)
	{
		Preconditions.checkArgument(existingFileHelper.exists(model, PackType.CLIENT_RESOURCES, "", "models"));
		return getBuilder(item)
				.customLoader(IEOBJBuilder::begin)
				.modelLocation(new ResourceLocation(model.getNamespace(), "models/"+model.getPath()));
	}

	private TRSRModelBuilder getBuilder(ItemLike item)
	{
		return getBuilder(name(item));
	}

	private String name(ItemLike item)
	{
		return BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
	}

	@Nonnull
	@Override
	public String getName()
	{
		return "Item models";
	}

	private void createMetalModels(INEnumMetals metal)
	{
		String name = metal.tagName();
		if(metal.shouldAddOre())
		{
			cubeAll(name(Metals.ORES.get(metal)), rl("block/metal/ore_"+name));
			cubeAll(name(Metals.DEEPSLATE_ORES.get(metal)), rl("block/metal/deepslate_ore_"+name));
		}
		if(!metal.isVanillaMetal())
		{
			ResourceLocation defaultName = rl("block/metal/storage_"+name);
			cubeAll(name(Metals.STORAGE.get(metal)), defaultName);

		}
	}

	private void addItemModels(String texturePrefix, ItemLike... items)
	{
		addItemModels(texturePrefix, Arrays.asList(items));
	}

	private void addItemModels(String texturePrefix, ResourceLocation parent, ItemLike... items)
	{
		addItemModels(texturePrefix, parent, Arrays.asList(items));
	}

	private void addItemModels(String texturePrefix, Collection<? extends ItemLike> items)
	{
		addItemModels(texturePrefix, mcLoc("item/generated"), items);
	}

	private void addItemModels(String texturePrefix, ResourceLocation parent, Collection<? extends ItemLike> items)
	{
		for(ItemLike item : items)
			addItemModel(texturePrefix==null?null: (texturePrefix+BuiltInRegistries.ITEM.getKey(item.asItem()).getPath()), item, parent);
	}

	private void addItemModel(String texture, ItemLike item)
	{
		addItemModel(texture, item, mcLoc("item/generated"));
	}

	private void addItemModel(String texture, ItemLike item, ResourceLocation parent)
	{
		String path = name(item);
		String textureLoc = texture==null?path: ("item/"+texture);
		withExistingParent(path, parent)
				.texture("layer0", modLoc(textureLoc));
	}

	private void addLayeredItemModel(Item item, ResourceLocation... layers)
	{
		String path = name(item);
		TRSRModelBuilder modelBuilder = withExistingParent(path, mcLoc("item/generated"));
		int layerIdx = 0;
		for(ResourceLocation layer : layers)
			modelBuilder.texture("layer"+(layerIdx++), layer);
	}

	private void addTrimmedArmorModel(ArmorItem item)
	{
		String path = name(item);
		ResourceLocation baseTexture = modLoc("item/"+path);
		TRSRModelBuilder modelBuilder = withExistingParent(path, mcLoc("item/generated"))
				.texture("layer0", baseTexture);
		for(TrimModelDataAccess trim : ItemModelGeneratorsAccess.getGeneratedTrimModels())
		{
			String material = trim.getName();
			String name = path+"_"+material+"_trim";
			ResourceLocation trimTexture = mcLoc("trims/items/"+item.getType().getName()+"_trim_"+material);
			// hacky workaround to avoid complaints about missing textures
			existingFileHelper.trackGenerated(trimTexture, ModelProvider.TEXTURE);
			TRSRModelBuilder trimModel = this.withExistingParent(name, mcLoc("item/generated"))
					.texture("layer0", baseTexture)
					.texture("layer1", trimTexture);
			modelBuilder.override(trimModel, new ResourceLocation("trim_type"), trim.getItemModelIndex());
		}
	}
}
