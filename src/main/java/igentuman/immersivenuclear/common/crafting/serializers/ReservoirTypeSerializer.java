package igentuman.immersivenuclear.common.crafting.serializers;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.api.crafting.pumpjack.PumpjackHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class ReservoirTypeSerializer extends IERecipeSerializer<PumpjackHandler.ReservoirType>{
	@Override
	public PumpjackHandler.ReservoirType readFromJson(ResourceLocation recipeId, JsonObject json){
		String name = JSONUtils.getString(json, "name");
		ResourceLocation fluid = new ResourceLocation(JSONUtils.getString(json, "fluid"));
		int min = JSONUtils.getInt(json, "fluidminimum");
		int max = JSONUtils.getInt(json, "fluidcapacity");
		int trace = JSONUtils.getInt(json, "fluidtrace");
		int weight = JSONUtils.getInt(json, "weight");
		
		PumpjackHandler.ReservoirType type = new PumpjackHandler.ReservoirType(name, recipeId, fluid, min, max, trace, weight);
		
		ImmersiveNuclear.log.debug(String.format("Loaded reservoir %s as %s, with %smB to %smB of %s and %smB trace, with %s of weight.",
				recipeId, name, min, max, fluid, trace, weight));
		
		if(JSONUtils.hasField(json, "dimension")){
			JsonObject dimension = JSONUtils.getJsonObject(json, "dimension");
			
			List<ResourceLocation> whitelist = new ArrayList<>();
			List<ResourceLocation> blacklist = new ArrayList<>();
			
			if(JSONUtils.hasField(dimension, "whitelist")){
				JsonArray array = JSONUtils.getJsonArray(dimension, "whitelist");
				for(JsonElement obj:array){
					whitelist.add(new ResourceLocation(obj.getAsString()));
				}
			}
			
			if(JSONUtils.hasField(dimension, "blacklist")){
				JsonArray array = JSONUtils.getJsonArray(dimension, "blacklist");
				for(JsonElement obj:array){
					blacklist.add(new ResourceLocation(obj.getAsString()));
				}
			}
			
			if(whitelist.size() > 0){
				ImmersiveNuclear.log.debug("- Adding these to dimension-whitelist for {} -", name);
				whitelist.forEach(ins -> ImmersiveNuclear.log.debug(ins));
				type.addDimension(false, whitelist);
			}else if(blacklist.size() > 0){
				ImmersiveNuclear.log.debug("- Adding these to dimension-blacklist for {} -", name);
				blacklist.forEach(ins -> ImmersiveNuclear.log.debug(ins));
				type.addDimension(true, blacklist);
			}
		}
		
		if(JSONUtils.hasField(json, "biome")){
			JsonObject biome = JSONUtils.getJsonObject(json, "biome");
			
			List<ResourceLocation> whitelist = new ArrayList<>();
			List<ResourceLocation> blacklist = new ArrayList<>();
			
			if(JSONUtils.hasField(biome, "whitelist")){
				JsonArray array = JSONUtils.getJsonArray(biome, "whitelist");
				for(JsonElement obj:array){
					whitelist.add(new ResourceLocation(obj.getAsString()));
				}
			}
			
			if(JSONUtils.hasField(biome, "blacklist")){
				JsonArray array = JSONUtils.getJsonArray(biome, "blacklist");
				for(JsonElement obj:array){
					blacklist.add(new ResourceLocation(obj.getAsString()));
				}
			}
			
			if(whitelist.size() > 0){
				ImmersiveNuclear.log.debug("- Adding these to biome-whitelist for {} -", name);
				whitelist.forEach(ins -> ImmersiveNuclear.log.debug(ins));
				type.addBiome(false, whitelist);
			}else if(blacklist.size() > 0){
				ImmersiveNuclear.log.debug("- Adding these to biome-blacklist for {} -", name);
				blacklist.forEach(ins -> ImmersiveNuclear.log.debug(ins));
				type.addBiome(true, blacklist);
			}
		}
		
		return type;
	}
	
	@Override
	public PumpjackHandler.ReservoirType read(ResourceLocation recipeId, PacketBuffer buffer){
		return new PumpjackHandler.ReservoirType(buffer.readCompoundTag()); // Very convenient having the NBT stuff already.
	}
	
	@Override
	public void write(PacketBuffer buffer, PumpjackHandler.ReservoirType recipe){
		buffer.writeCompoundTag(recipe.writeToNBT());
	}
	
	@Override
	public ItemStack getIcon(){
		return ItemStack.EMPTY;
	}
}
