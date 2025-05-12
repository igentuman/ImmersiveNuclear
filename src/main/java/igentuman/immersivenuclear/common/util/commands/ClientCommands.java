/*
 * BluSunrize
 * Copyright (c) 2022
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.common.util.commands;

import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.api.Lib;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Lib.MODID, value = Dist.CLIENT)
public class ClientCommands
{
	@SubscribeEvent
	public static void registerClientCommands(RegisterClientCommandsEvent ev)
	{
		LiteralArgumentBuilder<CommandSourceStack> main = Commands.literal("cie");
		main.then(createResetRender())
				.then(createResetManual());
		ev.getDispatcher().register(main);
	}

	public static LiteralArgumentBuilder<CommandSourceStack> createResetRender()
	{
		LiteralArgumentBuilder<CommandSourceStack> ret = Commands.literal("resetrender");
		ret.executes(context -> {
			ImmersiveNuclear.proxy.clearRenderCaches();
			return Command.SINGLE_SUCCESS;
		});
		return ret;
	}

	public static LiteralArgumentBuilder<CommandSourceStack> createResetManual()
	{
		LiteralArgumentBuilder<CommandSourceStack> ret = Commands.literal("resetmanual");
		ret.executes(context -> {
			ImmersiveNuclear.proxy.resetManual();
			return Command.SINGLE_SUCCESS;
		});
		return ret;
	}
}
