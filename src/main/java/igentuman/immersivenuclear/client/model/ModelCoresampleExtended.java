package igentuman.immersivenuclear.client.model;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;

import com.google.common.cache.Cache;

import blusunrize.immersiveengineering.api.excavator.MineralMix;
import blusunrize.immersiveengineering.client.models.ModelCoresample;
import blusunrize.immersiveengineering.common.items.CoresampleItem;
import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.IModelData;

// TODO Get the Coresample to display shit
@SuppressWarnings("unused")
public class ModelCoresampleExtended extends ModelCoresample{
	private Fluid fluid;
	
	public ModelCoresampleExtended(@Nullable MineralMix[] mineral, VertexFormat format, @Nullable Fluid fluid){
		super(mineral, format);
		this.fluid = fluid;
	}
	
	@Override
	public List<BakedQuad> getQuads(BlockState coreState, Direction side, Random rand, IModelData extraData){
		List<BakedQuad> bakedQuads=super.getQuads(coreState, side, rand, extraData);
		return bakedQuads;
	}
	
	protected final void putVertexDataSpr(List<BakedQuad> bakedQuads, Vector3f normal, Vector3f[] vertices, Vector2f[] uvs, TextureAtlasSprite sprite)
	{
		/*
		UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(DefaultVertexFormats.ITEM);
		builder.setQuadOrientation(Direction.getFacingFromVector(normal.getX(), normal.getY(), normal.getZ()));
		builder.setTexture(sprite);
//		builder.setQuadColored();
		for (int i = 0; i < vertices.length; i++)
		{
			builder.put(0, vertices[i].getX(), vertices[i].getY(), vertices[i].getZ(), 1);//Pos
			float d = LightUtil.diffuseLight(normal.getX(), normal.getY(), normal.getZ());
			builder.put(1, d, d, d, 1);//Colour
			builder.put(2, uvs[i].getX(), uvs[i].getY(), 0, 1);//UV
			builder.put(3, normal.getX(), normal.getY(), normal.getZ(), 0);//Normal
			builder.put(4);//padding
		}
		bakedQuads.add(builder.build());
		*/
	}
	
	@Override
	public ItemOverrideList getOverrides(){
		return overrideList2;
	}
	
	ItemOverrideList overrideList2 = new ItemOverrideList(){
		
		@Override
		public IBakedModel getOverrideModel(IBakedModel originalModel, ItemStack stack, ClientWorld worldIn, LivingEntity entityIn){
			String resName = ItemNBTHelper.hasKey(stack, "resType") ? ItemNBTHelper.getString(stack, "resType") : null;
			if(ItemNBTHelper.hasKey(stack, "resAmount") && resName == null && ItemNBTHelper.getInt(stack, "resAmount") > 0){
				resName = "resAmount";
			}
			
			MineralMix[] minerals = CoresampleItem.getMineralMixes(stack);
			if(minerals.length > 0){
				try{
					String cacheKey = "";
					for(int i = 0;i < minerals.length;i++){
						cacheKey += (i > 0 ? "_" : "") + minerals[i].getId().toString();
					}
					
					return getSampleCache().get(cacheKey, () -> {
						VertexFormat format;
						if(originalModel instanceof ModelCoresample){
							format = getVertexFormat((ModelCoresample) originalModel);
						}else{
							format = DefaultVertexFormats.BLOCK;
						}
						return new ModelCoresampleExtended(minerals, format, null);
					});
				}catch(ExecutionException e){
					throw new RuntimeException(e);
				}
			}
			
			return originalModel;
		}
	};
	
