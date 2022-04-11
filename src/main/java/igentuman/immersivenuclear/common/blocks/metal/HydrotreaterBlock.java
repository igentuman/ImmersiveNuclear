package igentuman.immersivenuclear.common.blocks.metal;

import igentuman.immersivenuclear.common.IPTileTypes;
import igentuman.immersivenuclear.common.blocks.IPMetalMultiblock;
import igentuman.immersivenuclear.common.blocks.tileentities.HydrotreaterTileEntity;

public class HydrotreaterBlock extends IPMetalMultiblock<HydrotreaterTileEntity>{
	public HydrotreaterBlock(){
		super("hydrotreater", () -> IPTileTypes.TREATER.get());
	}
}
