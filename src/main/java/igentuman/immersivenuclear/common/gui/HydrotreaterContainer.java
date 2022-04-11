package igentuman.immersivenuclear.common.gui;

import igentuman.immersivenuclear.common.blocks.tileentities.HydrotreaterTileEntity;
import igentuman.immersivenuclear.common.multiblocks.HydroTreaterMultiblock;
import net.minecraft.entity.player.PlayerInventory;

public class HydrotreaterContainer extends MultiblockAwareGuiContainer<HydrotreaterTileEntity>{
	public HydrotreaterContainer(int id, PlayerInventory playerInventory, final HydrotreaterTileEntity tile){
		super(tile, id, HydroTreaterMultiblock.INSTANCE);
	}
}
