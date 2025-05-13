/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.common.register;

import blusunrize.immersiveengineering.common.blocks.BlockItemIE;
import blusunrize.immersiveengineering.common.blocks.IEBaseBlock;
import blusunrize.immersiveengineering.common.blocks.IEEntityBlock;
import blusunrize.immersiveengineering.common.blocks.generic.ConnectorBlock;
import blusunrize.immersiveengineering.common.blocks.generic.PostBlock;
import blusunrize.immersiveengineering.common.blocks.generic.ScaffoldingBlock;
import blusunrize.immersiveengineering.common.blocks.generic.WallmountBlock;
import blusunrize.immersiveengineering.common.blocks.wooden.BarrelBlock;
import com.mojang.datafixers.util.Pair;
import igentuman.immersivenuclear.api.INEnumMetals;
import igentuman.immersivenuclear.api.Lib;
import igentuman.immersivenuclear.common.blocks.metal.BasicConnectorBlock;
import igentuman.immersivenuclear.common.blocks.metal.BlockItemCapacitor;
import igentuman.immersivenuclear.common.blocks.metal.CapacitorBlockEntity;
import igentuman.immersivenuclear.common.blocks.metal.TransformerEVBlock;
import igentuman.immersivenuclear.common.config.IEServerConfig;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static igentuman.immersivenuclear.api.wires.INWireType.EV_CATEGORY;

