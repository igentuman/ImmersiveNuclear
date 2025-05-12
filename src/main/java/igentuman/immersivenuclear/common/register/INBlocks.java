/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.common.register;

import blusunrize.immersiveengineering.api.EnumMetals;
import blusunrize.immersiveengineering.api.tool.conveyor.IConveyorType;
import blusunrize.immersiveengineering.common.blocks.BlockItemIE;
import blusunrize.immersiveengineering.common.blocks.IEBaseBlock;
import blusunrize.immersiveengineering.common.blocks.IEEntityBlock;
import blusunrize.immersiveengineering.common.blocks.generic.ConnectorBlock;
import blusunrize.immersiveengineering.common.blocks.metal.BasicConnectorBlock;
import blusunrize.immersiveengineering.common.blocks.metal.BlockItemCapacitor;
import blusunrize.immersiveengineering.common.blocks.metal.CapacitorBlockEntity;
import blusunrize.immersiveengineering.common.blocks.metal.CapacitorCreativeBlockEntity;
import blusunrize.immersiveengineering.common.register.IEBlocks.BlockEntry;
import igentuman.immersivenuclear.api.Lib;
import igentuman.immersivenuclear.common.blocks.metal.*;
import igentuman.immersivenuclear.common.config.IEServerConfig;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static igentuman.immersivenuclear.api.wires.INWireType.EV_CATEGORY;

// TODO block items
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
		public static final Map<EnumMetals, BlockEntry<Block>> ORES = new EnumMap<>(EnumMetals.class);
		public static final Map<EnumMetals, BlockEntry<Block>> DEEPSLATE_ORES = new EnumMap<>(EnumMetals.class);
		public static final Map<EnumMetals, BlockEntry<Block>> RAW_ORES = new EnumMap<>(EnumMetals.class);
		public static final Map<EnumMetals, BlockEntry<Block>> STORAGE = new EnumMap<>(EnumMetals.class);

		private static void init()
		{

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

	/*	public static final BlockEntry<IEEntityBlock<CapacitorBlockEntity>> CAPACITOR_EV = new BlockEntry<>(
				"capacitor_ev", DEFAULT_METAL_PROPERTIES, p -> new IEEntityBlock<>(INBlockEntities.CAPACITOR_EV, p)
		);*/


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
 			/*if(entry==MetalDevices.CAPACITOR_EV)
				toItem = block -> new BlockItemCapacitor(block, IEServerConfig.MACHINES.evCapConfig);
			else*/
				toItem = BlockItemIE::new;

			Function<Block, BlockItemIE> finalToItem = toItem;
			INItems.REGISTER.register(entry.getId().getPath(), () -> finalToItem.apply(entry.get()));
		}
	}
}
