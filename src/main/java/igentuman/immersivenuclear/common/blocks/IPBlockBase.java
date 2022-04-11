package igentuman.immersivenuclear.common.blocks;

import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.common.IPContent;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class IPBlockBase extends Block{
	public IPBlockBase(String name, Block.Properties props){
		super(props);
		setRegistryName(new ResourceLocation(ImmersiveNuclear.MODID, name));
		
		IPContent.registeredIPBlocks.add(this);
		
		BlockItem bItem = createBlockItem();
		if(bItem != null)
			IPContent.registeredIPItems.add(bItem.setRegistryName(getRegistryName()));
	}
	
	protected BlockItem createBlockItem(){
		return new IPBlockItemBase(this, new Item.Properties().group(ImmersiveNuclear.creativeTab));
	}
}
