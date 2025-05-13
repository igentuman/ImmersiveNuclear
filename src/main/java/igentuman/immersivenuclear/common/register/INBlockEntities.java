/*
 * BluSunrize
 * Copyright (c) 2020
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 *
 */

package igentuman.immersivenuclear.common.register;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IGeneralMultiblock;
import blusunrize.immersiveengineering.common.blocks.MultiblockBEType;
import blusunrize.immersiveengineering.common.blocks.metal.EnergyConnectorBlockEntity;
import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.common.blocks.metal.*;
import igentuman.immersivenuclear.common.config.IEServerConfig;
import igentuman.immersivenuclear.common.register.INBlocks.*;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class INBlockEntities
{
	public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(
			ForgeRegistries.BLOCK_ENTITY_TYPES, ImmersiveNuclear.MODID
	);

	public static final RegistryObject<BlockEntityType<TransformerEVBlockEntity>> TRANSFORMER_EV = REGISTER.register(
			"transformerev", makeType(TransformerEVBlockEntity::new, Connectors.TRANSFORMER_EV)
	);

	public static final RegistryObject<BlockEntityType<CapacitorBlockEntity>> CAPACITOR_EV = REGISTER.register(
			"capacitorev", makeType((pos, state) -> new CapacitorBlockEntity(IEServerConfig.MACHINES.evCapConfig, pos, state), MetalDevices.CAPACITOR_EV)
	);

	static
	{
		EnergyConnectorBlockEntity.registerConnectorTEs(REGISTER);
	}

	public static <T extends BlockEntity> Supplier<BlockEntityType<T>> makeType(BlockEntityType.BlockEntitySupplier<T> create, Supplier<? extends Block> valid)
	{
		return makeTypeMultipleBlocks(create, ImmutableSet.of(valid));
	}

	public static <T extends BlockEntity> Supplier<BlockEntityType<T>> makeTypeMultipleBlocks(
			BlockEntityType.BlockEntitySupplier<T> create, Collection<? extends Supplier<? extends Block>> valid
	)
	{
		return () -> new BlockEntityType<>(
				create, ImmutableSet.copyOf(valid.stream().map(Supplier::get).collect(Collectors.toList())), null
		);
	}

	private static <T extends BlockEntity & IGeneralMultiblock>
	MultiblockBEType<T> makeMultiblock(String name, MultiblockBEType.BEWithTypeConstructor<T> make, Supplier<? extends Block> block)
	{
		return new MultiblockBEType<>(
				name, REGISTER, make, block, state -> state.hasProperty(IEProperties.MULTIBLOCKSLAVE)&&!state.getValue(IEProperties.MULTIBLOCKSLAVE)
		);
	}
}
