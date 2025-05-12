/*
 * BluSunrize
 * Copyright (c) 2023
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.common;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Supplier;

public class CommonProxy
{
	public void onWorldLoad()
	{
	}

	public void resetManual()
	{
	}

	public void handleTileSound(Supplier<SoundEvent> soundEvent, BlockEntity tile, boolean tileActive, float volume, float pitch)
	{
	}


	public Level getClientWorld()
	{
		return null;
	}

	public Player getClientPlayer()
	{
		return null;
	}

	public void reInitGui()
	{
	}

	public void clearRenderCaches()
	{
	}

	public void openManual()
	{

	}

	public void openTileScreen(String guiId, BlockEntity tileEntity)
	{
	}
}