package igentuman.immersivenuclear.common.blocks.metal;

import igentuman.immersivenuclear.common.IPTileTypes;
import igentuman.immersivenuclear.common.blocks.IPMetalMultiblock;
import igentuman.immersivenuclear.common.blocks.tileentities.PumpjackTileEntity;

public class PumpjackBlock extends IPMetalMultiblock<PumpjackTileEntity>{
	public PumpjackBlock(){
		super("pumpjack", () -> IPTileTypes.PUMP.get());
	}
}
