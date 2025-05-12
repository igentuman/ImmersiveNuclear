/*
 * BluSunrize
 * Copyright (c) 2023
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.common.register;

import igentuman.immersivenuclear.api.Lib;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class INMultiblockLogic
{
	public static final DeferredRegister<Block> BLOCK_REGISTER = DeferredRegister.create(
			ForgeRegistries.BLOCKS, Lib.MODID
	);
	private static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(
			ForgeRegistries.ITEMS, Lib.MODID
	);
	private static final DeferredRegister<BlockEntityType<?>> BE_REGISTER = DeferredRegister.create(
			ForgeRegistries.BLOCK_ENTITY_TYPES, Lib.MODID
	);


}
