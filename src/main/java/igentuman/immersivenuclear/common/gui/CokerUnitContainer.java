package igentuman.immersivenuclear.common.gui;

import static igentuman.immersivenuclear.common.blocks.tileentities.CokerUnitTileEntity.TANK_INPUT;

import igentuman.immersivenuclear.api.crafting.CokerUnitRecipe;
import igentuman.immersivenuclear.common.blocks.tileentities.CokerUnitTileEntity;
import igentuman.immersivenuclear.common.blocks.tileentities.CokerUnitTileEntity.Inventory;
import igentuman.immersivenuclear.common.gui.IPSlot.FluidContainer.FluidFilter;
import igentuman.immersivenuclear.common.multiblocks.CokerUnitMultiblock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

public class CokerUnitContainer extends MultiblockAwareGuiContainer<CokerUnitTileEntity>{
	public CokerUnitContainer(int id, PlayerInventory playerInventory, final CokerUnitTileEntity tile){
		super(tile, id, CokerUnitMultiblock.INSTANCE);
		
		addSlot(new IPSlot.CokerInput(this, this.inv, Inventory.INPUT.id(), 20, 71));
		addSlot(new IPSlot(this.inv, Inventory.INPUT_FILLED.id(), 9, 14){
			@Override
			public boolean isItemValid(ItemStack stack){
				return FluidUtil.getFluidHandler(stack).map(h -> {
					if(h.getTanks() <= 0 || h.getFluidInTank(0).isEmpty()){
						return false;
					}
					
					FluidStack fs = h.getFluidInTank(0);
					if(fs.isEmpty() || (tile.bufferTanks[TANK_INPUT].getFluidAmount() > 0 && !fs.isFluidEqual(tile.bufferTanks[TANK_INPUT].getFluid()))){
						return false;
					}
					
					return CokerUnitRecipe.hasRecipeWithInput(fs, true);
				}).orElse(false);
			}
		});
		addSlot(new IPSlot.ItemOutput(this.inv, Inventory.INPUT_EMPTY.id(), 9, 45));
		
		addSlot(new IPSlot.FluidContainer(this.inv, Inventory.OUTPUT_EMPTY.id(), 175, 14, FluidFilter.EMPTY));
		addSlot(new IPSlot.ItemOutput(this.inv, Inventory.OUTPUT_FILLED.id(), 175, 45));
		
		this.slotCount = Inventory.values().length;
		
		// Player Inventory
		for(int i = 0;i < 3;i++){
			for(int j = 0;j < 9;j++){
				addSlot(new Slot(playerInventory, j + i * 9 + 9, 20 + j * 18, 105 + i * 18));
			}
		}
		
		// Hotbar
		for(int i = 0;i < 9;i++){
			addSlot(new Slot(playerInventory, i, 20 + i * 18, 163));
		}
	}
}
