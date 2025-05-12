/*
 * BluSunrize
 * Copyright (c) 2020
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 *
 */

package igentuman.immersivenuclear.common.config;

import blusunrize.immersiveengineering.api.EnumMetals;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.common.blocks.metal.CapacitorBlockEntity;
import com.electronwill.nightconfig.core.Config;
import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.common.register.INBlockEntities;
import igentuman.immersivenuclear.common.wires.INWireTypes.IEWireType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

@SuppressWarnings("WeakerAccess")
@EventBusSubscriber(modid = ImmersiveNuclear.MODID, bus = Bus.MOD)
public class IEServerConfig
{
	public static class Wires
	{
		Wires(ForgeConfigSpec.Builder builder)
		{
			builder.comment("Configuration related to Immersive Engineering wires").push("wires");
			// Server
			sanitizeConnections = builder
					.comment("Attempts to make the internal data structures used for wires consistent with the connectors in the world."+
									"Use with care and backups and only when suspecting corrupted data.",
							"This option will check and load all connection endpoints and may slow down the world loading process.")
					.define("sanitizeConnections", false);

			energyWireConfigs.put(
					IEWireType.STEEL,
					new EnergyWireConfig(builder, "ev", 48, 131072, 0.0125)
			);

			wireConfigs.putAll(energyWireConfigs);
			//Server
			enableWireDamage = builder.comment("If this is enabled, wires connected to power sources will cause damage to entities touching them",
					"This shouldn't cause significant lag but possibly will. If it does, please report it at https://github.com/BluSunrize/ImmersiveEngineering/issues unless there is a report of it already.")
					.define("enableWireDamage", true);
			blocksBreakWires = builder.comment("If this is enabled, placing a block in a wire will break it (drop the wire coil)")
					.define("blocksBreakWires", true);
			builder.pop();
		}

		public final BooleanValue sanitizeConnections;
		public final BooleanValue enableWireDamage;
		public final BooleanValue blocksBreakWires;
		public final Map<IEWireType, WireConfig> wireConfigs = new EnumMap<>(IEWireType.class);
		public final Map<IEWireType, EnergyWireConfig> energyWireConfigs = new EnumMap<>(IEWireType.class);

		public static class WireConfig
		{
			public final IntValue maxLength;

			protected WireConfig(ForgeConfigSpec.Builder builder, String name, int defLength, boolean doPop)
			{
				builder.push(name);
				maxLength = builder.comment("The maximum length of "+name+" wires")
						.defineInRange("maxLength", defLength, 0, Integer.MAX_VALUE);
				if(doPop)
					builder.pop();
			}

			public WireConfig(ForgeConfigSpec.Builder builder, String name, int defLength)
			{
				this(builder, name, defLength, true);
			}
		}

		public static class EnergyWireConfig extends WireConfig
		{
			public final IntValue transferRate;
			public final IntValue connectorRate;
			public final DoubleValue lossRatio;

			public EnergyWireConfig(Builder builder, String name, int defLength, int defRate, double defLoss)
			{
				super(builder, name, defLength, false);
				this.transferRate = builder.comment("The transfer rate of "+name+" wire in IF/t")
						.defineInRange("transferRate", defRate, 0, Integer.MAX_VALUE);
				this.lossRatio = builder.comment("The percentage of power lost every 16 blocks of distance in "+name+" wire")
						.defineInRange("loss", defLoss, 0, 1);
				this.connectorRate = builder
						.comment("In- and output rates of "+name+" wire connectors. This is independant of the transferrate of the wires.")
						.defineInRange("wireConnectorInput", defRate/8, 0, Integer.MAX_VALUE);
				builder.pop();
			}
		}
	}

	public static class Machines
	{
		public CapacitorConfig evCapConfig;

		Machines(ForgeConfigSpec.Builder builder)
		{
			builder.push("machines");
			{
				builder.push("capacitors");
				//evCapConfig = new CapacitorConfig(builder, () -> INBlockEntities.CAPACITOR_EV.get(), "extreme", 16000000, 16384, 16384);
				builder.pop();
			}

			builder.pop();
		}

		private <T extends MultiblockRecipe> MachineRecipeConfig<T> addMachineEnergyTimeModifiers(Builder builder, String machine)
		{
			return addMachineEnergyTimeModifiers(builder, machine, true);
		}

