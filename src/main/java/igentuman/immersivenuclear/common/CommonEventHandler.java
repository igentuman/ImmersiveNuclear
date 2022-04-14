package igentuman.immersivenuclear.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.common.cfg.IPServerConfig;
import igentuman.immersivenuclear.common.fluids.NapalmFluid;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommonEventHandler{
	@SubscribeEvent
	public void onSave(WorldEvent.Save event){
		if(!event.getWorld().isRemote()){
			IPSaveData.markInstanceAsDirty();
		}
	}
	
	@SubscribeEvent
	public void onUnload(WorldEvent.Unload event){
		if(!event.getWorld().isRemote()){
			IPSaveData.markInstanceAsDirty();
		}
	}
	
	static final Random random = new Random();

	
	@SubscribeEvent
	public void onEntityJoiningWorld(EntityJoinWorldEvent event){
		if(event.getEntity() instanceof PlayerEntity){
			if(event.getEntity() instanceof FakePlayer){
				return;
			}
			
			if(IPServerConfig.MISCELLANEOUS.autounlock_recipes.get()){
				List<IRecipe<?>> l = new ArrayList<IRecipe<?>>();
				Collection<IRecipe<?>> recipes = event.getWorld().getRecipeManager().getRecipes();
				recipes.forEach(recipe -> {
					ResourceLocation name = recipe.getId();
					if(name.getNamespace() == ImmersiveNuclear.MODID){
						if(recipe.getRecipeOutput().getItem() != null){
							l.add(recipe);
						}
					}
				});
				
				((PlayerEntity) event.getEntity()).unlockRecipes(l);
			}
		}
	}
	
	@SubscribeEvent
	public void test(LivingEvent.LivingUpdateEvent event){
		if(event.getEntityLiving() instanceof PlayerEntity){
			// event.getEntityLiving().setFire(1);
		}
	}
	
	public static Map<ResourceLocation, List<BlockPos>> napalmPositions = new HashMap<>();
	public static Map<ResourceLocation, List<BlockPos>> toRemove = new HashMap<>();
	
	@SubscribeEvent
	public void handleNapalm(WorldTickEvent event){
		ResourceLocation d = event.world.getDimensionKey().getRegistryName();
		
		if(event.phase == Phase.START){
			toRemove.put(d, new ArrayList<>());
			if(napalmPositions.get(d) != null){
				List<BlockPos> iterate = new ArrayList<>(napalmPositions.get(d));
				for(BlockPos position:iterate){
					BlockState state = event.world.getBlockState(position);
					if(state.getBlock() instanceof FlowingFluidBlock && state.getBlock() == IPContent.Fluids.napalm.block){
						((NapalmFluid) IPContent.Fluids.napalm).processFire(event.world, position);
					}
					toRemove.get(d).add(position);
				}
			}
		}else if(event.phase == Phase.END){
			if(toRemove.get(d) != null && napalmPositions.get(d) != null){
				for(BlockPos position:toRemove.get(d)){
					napalmPositions.get(d).remove(position);
				}
			}
		}
	}
}
