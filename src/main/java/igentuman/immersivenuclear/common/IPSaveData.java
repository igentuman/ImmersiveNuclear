package igentuman.immersivenuclear.common;

import java.util.Map;

import blusunrize.immersiveengineering.api.DimensionChunkCoords;
import igentuman.immersivenuclear.api.crafting.LubricatedHandler;
import igentuman.immersivenuclear.api.crafting.LubricatedHandler.LubricatedTileInfo;
import igentuman.immersivenuclear.api.crafting.pumpjack.PumpjackHandler;
import igentuman.immersivenuclear.api.crafting.pumpjack.ReservoirWorldInfo;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.storage.WorldSavedData;

public class IPSaveData extends WorldSavedData{
	public static final String dataName = "ImmersiveNuclear-SaveData";
	
	public IPSaveData(){
		super(dataName);
	}
	
	@Override
	public void read(CompoundNBT nbt){
		ListNBT oilList = nbt.getList("oilInfo", 10);
		PumpjackHandler.reservoirsCache.clear();
		for(int i = 0;i < oilList.size();i++){
			CompoundNBT tag = oilList.getCompound(i);
			DimensionChunkCoords coords = DimensionChunkCoords.readFromNBT(tag);
			if(coords != null){
				ReservoirWorldInfo info = ReservoirWorldInfo.readFromNBT(tag.getCompound("info"));
				PumpjackHandler.reservoirsCache.put(coords, info);
			}
		}
		
		ListNBT lubricatedList = nbt.getList("lubricated", 10);
		LubricatedHandler.lubricatedTiles.clear();
		for(int i = 0;i < lubricatedList.size();i++){
			CompoundNBT tag = lubricatedList.getCompound(i);
			LubricatedTileInfo info = new LubricatedTileInfo(tag);
			LubricatedHandler.lubricatedTiles.add(info);
		}
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt){
		ListNBT oilList = new ListNBT();
		for(Map.Entry<DimensionChunkCoords, ReservoirWorldInfo> e:PumpjackHandler.reservoirsCache.entrySet()){
			if(e.getKey() != null && e.getValue() != null){
				CompoundNBT tag = e.getKey().writeToNBT();
				tag.put("info", e.getValue().writeToNBT());
				oilList.add(tag);
			}
		}
		nbt.put("oilInfo", oilList);
		
		ListNBT lubricatedList = new ListNBT();
		for(LubricatedTileInfo info:LubricatedHandler.lubricatedTiles){
			if(info != null){
				CompoundNBT tag = info.writeToNBT();
				lubricatedList.add(tag);
			}
		}
		nbt.put("lubricated", lubricatedList);
		
		return nbt;
	}
	
	
	private static IPSaveData INSTANCE;
	
	public static void markInstanceAsDirty(){
		if(INSTANCE != null){
			INSTANCE.markDirty();
		}
	}
	
	public static void setInstance(IPSaveData in){
		INSTANCE = in;
	}
}
