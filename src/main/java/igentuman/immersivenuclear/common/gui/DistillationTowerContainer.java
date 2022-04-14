package igentuman.immersivenuclear.common.gui;

import igentuman.immersivenuclear.common.blocks.tileentities.CoolingTowerTileEntity;
import igentuman.immersivenuclear.common.multiblocks.DistillationTowerMultiblock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;

public class DistillationTowerContainer extends MultiblockAwareGuiContainer<CoolingTowerTileEntity>{
	public DistillationTowerContainer(int id, PlayerInventory playerInventory, final CoolingTowerTileEntity tile){
		super(tile, id, DistillationTowerMultiblock.INSTANCE);


		
		slotCount = 4;
		
		for(int i = 0;i < 3;i++){
			for(int j = 0;j < 9;j++){
				addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 85 + i * 18));
			}
		}
		for(int i = 0;i < 9;i++){
			addSlot(new Slot(playerInventory, i, 8 + i * 18, 143));
		}
	}
}