	/*
	@Override
	public List<BakedQuad> getQuads(BlockState coreState, Direction side, Random rand, IModelData extraData){
		if(bakedQuads == null){
			try{
				bakedQuads = Collections.synchronizedSet(new LinkedHashSet<BakedQuad>());
				float width = .25f;
				float depth = .25f;
				float wOff = (1 - width) / 2;
				float dOff = (1 - depth) / 2;
				
				float fWidth = .24f;
				float fDepth = .24f;
				float fWOff = (1 - fWidth) / 2;
				float fDOff = (1 - fDepth) / 2;
				
				int pixelLength = 0;
				
				HashMap<TextureAtlasSprite, Integer> textureOre = new HashMap<>();
				if(mineral != null && mineral.outputs != null){
					for(int i = 0;i < mineral.outputs.length;i++){
						if(mineral.outputs[i]!=null){
							int weight = Math.max(2, Math.round(16 * mineral.failChance));
							Block b = Block.getBlockFromItem(mineral.outputs[i].getStack().getItem());
							BlockState state = b != null ? b.getDefaultState() : Blocks.STONE.getDefaultState();
							
							IForgeBakedModel model =  (IForgeBakedModel)Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getModel(state);
							if(b == flaxbeard.immersivenuclear.common.IPContent.Blocks.dummyBlockOilOre){
								textureOre.put(null, weight);
							}else if(model != null && model.getParticleTexture(null) != null){
								textureOre.put(model.getParticleTexture(null), weight);
							}
							pixelLength += weight;
						}
					}
				}else
					pixelLength = 16;
				TextureAtlasSprite textureStone = ClientUtils.getSprite(new ResourceLocation("blocks/stone"));
				
				Vector2f[] stoneUVs = {new Vector2f(textureStone.getInterpolatedU(16 * wOff), textureStone.getInterpolatedV(16 * dOff)), new Vector2f(textureStone.getInterpolatedU(16 * wOff), textureStone.getInterpolatedV(16 * (dOff + depth))), new Vector2f(textureStone.getInterpolatedU(16 * (wOff + width)), textureStone.getInterpolatedV(16 * (dOff + depth))), new Vector2f(textureStone.getInterpolatedU(16 * (wOff + width)), textureStone.getInterpolatedV(16 * dOff))};
				
				putVertexDataSpr(new Vector3f(0, -1, 0), new Vector3f[]{new Vector3f(wOff, 0, dOff), new Vector3f(wOff + width, 0, dOff), new Vector3f(wOff + width, 0, dOff + depth), new Vector3f(wOff, 0, dOff + depth)}, stoneUVs, textureStone);
				putVertexDataSpr(new Vector3f(0, 1, 0), new Vector3f[]{new Vector3f(wOff, 1, dOff), new Vector3f(wOff, 1, dOff + depth), new Vector3f(wOff + width, 1, dOff + depth), new Vector3f(wOff + width, 1, dOff)}, stoneUVs, textureStone);
				if(textureOre.isEmpty()){
					Vector2f[][] uvs = new Vector2f[4][];
					for(int j = 0;j < 4;j++){
						uvs[j] = new Vector2f[]{new Vector2f(textureStone.getInterpolatedU(j * 4), textureStone.getInterpolatedV(0)), new Vector2f(textureStone.getInterpolatedU(j * 4), textureStone.getInterpolatedV(16)), new Vector2f(textureStone.getInterpolatedU((j + 1) * 4), textureStone.getInterpolatedV(16)), new Vector2f(textureStone.getInterpolatedU((j + 1) * 4), textureStone.getInterpolatedV(0))};
					}
					
					putVertexDataSpr(new Vector3f(0, 0, -1), new Vector3f[]{new Vector3f(wOff, 0, dOff), new Vector3f(wOff, 1, dOff), new Vector3f(wOff + width, 1, dOff), new Vector3f(wOff + width, 0, dOff)}, uvs[0], textureStone);
					putVertexDataSpr(new Vector3f(0, 0, 1), new Vector3f[]{new Vector3f(wOff + width, 0, dOff + depth), new Vector3f(wOff + width, 1, dOff + depth), new Vector3f(wOff, 1, dOff + depth), new Vector3f(wOff, 0, dOff + depth)}, uvs[2], textureStone);
					putVertexDataSpr(new Vector3f(-1, 0, 0), new Vector3f[]{new Vector3f(wOff, 0, dOff + depth), new Vector3f(wOff, 1, dOff + depth), new Vector3f(wOff, 1, dOff), new Vector3f(wOff, 0, dOff)}, uvs[3], textureStone);
					putVertexDataSpr(new Vector3f(1, 0, 0), new Vector3f[]{new Vector3f(wOff + width, 0, dOff), new Vector3f(wOff + width, 1, dOff), new Vector3f(wOff + width, 1, dOff + depth), new Vector3f(wOff + width, 0, dOff + depth)}, uvs[1], textureStone);
				}else{
					float h = 0;
					for(TextureAtlasSprite sprite:textureOre.keySet()){
						int weight = textureOre.get(sprite);
						int v = weight > 8 ? 16 - weight : 8;
						
						if(sprite == null){
							TextureAtlasSprite fSprite = null;
							
							if(fluid != null){
								fSprite = Minecraft.getInstance().getTextureMap().getSprite(fluid.getRegistryName());
							}
							
							if(fSprite != null){
								Vector2f[][] uvs = new Vector2f[4][];
								for(int j = 0;j < 4;j++){
									uvs[j] = new Vector2f[]{new Vector2f(fSprite.getInterpolatedU(j * 4), fSprite.getInterpolatedV(v)), new Vector2f(fSprite.getInterpolatedU(j * 4), fSprite.getInterpolatedV(v + weight)), new Vector2f(fSprite.getInterpolatedU((j + 1) * 4), fSprite.getInterpolatedV(v + weight)), new Vector2f(fSprite.getInterpolatedU((j + 1) * 4), fSprite.getInterpolatedV(v))};
								}
								
								float h1 = weight / (float) pixelLength;
								putVertexDataSpr(new Vector3f(0, 0, -1), new Vector3f[]{new Vector3f(fWOff, h, fDOff), new Vector3f(fWOff, h + h1, fDOff), new Vector3f(fWOff + fWidth, h + h1, fDOff), new Vector3f(fWOff + fWidth, h, fDOff)}, uvs[0], fSprite);
								putVertexDataSpr(new Vector3f(0, 0, 1), new Vector3f[]{new Vector3f(fWOff + fWidth, h, fDOff + fDepth), new Vector3f(fWOff + fWidth, h + h1, fDOff + fDepth), new Vector3f(fWOff, h + h1, fDOff + fDepth), new Vector3f(fWOff, h, fDOff + fDepth)}, uvs[2], fSprite);
								putVertexDataSpr(new Vector3f(-1, 0, 0), new Vector3f[]{new Vector3f(fWOff, h, fDOff + fDepth), new Vector3f(fWOff, h + h1, fDOff + fDepth), new Vector3f(fWOff, h + h1, fDOff), new Vector3f(fWOff, h, fDOff)}, uvs[3], fSprite);
								putVertexDataSpr(new Vector3f(1, 0, 0), new Vector3f[]{new Vector3f(fWOff + fWidth, h, fDOff), new Vector3f(fWOff + fWidth, h + h1, fDOff), new Vector3f(fWOff + fWidth, h + h1, fDOff + fDepth), new Vector3f(fWOff + fWidth, h, fDOff + fDepth)}, uvs[1], fSprite);
							}
							
							// BlockTypes_Dummy.OIL_DEPOSIT
							BlockState state = flaxbeard.immersivenuclear.common.IPContent.Blocks.dummyBlockOilOre.getDefaultState();
							IForgeBakedModel model = (IForgeBakedModel)Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getModel(state);
							sprite = model.getParticleTexture(null);
							
						}
						Vector2f[][] uvs = new Vector2f[4][];
						for(int j = 0;j < 4;j++){
							uvs[j] = new Vector2f[]{new Vector2f(sprite.getInterpolatedU(j * 4), sprite.getInterpolatedV(v)), new Vector2f(sprite.getInterpolatedU(j * 4), sprite.getInterpolatedV(v + weight)), new Vector2f(sprite.getInterpolatedU((j + 1) * 4), sprite.getInterpolatedV(v + weight)), new Vector2f(sprite.getInterpolatedU((j + 1) * 4), sprite.getInterpolatedV(v))};
						}
						
						float h1 = weight / (float) pixelLength;
						putVertexDataSpr(new Vector3f(0, 0, -1), new Vector3f[]{new Vector3f(wOff, h, dOff), new Vector3f(wOff, h + h1, dOff), new Vector3f(wOff + width, h + h1, dOff), new Vector3f(wOff + width, h, dOff)}, uvs[0], sprite);
						putVertexDataSpr(new Vector3f(0, 0, 1), new Vector3f[]{new Vector3f(wOff + width, h, dOff + depth), new Vector3f(wOff + width, h + h1, dOff + depth), new Vector3f(wOff, h + h1, dOff + depth), new Vector3f(wOff, h, dOff + depth)}, uvs[2], sprite);
						putVertexDataSpr(new Vector3f(-1, 0, 0), new Vector3f[]{new Vector3f(wOff, h, dOff + depth), new Vector3f(wOff, h + h1, dOff + depth), new Vector3f(wOff, h + h1, dOff), new Vector3f(wOff, h, dOff)}, uvs[3], sprite);
						putVertexDataSpr(new Vector3f(1, 0, 0), new Vector3f[]{new Vector3f(wOff + width, h, dOff), new Vector3f(wOff + width, h + h1, dOff), new Vector3f(wOff + width, h + h1, dOff + depth), new Vector3f(wOff + width, h, dOff + depth)}, uvs[1], sprite);
						h += h1;
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if(bakedQuads != null && !bakedQuads.isEmpty()){
			List<BakedQuad> quadList = Collections.synchronizedList(Lists.newArrayList(bakedQuads));
			return quadList;
		}
		return emptyQuads;
	}//*/
	
