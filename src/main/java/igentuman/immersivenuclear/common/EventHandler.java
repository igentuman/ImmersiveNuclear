/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.common;

import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.common.util.IEExplosion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;

public class EventHandler
{
	public static Map<Level, Set<IEExplosion>> currentExplosions = new WeakHashMap<>();
	public static final Queue<Runnable> SERVER_TASKS = new ArrayDeque<>();

	@SubscribeEvent
	public void onLoad(LevelEvent.Load event)
	{
		ImmersiveNuclear.proxy.onWorldLoad();
	}

	@SubscribeEvent
	public void onWorldTick(LevelTickEvent event)
	{
		if(event.level.isClientSide||event.phase!=TickEvent.Phase.START)
			return;

		// Explicitly support tasks adding more tasks to be delayed
		int numToRun = SERVER_TASKS.size();
		for(int i = 0; i < numToRun; ++i)
		{
			Runnable next = SERVER_TASKS.poll();
			if(next!=null)
				next.run();
		}

		final Set<IEExplosion> explosionsInLevel = currentExplosions.get(event.level);
		if(explosionsInLevel!=null)
		{
			Iterator<IEExplosion> itExplosion = explosionsInLevel.iterator();
			while(itExplosion.hasNext())
			{
				IEExplosion ex = itExplosion.next();
				ex.doExplosionTick();
				if(ex.isExplosionFinished)
					itExplosion.remove();
			}
		}
	}

	@SubscribeEvent
	public void onLivingJump(LivingJumpEvent event)
	{

	}


	@SubscribeEvent
	public void onBlockRightclick(RightClickBlock event)
	{

	}
}