package igentuman.immersivenuclear.common.items;

import java.util.List;
import java.util.Set;

import igentuman.immersivenuclear.common.blocks.tileentities.CoolingTowerTileEntity;
import igentuman.immersivenuclear.common.blocks.tileentities.GasGeneratorTileEntity;
import org.lwjgl.glfw.GLFW;

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import blusunrize.immersiveengineering.common.util.inventory.MultiFluidTank;
import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.client.model.IPModels;
import igentuman.immersivenuclear.common.IPContent;
import igentuman.immersivenuclear.common.network.IPPacketHandler;
import igentuman.immersivenuclear.common.network.MessageDebugSync;
import igentuman.immersivenuclear.common.particle.IPParticleTypes;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;

public class DebugItem extends IPItemBase{
	protected static enum Modes{
		DISABLED("Disabled"),
		INFO_SPEEDBOAT("Info: Speedboat."),
		INFO_TE_AUTOLUBE("Info: AutoLubricator."),
		INFO_TE_GASGEN("Info: Portable Generator."),
		INFO_TE_MULTIBLOCK("Info: Powered Multiblock."),
		INFO_TE_DISTILLATION_TOWER("Info: Distillation Tower."),
		RESERVOIR("Create/Get Reservoir"),
		RESERVOIR_BIG_SCAN("Scan 5 Block Radius Area"),
		CLEAR_RESERVOIR_CACHE("Clear Reservoir Cache"),
		REFRESH_ALL_IPMODELS("Refresh all IPModels"),
		GENERAL_TEST("You may not want to trigger this.")
		;
		
		public final String display;
		private Modes(String display){
			this.display = display;
		}
	}
	
	public DebugItem(){
		super("debug");
	}
	
	@Override
	public ITextComponent getDisplayName(ItemStack stack){
		return new StringTextComponent("IP Debugging Tool").mergeStyle(TextFormatting.LIGHT_PURPLE);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new StringTextComponent("[Shift + Scroll-UP/DOWN] Change mode.").mergeStyle(TextFormatting.GRAY));
		Modes mode = getMode(stack);
		if(mode == Modes.DISABLED){
			tooltip.add(new StringTextComponent("  Disabled.").mergeStyle(TextFormatting.DARK_GRAY));
		}else{
			tooltip.add(new StringTextComponent("  " + mode.display).mergeStyle(TextFormatting.DARK_GRAY));
		}
		
