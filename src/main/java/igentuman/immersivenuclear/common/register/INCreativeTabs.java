/*
 * BluSunrize
 * Copyright (c) 2023
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.common.register;

import blusunrize.immersiveengineering.common.blocks.IEBaseBlock;
import blusunrize.immersiveengineering.common.items.IEBaseItem;
import blusunrize.immersiveengineering.common.register.IEItems.Misc;
import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.api.Lib;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.Row;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(modid = Lib.MODID, bus = Bus.MOD)
public class INCreativeTabs
{
	public static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(
			Registries.CREATIVE_MODE_TAB, Lib.MODID
	);

	private static RegistryObject<CreativeModeTab> TAB = REGISTER.register(
			"main",
			() -> new CreativeModeTab.Builder(Row.TOP, 0)
					.icon(() -> Misc.SHIELD.get().getDefaultInstance())
					.title(Component.literal(ImmersiveNuclear.MODNAME))
					.displayItems(INCreativeTabs::fillIETab)
					.build()
	);


	private static void fillIETab(CreativeModeTab.ItemDisplayParameters parms, CreativeModeTab.Output out)
	{
		for(final RegistryObject<Item> itemRef : INItems.REGISTER.getEntries())
		{
			final Item item = itemRef.get();
			if(item==Misc.POTION_BUCKET.get())
				continue;
			if(item instanceof IEBaseItem ieItem)
				ieItem.fillCreativeTab(out);
			else if(item instanceof BlockItem blockItem&&blockItem.getBlock() instanceof IEBaseBlock ieBlock)
				ieBlock.fillCreativeTab(out);
			else
				out.accept(itemRef.get());
		}
	}
}
