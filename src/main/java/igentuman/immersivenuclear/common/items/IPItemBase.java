package igentuman.immersivenuclear.common.items;

import blusunrize.immersiveengineering.common.items.IEItemInterfaces.IColouredItem;
import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.common.IPContent;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class IPItemBase extends Item implements IColouredItem{
	/** For basic items */
	public IPItemBase(String name){
		this(name, new Item.Properties());
	}
	
	/** For items that require special attention */
	public IPItemBase(String name, Item.Properties properties){
		super(properties.group(ImmersiveNuclear.creativeTab));
		setRegistryName(new ResourceLocation(ImmersiveNuclear.MODID, name));
		
		IPContent.registeredIPItems.add(this);
	}
}