		tooltip.add(new StringTextComponent("You're not supposed to have this.").mergeStyle(TextFormatting.DARK_RED));
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items){
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn){
		if(!worldIn.isRemote){
			Modes mode = DebugItem.getMode(playerIn.getHeldItem(handIn));
			
			switch(mode){
				case REFRESH_ALL_IPMODELS:{
					IPModels.getModels().forEach(m -> m.init());
					
					playerIn.sendStatusMessage(new StringTextComponent("Models refreshed."), true);
					
					return new ActionResult<ItemStack>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
				}
				default:
					break;
			}
			return new ActionResult<ItemStack>(ActionResultType.PASS, playerIn.getHeldItem(handIn));
		}
		
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}
	
	@Override
	public ActionResultType onItemUse(ItemUseContext context){
		PlayerEntity player = context.getPlayer();
		if(player == null){
			return ActionResultType.PASS;
		}
		
		ItemStack held = player.getHeldItem(context.getHand());
		Modes mode = DebugItem.getMode(held);
		
		TileEntity te = context.getWorld().getTileEntity(context.getPos());
		switch(mode){
			case GENERAL_TEST:{
				World world = context.getWorld();
				if(world.isRemote){
					// Client
					BlockPos pos = context.getPos();

					float xa = 0.0625F * (float) Math.random();
					float ya = 0.0625F;
					float za = 0.0625F * (float) Math.random();
					
					world.addParticle(IPParticleTypes.FLARE_FIRE, true, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, xa, ya, za);
				}else{
					// Server
				}
				
				return ActionResultType.SUCCESS;
			}
			case INFO_TE_DISTILLATION_TOWER:{
				if(te instanceof CoolingTowerTileEntity && !context.getWorld().isRemote){
					CoolingTowerTileEntity tower = (CoolingTowerTileEntity) te;
					if(!tower.offsetToMaster.equals(BlockPos.ZERO)){
						tower = tower.master();
					}
					
					IFormattableTextComponent tankInText = new StringTextComponent("\nInputFluids: ");
					{
						MultiFluidTank tank = tower.tanks[CoolingTowerTileEntity.TANK_OUTPUT];
						for(int i = 0;i < tank.fluids.size();i++){
							FluidStack fstack = tank.fluids.get(i);
							tankInText.appendString(" ").appendSibling(fstack.getDisplayName()).appendString(" " + fstack.getAmount() + "mB,");
						}
					}
					
					IFormattableTextComponent tankOutText = new StringTextComponent("\nOutputFluids: ");
					{
						MultiFluidTank tank = tower.tanks[CoolingTowerTileEntity.TANK_INPUT];
						for(int i = 0;i < tank.fluids.size();i++){
							FluidStack fstack = tank.fluids.get(i);
							tankOutText.appendString(" ").appendSibling(fstack.getDisplayName()).appendString(" " + fstack.getAmount() + "mB,");
						}
					}
					
					player.sendMessage(new StringTextComponent("DistillationTower:\n").appendSibling(tankInText).appendSibling(tankOutText), Util.DUMMY_UUID);
				}
				return ActionResultType.PASS;
			}
			case INFO_TE_MULTIBLOCK:{
				if(te instanceof PoweredMultiblockTileEntity && !context.getWorld().isRemote){ // Generic
					PoweredMultiblockTileEntity<?, ?> poweredMultiblock = (PoweredMultiblockTileEntity<?, ?>) te;
					
					Vector3i loc = poweredMultiblock.posInMultiblock;
					Set<BlockPos> energyInputs = poweredMultiblock.getEnergyPos();
					Set<BlockPos> redstoneInputs = poweredMultiblock.getRedstonePos();
					
					IFormattableTextComponent out = new StringTextComponent("[" + loc.getX() + " " + loc.getY() + " " + loc.getZ() + "]: ");
					
					for(BlockPos pos:energyInputs){
						if(pos.equals(loc)){
							out.appendString("Energy Port.");
						}
					}
					
					for(BlockPos pos:redstoneInputs){
						if(pos.equals(loc)){
							out.appendString("Redstone Port.");
						}
					}
					
					if(poweredMultiblock.offsetToMaster.equals(BlockPos.ZERO)){
						out.appendString("Master.");
					}
					
					out.appendString(" (Facing: " + poweredMultiblock.getFacing() + ", Block-Face: " + context.getFace() + ")");
					
					player.sendStatusMessage(out, true);
					return ActionResultType.SUCCESS;
				}
				break;
			}
			case INFO_TE_GASGEN:{
				if(te instanceof GasGeneratorTileEntity){
					GasGeneratorTileEntity gas = (GasGeneratorTileEntity) te;
					
					IFormattableTextComponent out = new StringTextComponent(context.getWorld().isRemote ? "CLIENT: " : "SERVER: ");
					out.appendString(gas.getFacing() + ", ");
					out.appendString(gas.getEnergyStored(null) + ", ");
					out.appendString(gas.getMaxEnergyStored(null) + ", ");
					
					player.sendMessage(out, Util.DUMMY_UUID);
					
					return ActionResultType.SUCCESS;
				}
				break;
			}
			default:
				break;
		}
		
		return ActionResultType.PASS;
	}
	
	public static void setModeServer(ItemStack stack, Modes mode){
		CompoundNBT nbt = getSettings(stack);
		nbt.putInt("mode", mode.ordinal());
	}
	
	public static void setModeClient(ItemStack stack, Modes mode){
		CompoundNBT nbt = getSettings(stack);
		nbt.putInt("mode", mode.ordinal());
		IPPacketHandler.sendToServer(new MessageDebugSync(nbt));
	}
	
	public static Modes getMode(ItemStack stack){
		CompoundNBT nbt = getSettings(stack);
		if(nbt.contains("mode")){
			int mode = nbt.getInt("mode");
			
			if(mode < 0 || mode >= Modes.values().length)
				mode = 0;
			
			return Modes.values()[mode];
		}
		return Modes.DISABLED;
	}
	
	public static CompoundNBT getSettings(ItemStack stack){
		return stack.getOrCreateChildTag("settings");
	}
	
	@Mod.EventBusSubscriber(modid = ImmersiveNuclear.MODID, value = Dist.CLIENT)
	public static class ClientInputHandler{
		static boolean shiftHeld = false;
		
		@SubscribeEvent
		public static void handleScroll(InputEvent.MouseScrollEvent event){
			double delta = event.getScrollDelta();
			
			if(shiftHeld && delta != 0.0){
				PlayerEntity player = ClientUtils.mc().player;
				ItemStack mainItem = player.getHeldItemMainhand();
				ItemStack secondItem = player.getHeldItemOffhand();
				boolean main = !mainItem.isEmpty() && mainItem.getItem() == IPContent.debugItem;
				boolean off = !secondItem.isEmpty() && secondItem.getItem() == IPContent.debugItem;
				
				if(main || off){
					ItemStack target = main ? mainItem : secondItem;
					
					Modes mode = DebugItem.getMode(target);
					int id = mode.ordinal() + (int) delta;
					if(id < 0){
						id = Modes.values().length - 1;
					}
					if(id >= Modes.values().length){
						id = 0;
					}
					mode = Modes.values()[id];
					
					DebugItem.setModeClient(target, mode);
					player.sendStatusMessage(new StringTextComponent(mode.display), true);
					event.setCanceled(true);
				}
			}
		}
		
		@SubscribeEvent
		public static void handleKey(InputEvent.KeyInputEvent event){
			if(event.getKey() == GLFW.GLFW_KEY_RIGHT_SHIFT || event.getKey() == GLFW.GLFW_KEY_LEFT_SHIFT){
				switch(event.getAction()){
					case GLFW.GLFW_PRESS:{
						shiftHeld = true;
						return;
					}
					case GLFW.GLFW_RELEASE:{
						shiftHeld = false;
						return;
					}
				}
			}
		}
	}
}
