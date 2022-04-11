package igentuman.immersivenuclear.api.crafting.pumpjack;

import net.minecraft.nbt.CompoundNBT;

public class ReservoirWorldInfo{
	public PumpjackHandler.ReservoirType type;
	public PumpjackHandler.ReservoirType overrideType;
	public int capacity;
	public int current;
	
	public PumpjackHandler.ReservoirType getType(){
		return (overrideType == null) ? type : overrideType;
	}
	
	public CompoundNBT writeToNBT(){
		CompoundNBT tag = new CompoundNBT();
		tag.putInt("capacity", capacity);
		tag.putInt("resAmount", current);
		if(type != null){
			tag.putString("type", type.name);
		}
		if(overrideType != null){
			tag.putString("overrideType", overrideType.name);
		}
		return tag;
	}
	
	public static ReservoirWorldInfo readFromNBT(CompoundNBT tag){
		ReservoirWorldInfo info = new ReservoirWorldInfo();
		info.capacity = tag.getInt("capacity");
		info.current = tag.getInt("resAmount");
		
		if(tag.contains("type")){
			String s = tag.getString("type");
			for(PumpjackHandler.ReservoirType res:PumpjackHandler.reservoirs.values()){
				if(s.equalsIgnoreCase(res.name))
					info.type = res;
			}
		}else if(info.current > 0){
			for(PumpjackHandler.ReservoirType res:PumpjackHandler.reservoirs.values()){
				if(res.name.equalsIgnoreCase("resAmount"))
					info.type = res;
			}
			
			if(info.type == null){
				return null;
			}
		}
		
		if(tag.contains("overrideType")){
			String s = tag.getString("overrideType");
			for(PumpjackHandler.ReservoirType res:PumpjackHandler.reservoirs.values()){
				if(s.equalsIgnoreCase(res.name))
					info.overrideType = res;
			}
		}
		
		return info;
	}
}
