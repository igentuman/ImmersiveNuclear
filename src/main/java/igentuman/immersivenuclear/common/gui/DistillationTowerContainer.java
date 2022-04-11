package igentuman.immersivenuclear.common.gui;

import igentuman.immersivenuclear.api.crafting.DistillationRecipe;
import igentuman.immersivenuclear.common.blocks.tileentities.DistillationTowerTileEntity;
import igentuman.immersivenuclear.common.gui.IPSlot.FluidContainer.FluidFilter;
import igentuman.immersivenuclear.common.multiblocks.DistillationTowerMultiblock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

public class DistillationTowerContainer extends MultiblockAwareGuiContainer<DistillationTowerTileEntity>{
	public DistillationTowerContainer(int id, PlayerInventory playerInventory, final DistillationTowerTileEntity tile){
		super(tile, id, DistillationTowerMultiblock.INSTANCE);
		
		addSlot(new IPSlot(this.inv, DistillationTowerTileEntity.INV_0, 12, 17){
			@Override
			public boolean isItemValid(ItemStack stack){
				return FluidUtil.getFluidHandler(stack).map(h -> {
					if(h.getTanks() <= 0){
						return false;
					}
					
					FluidStack fs = h.getFluidInTank(0);
					if(fs.isEmpty() || (tile.tanks[DistillationTowerTileEntity.TANK_INPUT].getFluidAmount() > 0 && !fs.isFluidEqual(tile.tanks[DistillationTowerTileEntity.TANK_INPUT].getFluid()))){
						return false;
					}
					
					DistillationRecipe recipe = DistillationRecipe.findRecipe(fs);
					return recipe != null;
				}).orElse(false);
			}
		});
		addSlot(new IPSlot.ItemOutput(this.inv, DistillationTowerTileEntity.INV_1, 12, 53));
		
		addSlot(new IPSlot.FluidContainer(this.inv, DistillationTowerTileEntity.INV_2, 134, 17, FluidFilter.EMPTY));
		addSlot(new IPSlot.ItemOutput(this.inv, DistillationTowerTileEntity.INV_3, 134, 53));
		
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
