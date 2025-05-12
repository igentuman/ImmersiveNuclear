/*
 *  BluSunrize
 *  Copyright (c) 2021
 *
 *  This code is licensed under "Blu's License of Common Sense"
 *  Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.common.register;

import blusunrize.immersiveengineering.common.register.IEMenuTypes;
import igentuman.immersivenuclear.api.Lib;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class INMenuTypes extends IEMenuTypes
{
	public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Lib.MODID);

	/*public static final MultiblockContainer<AlloySmelterLogic.State, AlloySmelterMenu> ALLOY_SMELTER = registerMultiblock(
			Lib.GUIID_AlloySmelter, AlloySmelterMenu::makeServer, AlloySmelterMenu::makeClient
	);*/


}
