package igentuman.immersivenuclear.common.blocks.metal;

import igentuman.immersivenuclear.common.IPTileTypes;
import igentuman.immersivenuclear.common.blocks.IPMetalMultiblock;
import igentuman.immersivenuclear.common.blocks.tileentities.CoolingTowerTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class CoolingTowerBlock extends IPMetalMultiblock<CoolingTowerTileEntity>{
	public CoolingTowerBlock(){
		super("coolingtower", () -> IPTileTypes.TOWER.get());
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit){
		if(!player.getHeldItem(hand).isEmpty()){
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof CoolingTowerTileEntity){
				CoolingTowerTileEntity tower = (CoolingTowerTileEntity) te;
				BlockPos tPos = tower.posInMultiblock;
				Direction facing = tower.getFacing();
				
				// Locations that don't require sneaking to avoid the GUI
				
				// Power input
				if(CoolingTowerTileEntity.Energy_IN.contains(tPos) && hit.getFace() == Direction.UP){
					return ActionResultType.FAIL;
				}
				
				// Redstone controller input
				if(CoolingTowerTileEntity.Redstone_IN.contains(tPos) && (tower.getIsMirrored() ? hit.getFace() == facing.rotateY() : hit.getFace() == facing.rotateYCCW())){
					return ActionResultType.FAIL;
				}
				
				// Fluid I/O Ports
				if((tPos.equals(CoolingTowerTileEntity.Fluid_IN) && (tower.getIsMirrored() ? hit.getFace() == facing.rotateYCCW() : hit.getFace() == facing.rotateY()))
				|| (tPos.equals(CoolingTowerTileEntity.Fluid_OUT) && hit.getFace() == facing.getOpposite())){
					return ActionResultType.FAIL;
				}
			}
		}
		return super.onBlockActivated(state, world, pos, player, hand, hit);
	}
	
	@Override
	public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity){
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof CoolingTowerTileEntity){
			return ((CoolingTowerTileEntity) te).isLadder();
		}
		return false;
	}
}