	/*
	ItemOverrideList overrideList = new ItemOverrideList(){
		@Override
		public IBakedModel getModelWithOverrides(IBakedModel originalModel, ItemStack stack, World world, LivingEntity entity){
			String resName = ItemNBTHelper.hasKey(stack, "resType") ? ItemNBTHelper.getString(stack, "resType") : null;
			if(ItemNBTHelper.hasKey(stack, "resAmount") && resName == null && ItemNBTHelper.getInt(stack, "resAmount") > 0){
				resName = "resAmount";
			}
			
			if(ItemNBTHelper.hasKey(stack, "mineral")){
				String name = ItemNBTHelper.getString(stack, "mineral");
				String indexName = resName == null ? name : name + "_" + resName;
				if(name != null && !name.isEmpty()){
					if(!getSampleCache().asMap().containsKey(indexName)){
						outer: for(MineralVein mix:ExcavatorHandler.getMineralVeinList().values()){
							if(name.equals(mix.getPlainName())){
								if(resName != null){
									for(ReservoirType type:PumpjackHandler.reservoirs.values()){
										if(resName.equals(type.name)){
											List<StackWithChance> outputs = new ArrayList<>(Arrays.asList(mix.outputs));
											outputs.add(new StackWithChance(new ItemStack(flaxbeard.immersivenuclear.common.IPContent.Blocks.dummyBlockOilOre), 0.04F));
											StackWithChance[] outputNew = outputs.toArray(new StackWithChance[0]);
											DimensionType[] copy = mix.dimensions.toArray(new DimensionType[0]);
											MineralMix mix2 = new MineralMix(mix.getId(), outputNew, mix.weight, mix.failChance, copy, mix.background);
											getSampleCache().put(indexName, new ModelCoresampleExtended(mix2, DefaultVertexFormats.BLOCK, type.getFluid()));
											
//											mix.outputs.add(new ExcavatorHandler.OreOutput(IPContent.dummyBlockOilOre.getRegistryName(), 0.4F));
											
//											MineralMix mix2 = new MineralMix(mix.name, mix.failChance, newOres, newChances);
//											mix2.recalculateChances();
//											mix2.outputs.set(mix2.outputs.size() - 1, new ItemStack(IPContent.blockDummy, 1));
											
//											Fluid fluid = type.getFluid();
//											modelCache.put(indexName, new ModelCoresampleExtended(mix, fluid));
											break outer;
										}
									}
									
//									modelCache.put(indexName, new ModelCoresample(mix));
								}else{
//									modelCache.put(indexName, new ModelCoresample(mix));
								}
							}
						}
					}
					
					IBakedModel model = ExcavatorHandler.getMineralVeinList().get(indexName);
					if(model != null) return model;
				}
			}
			
			if(resName != null){
				if(!modelCache.containsKey("_" + resName)){
					for(ReservoirType type:PumpjackHandler.reservoirs.values()){
						if(resName.equals(type.name)){
//							MineralMix mix = new MineralMix(resName, 1, Arrays.asList(new ExcavatorHandler.OreOutput(IPContent.dummyBlockOilOre.getRegistryName(), 1F)));
//							mix.outputs.set(0, new ItemStack(IPContent.blockDummy, 1, EnumDummyType.OIL_DEPOSIT.getMeta()));
//							Fluid fluid = type.getFluid();
//							modelCache.put("_" + resName, new ModelCoresampleExtended(mix, null, fluid));
						}
					}
				}
				
				//IBakedModel model = modelCache.get("_" + resName);
				//if(model != null) return model;
			}
			
			return originalModel;
		}
	};//*/
	