		private <T extends MultiblockRecipe> MachineRecipeConfig<T> addMachineEnergyTimeModifiers(Builder builder, String machine, boolean popCategory)
		{
			builder.push(machine.replace(' ', '_'));
			DoubleValue energy = builder
					.comment("A modifier to apply to the energy costs of every "+machine+" recipe")
					.defineInRange("energyModifier", 1, 1e-3, 1e3);
			DoubleValue time = builder
					.comment("A modifier to apply to the time of every "+machine+" recipe")
					.defineInRange("timeModifier", 1, 1e-3, 1e3);
			if(popCategory)
				builder.pop();
			return new MachineRecipeConfig<>(energy, time);
		}

		public static class CapacitorConfig
		{

			public final IntSupplier storage;
			public final IntSupplier input;
			public final IntSupplier output;
			public final Supplier<BlockEntityType<? extends CapacitorBlockEntity>> tileType;

			private CapacitorConfig(Builder builder, Supplier<BlockEntityType<? extends CapacitorBlockEntity>> tileType, String voltage, int defaultStorage, int defaultInput, int defaultOutput)
			{
				this.tileType = tileType;
				builder
						.comment("Configuration for the "+voltage+" voltage capacitor")
						.push(voltage.charAt(0)+"v");
				storage = builder
						.comment("Maximum energy stored (Flux)")
						.defineInRange("storage", defaultStorage, 1, Integer.MAX_VALUE)::get;
				input = builder
						.comment("Maximum energy input (Flux/tick)")
						.defineInRange("input", defaultInput, 1, Integer.MAX_VALUE)::get;
				output = builder
						.comment("Maximum energy output (Flux/tick)")
						.defineInRange("output", defaultOutput, 1, Integer.MAX_VALUE)::get;
				builder.pop();
			}

			private CapacitorConfig(int storage, int input, int output, Supplier<BlockEntityType<? extends CapacitorBlockEntity>> type)
			{
				this.storage = () -> storage;
				this.input = () -> input;
				this.output = () -> output;
				this.tileType = type;
			}
		}

		//Multiblock Recipes
//		public final MachineRecipeConfig<BottlingMachineRecipe> bottlingMachineConfig;

		public record MachineRecipeConfig<T extends MultiblockRecipe>(
				DoubleValue energyModifier, DoubleValue timeModifier
		)
		{
			public T apply(T in)
			{
				in.modifyTimeAndEnergy(timeModifier::get, energyModifier::get);
				return in;
			}
		}
	}

	public static class Ores
	{
		Ores(Builder builder)
		{
			builder.push("ores");
			//Server
			for(VeinType type : VeinType.values())
				ores.put(type, new OreConfig(builder, type));
			retrogen_key = builder
					.comment("The retrogeneration key. Basically IE checks if this key is saved in the chunks data. If it isn't, it will perform retrogen on all ores marked for retrogen.", "Change this in combination with the retrogen booleans to regen only some of the ores.")
					.define("retrogen_key", "DEFAULT");
			retrogen_log_flagChunk = builder
					.comment("Set this to false to disable the logging of the chunks that were flagged for retrogen.")
					.define("retrogen_log_flagChunk", true);
			retrogen_log_remaining = builder
					.comment("Set this to false to disable the logging of the chunks that are still left to retrogen.")
					.define("retrogen_log_remaining", true);
			builder.pop();
		}


		public final Map<VeinType, OreConfig> ores = new EnumMap<>(VeinType.class);
		public final BooleanValue retrogen_log_flagChunk;
		public final BooleanValue retrogen_log_remaining;
		public final ConfigValue<String> retrogen_key;

		public static class OreConfig
		{
			public final EnumValue<OreDistribution> distribution;
			public final DoubleValue airExposure;
			public final IntValue veinSize;
			public final IntValue minY;
			public final IntValue maxY;
			public final IntValue veinsPerChunk;
			public final BooleanValue retrogenEnabled;

