package igentuman.immersivenuclear.common;

import java.util.Map;

import blusunrize.immersiveengineering.api.DimensionChunkCoords;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.storage.WorldSavedData;

public class IPSaveData extends WorldSavedData{
	public static final String dataName = "ImmersiveNuclear-SaveData";
	
	public IPSaveData(){
		super(dataName);
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

	@Override
	public void read(CompoundNBT nbt) {

	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		return null;
	}
}