@SuppressWarnings({"unchecked", "deprecation"})
public final class INBlocks
{
	public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, Lib.MODID);
	private static final Supplier<Properties> STONE_DECO_PROPS = () -> Block.Properties.of()
			.mapColor(MapColor.STONE)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.sound(SoundType.STONE)
			.requiresCorrectToolForDrops()
			.strength(2, 10);

	private static final Supplier<Properties> STONE_DECO_STONE_BRICK_PROPS = () -> Block.Properties.of()
			.sound(SoundType.STONE)
			.mapColor(MapColor.STONE)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresCorrectToolForDrops()
			.strength(1.75f, 10);
	private static final Supplier<Properties> STONE_DECO_LEADED_PROPS = () -> Block.Properties.of()
			.sound(SoundType.STONE)
			.mapColor(MapColor.STONE)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresCorrectToolForDrops()
			.strength(2, 180);
	private static final Supplier<Properties> STONE_DECO_PROPS_NOT_SOLID = () -> Block.Properties.of()
			.sound(SoundType.STONE)
			.mapColor(MapColor.STONE)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresCorrectToolForDrops()
			.strength(0.5f, 0.5f) //Glass & Tinted Glass are 0.3f,0.3f. These glasses are stronger, thus 0.5f,0.5f
			.noOcclusion();

	private static final Supplier<Properties> STONE_DECO_BRICK_PROPS = () -> Block.Properties.of()
			.sound(SoundType.NETHER_BRICKS)
			.mapColor(MapColor.COLOR_RED)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresCorrectToolForDrops()
			.strength(2f, 8);

	private static final Supplier<Properties> STONE_DECO_GBRICK_PROPS = () -> Block.Properties.of()
			.sound(SoundType.NETHER_BRICKS)
			.mapColor(MapColor.STONE)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresCorrectToolForDrops()
			.strength(2f, 8);
	private static final Supplier<Properties> SHEETMETAL_PROPERTIES = () -> Block.Properties.of()
			.mapColor(MapColor.METAL)
			.sound(SoundType.METAL)
			.strength(2, 2); //Cauldron props are 2,2 and sheetmetal is similar
	private static final Supplier<Properties> STANDARD_WOOD_PROPERTIES = () -> Block.Properties.of()
			.mapColor(MapColor.WOOD)
			.ignitedByLava()
			.instrument(NoteBlockInstrument.BASS)
			.sound(SoundType.WOOD)
			.strength(2, 5);
	private static final Supplier<Properties> STANDARD_WOOD_PROPERTIES_NO_OVERLAY =
			() -> Block.Properties.of()
					.mapColor(MapColor.WOOD)
					.ignitedByLava()
					.instrument(NoteBlockInstrument.BASS)
					.sound(SoundType.WOOD)
					.strength(2, 5)
					.isViewBlocking((state, blockReader, pos) -> false);
	private static final Supplier<Properties> STANDARD_WOOD_PROPERTIES_NO_OCCLUSION = () -> STANDARD_WOOD_PROPERTIES_NO_OVERLAY.get().noOcclusion();
	private static final Supplier<Properties> DEFAULT_METAL_PROPERTIES = () -> Block.Properties.of()
			.mapColor(MapColor.METAL)
			.sound(SoundType.METAL)
			.requiresCorrectToolForDrops()
			.strength(3, 15);
	private static final Supplier<Properties> METAL_PROPERTIES_NO_OVERLAY =
			() -> Block.Properties.of()
					.mapColor(MapColor.METAL)
					.sound(SoundType.METAL)
					.strength(3, 15)
					.requiresCorrectToolForDrops()
					.isViewBlocking((state, blockReader, pos) -> false);
	public static final Supplier<Properties> METAL_PROPERTIES_NO_OCCLUSION = () -> METAL_PROPERTIES_NO_OVERLAY.get().noOcclusion();
	private static final Supplier<Properties> METAL_PROPERTIES_DYNAMIC = () -> METAL_PROPERTIES_NO_OCCLUSION.get().dynamicShape();

	private INBlocks()
	{
	}

	public static final class Metals
	{
		public static final Map<INEnumMetals, BlockEntry<Block>> ORES = new EnumMap<>(INEnumMetals.class);
		public static final Map<INEnumMetals, BlockEntry<Block>> DEEPSLATE_ORES = new EnumMap<>(INEnumMetals.class);
		public static final Map<INEnumMetals, BlockEntry<Block>> RAW_ORES = new EnumMap<>(INEnumMetals.class);
		public static final Map<INEnumMetals, BlockEntry<Block>> STORAGE = new EnumMap<>(INEnumMetals.class);


		private static void init()
		{
			for(INEnumMetals m : INEnumMetals.values()) {
				String name = m.tagName();
				BlockEntry<Block> storage;
				BlockEntry<Block> ore = null;
				BlockEntry<Block> deepslateOre = null;
				BlockEntry<Block> rawOre = null;
				if(m.shouldAddOre())
				{
					ore = new BlockEntry<>(BlockEntry.simple("ore_"+name,
							() -> Block.Properties.of()
									.mapColor(MapColor.STONE)
									.instrument(NoteBlockInstrument.BASEDRUM)
									.strength(3, 3)
									.requiresCorrectToolForDrops()));
					deepslateOre = new BlockEntry<>(BlockEntry.simple("deepslate_ore_"+name,
							() -> Block.Properties.of()
									.mapColor(MapColor.STONE)
									.instrument(NoteBlockInstrument.BASEDRUM)
									.mapColor(MapColor.DEEPSLATE)
									.sound(SoundType.DEEPSLATE)
									.strength(4.5f, 3)
									.requiresCorrectToolForDrops()));
					rawOre = new BlockEntry<>(BlockEntry.simple("raw_block_"+name,
							() -> Block.Properties.of()
									.mapColor(MapColor.STONE)
									.instrument(NoteBlockInstrument.BASEDRUM)
									.strength(5, 6)
									.requiresCorrectToolForDrops()));
				}
				if(!m.isVanillaMetal())
				{
					BlockEntry<IEBaseBlock> storageIE = BlockEntry.simple(
							"storage_"+name, () -> Block.Properties.of()
									.mapColor(MapColor.METAL)
									.sound(m==INEnumMetals.STAINLESS_STEEL?SoundType.NETHERITE_BLOCK: SoundType.METAL)
									.strength(5, 10)
									.requiresCorrectToolForDrops());
					storage = new BlockEntry<>(storageIE);
				}
				else
					throw new RuntimeException("Unkown vanilla metal: "+m.name());
				STORAGE.put(m, storage);
				if(ore!=null)
					ORES.put(m, ore);
				if(deepslateOre!=null)
					DEEPSLATE_ORES.put(m, deepslateOre);
				if(deepslateOre!=null)
					RAW_ORES.put(m, rawOre);
			}
		}
	}

	public static final class MetalDecoration
	{
		public static final BlockEntry<IEBaseBlock> EV_COIL = BlockEntry.simple("coil_ev", DEFAULT_METAL_PROPERTIES);
		//public static final BlockEntry<IEBaseBlock> ENGINEERING_HEAVY = BlockEntry.simple("heavy_engineering", DEFAULT_METAL_PROPERTIES);

		private static void init()
		{

		}
	}

	public static final class MetalDevices
	{

		public static final BlockEntry<IEEntityBlock<CapacitorBlockEntity>> CAPACITOR_EV = new BlockEntry<>(
				"capacitor_ev", DEFAULT_METAL_PROPERTIES, p -> new IEEntityBlock<>(INBlockEntities.CAPACITOR_EV, p)
		);


		private static void init()
		{


		}
	}

	public static final class Connectors
	{
		public static final Map<Pair<String, Boolean>, BlockEntry<BasicConnectorBlock<?>>> ENERGY_CONNECTORS = new HashMap<>();

		public static final BlockEntry<TransformerEVBlock> TRANSFORMER_EV = new BlockEntry<>(
				"transformer_ev", ConnectorBlock.PROPERTIES, TransformerEVBlock::new
		);

		public static BlockEntry<BasicConnectorBlock<?>> getEnergyConnector(String cat, boolean relay)
		{
			return ENERGY_CONNECTORS.get(Pair.of(cat, relay));
		}

		private static void init()
		{
			for(String cat : new String[]{EV_CATEGORY})
			{
				ENERGY_CONNECTORS.put(Pair.of(cat, false), BasicConnectorBlock.forPower(cat, false));
				ENERGY_CONNECTORS.put(Pair.of(cat, true), BasicConnectorBlock.forPower(cat, true));
			}
		}
	}

	private static Supplier<BlockBehaviour.Properties> dynamicShape(Supplier<BlockBehaviour.Properties> baseProps)
	{
		return () -> baseProps.get().dynamicShape();
	}

	public static void init()
	{
		REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
		Metals.init();
		MetalDecoration.init();
		MetalDevices.init();
		Connectors.init();

		for(BlockEntry<?> entry : BlockEntry.ALL_ENTRIES)
		{
			Function<Block, BlockItemIE> toItem;
 			if(entry==MetalDevices.CAPACITOR_EV)
				toItem = block -> new BlockItemCapacitor(block, IEServerConfig.MACHINES.evCapConfig);
			else
				toItem = BlockItemIE::new;

			Function<Block, BlockItemIE> finalToItem = toItem;
			INItems.REGISTER.register(entry.getId().getPath(), () -> finalToItem.apply(entry.get()));
		}
	}


	public static final class BlockEntry<T extends Block> implements Supplier<T>, ItemLike {
		public static final Collection<INBlocks.BlockEntry<?>> ALL_ENTRIES = new ArrayList<>();
		private final RegistryObject<T> regObject;
		private final Supplier<BlockBehaviour.Properties> properties;

		public static INBlocks.BlockEntry<IEBaseBlock> simple(String name, Supplier<BlockBehaviour.Properties> properties, Consumer<IEBaseBlock> extra) {
			return new INBlocks.BlockEntry<IEBaseBlock>(name, properties, (p) -> (IEBaseBlock) Util.make(new IEBaseBlock(p), extra));
		}

		public static INBlocks.BlockEntry<IEBaseBlock> simple(String name, Supplier<BlockBehaviour.Properties> properties) {
			return simple(name, properties, ($) -> {
			});
		}

		public static INBlocks.BlockEntry<IEEntityBlock<?>> barrel(String name, boolean metal) {
			return new INBlocks.BlockEntry<IEEntityBlock<?>>(name, () -> BarrelBlock.getProperties(metal), (p) -> BarrelBlock.make(p, metal));
		}

		public static INBlocks.BlockEntry<ScaffoldingBlock> scaffolding(String name, Supplier<BlockBehaviour.Properties> props) {
			return new INBlocks.BlockEntry<ScaffoldingBlock>(name, props, ScaffoldingBlock::new);
		}

		public static INBlocks.BlockEntry<FenceBlock> fence(String name, Supplier<BlockBehaviour.Properties> props) {
			return new INBlocks.BlockEntry<FenceBlock>(name, props, FenceBlock::new);
		}

		public static INBlocks.BlockEntry<PostBlock> post(String name, Supplier<BlockBehaviour.Properties> props) {
			return new INBlocks.BlockEntry<PostBlock>(name, INBlocks.dynamicShape(props), PostBlock::new);
		}

		public static INBlocks.BlockEntry<WallmountBlock> wallmount(String name, Supplier<BlockBehaviour.Properties> props) {
			return new INBlocks.BlockEntry<WallmountBlock>(name, props, WallmountBlock::new);
		}

		public BlockEntry(String name, Supplier<BlockBehaviour.Properties> properties, Function<BlockBehaviour.Properties, T> make) {
			this.properties = properties;
			this.regObject = (RegistryObject<T>) INBlocks.REGISTER.register(name, () -> (Block)make.apply((Properties)properties.get()));
			ALL_ENTRIES.add(this);
		}

		public BlockEntry(T existing) {
			this.properties = () -> Properties.copy(existing);
			this.regObject = RegistryObject.create(BuiltInRegistries.BLOCK.getKey(existing), ForgeRegistries.BLOCKS);
		}

		public BlockEntry(INBlocks.BlockEntry<? extends T> toCopy) {
			this.properties = toCopy.properties;
			this.regObject = (RegistryObject<T>) toCopy.regObject;
		}

		public T get() {
			return (T)(this.regObject.get());
		}

		public BlockState defaultBlockState() {
			return this.get().defaultBlockState();
		}

		public ResourceLocation getId() {
			return this.regObject.getId();
		}

		public BlockBehaviour.Properties getProperties() {
			return (BlockBehaviour.Properties)this.properties.get();
		}

		@Nonnull
		public Item asItem() {
			return this.get().asItem();
		}

		public RegistryObject<? extends Block> getRegObject() {
			return this.regObject;
		}
	}
}