			private OreConfig(Builder builder, VeinType type)
			{
				String name = type.getVeinName();
				builder
						.comment("Ore generation config - "+name)
						.push(name);
				distribution = builder
						.comment("The distribution shape. UNIFORM is evenly distributed across the height range, TRAPEZOID favors the middle of the range.")
						.defineEnum("distribution", type.defaultDistribution);
				airExposure = builder
						.comment("Chance for ores to not generate, if they are exposed to air. 0 means ignoring air exposure, 1 requires being burried.")
						.defineInRange("air_exposure", type.defaultAirExposure, 0, 1);
				veinSize = builder
						.comment("The maximum size of a vein. Set to 0 to disable generation")
						.defineInRange("vein_size", type.defaultVeinSize, 0, Integer.MAX_VALUE);
				minY = builder
						.comment("The minimum Y coordinate this ore can spawn at")
						.defineInRange("min_y", type.defaultMinY, Integer.MIN_VALUE, Integer.MAX_VALUE);
				maxY = builder
						.comment("The maximum Y coordinate this ore can spawn at")
						.defineInRange("max_y", type.defaultMaxY, Integer.MIN_VALUE, Integer.MAX_VALUE);
				veinsPerChunk = builder
						.comment("The number of veins attempted to be generated per chunk")
						.defineInRange("attempts_per_chunk", type.defaultVeinsPerChunk, 0, Integer.MAX_VALUE);
				retrogenEnabled = builder
						.comment("Set this to true to allow retro-generation of "+name+" Ore.")
						.define("retrogen_enable", false);
				builder.pop();
			}
		}

		public enum OreDistribution
		{
			UNIFORM,
			TRAPEZOID;
		}

		public enum VeinType
		{

			BAUXITE(EnumMetals.ALUMINUM, OreDistribution.TRAPEZOID, 0, 6, 32, 112, 16),
			LEAD(EnumMetals.LEAD, OreDistribution.TRAPEZOID, 0, 8, -32, 80, 12),
			SILVER(EnumMetals.SILVER, OreDistribution.TRAPEZOID, 0.25, 9, -48, 32, 10),
			NICKEL(EnumMetals.NICKEL, OreDistribution.UNIFORM, 0, 5, -64, 24, 7),
			DEEP_NICKEL(EnumMetals.NICKEL, OreDistribution.TRAPEZOID, 0, 6, -120, -8, 11),
			URANIUM(EnumMetals.URANIUM, OreDistribution.TRAPEZOID, 0.5, 4, -64, -16, 9),
			;
			public static final VeinType[] VALUES = values();
			public static final Codec<VeinType> CODEC = Codec.INT.xmap(i -> VALUES[i], VeinType::ordinal);

			public final EnumMetals metal;
			private final OreDistribution defaultDistribution;
			private final double defaultAirExposure;
			private final int defaultVeinSize;
			private final int defaultMinY;
			private final int defaultMaxY;
			private final int defaultVeinsPerChunk;

			VeinType(EnumMetals metal, OreDistribution defaultDistribution, double defaultAirExposure, int defaultVeinSize, int defaultMinY, int defaultMaxY, int defaultVeinsPerChunk)
			{
				this.metal = metal;
				this.defaultDistribution = defaultDistribution;
				this.defaultAirExposure = defaultAirExposure;
				this.defaultVeinSize = defaultVeinSize;
				this.defaultMinY = defaultMinY;
				this.defaultMaxY = defaultMaxY;
				this.defaultVeinsPerChunk = defaultVeinsPerChunk;
			}

			public String getVeinName()
			{
				return name().toLowerCase(Locale.ROOT);
			}
		}
	}


	private static IntValue addPositive(Builder builder, String name, int defaultVal, String... desc)
	{
		return builder
				.comment(desc)
				.defineInRange(name, defaultVal, 1, Integer.MAX_VALUE);
	}

	public static final ForgeConfigSpec CONFIG_SPEC;
	public static final Wires WIRES;
	public static final Machines MACHINES;
	public static final Ores ORES;

	static
	{
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		WIRES = new Wires(builder);
		MACHINES = new Machines(builder);
		ORES = new Ores(builder);
		CONFIG_SPEC = builder.build();
	}

	private static Config rawConfig;

	public static Config getRawConfig()
	{
		return Preconditions.checkNotNull(rawConfig);
	}

	@SubscribeEvent
	public static void onConfigReload(ModConfigEvent ev)
	{
		if(CONFIG_SPEC==ev.getConfig().getSpec())
		{
			rawConfig = ev.getConfig().getConfigData();
			refresh();
		}
	}

	public static int getOrDefault(IntValue value)
	{
		return CONFIG_SPEC.isLoaded()?value.get(): value.getDefault();
	}

	public static void refresh()
	{
//		ExcavatorHandler.mineralVeinYield = IEServerConfig.MACHINES.excavator_yield.get();
	}
}