	static Field STATIC_FIELD_modelCache;
	static Field FIELD_format;
	
	@SuppressWarnings("unchecked")
	static Cache<String, ModelCoresample> getSampleCache(){
		if(STATIC_FIELD_modelCache == null){
			try{
				STATIC_FIELD_modelCache = ModelCoresample.class.getDeclaredField("modelCache"); // Don't judge me alright? :c
				STATIC_FIELD_modelCache.setAccessible(true);
			}catch(Exception e){
				throw new RuntimeException(e);
			}
		}
		
		try{
			return (Cache<String, ModelCoresample>) STATIC_FIELD_modelCache.get(null);
		}catch(Exception e){
			throw new RuntimeException("This shouldnt happen! Report this immediately!", e);
		}
	}
	
	static VertexFormat getVertexFormat(ModelCoresample instance){
		if(FIELD_format == null){
			try{
				FIELD_format = ModelCoresample.class.getDeclaredField("format"); // Don't judge me alright? :c
				FIELD_format.setAccessible(true);
			}catch(Exception e){
				throw new RuntimeException(e);
			}
		}
		
		try{
			return (VertexFormat) FIELD_format.get(instance);
		}catch(Exception e){
			throw new RuntimeException("This shouldnt happen! Report this immediately!", e);
		}
	}
}
