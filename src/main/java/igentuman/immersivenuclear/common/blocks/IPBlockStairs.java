package igentuman.immersivenuclear.common.blocks;

import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.common.IPContent;
import net.minecraft.block.StairsBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class IPBlockStairs<B extends IPBlockBase> extends StairsBlock{
	public IPBlockStairs(B base){
		super(base::getDefaultState, Properties.from(base));
		setRegistryName(new ResourceLocation(ImmersiveNuclear.MODID, base.getRegistryName().getPath() + "_stairs"));
		
		IPContent.registeredIPBlocks.add(this);
		
		BlockItem bItem = createBlockItem();
		if(bItem != null){
			IPContent.registeredIPItems.add(bItem.setRegistryName(getRegistryName()));
		}
	}
	
	protected BlockItem createBlockItem(){
		return new IPBlockItemBase(this, new Item.Properties().group(ImmersiveNuclear.creativeTab));
	}
}
