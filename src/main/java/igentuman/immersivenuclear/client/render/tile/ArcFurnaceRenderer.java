/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.client.render.tile;

import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.client.render.tile.DynamicModel;
import blusunrize.immersiveengineering.client.render.tile.IEBlockEntityRenderer;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.arcfurnace.ArcFurnaceLogic.State;
import com.mojang.blaze3d.vertex.PoseStack;
import igentuman.immersivenuclear.ImmersiveNuclear;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public class ArcFurnaceRenderer extends IEBlockEntityRenderer<MultiblockBlockEntityMaster<State>>
{
	private TextureAtlasSprite hotMetal_flow = null;
	private TextureAtlasSprite hotMetal_still = null;

	public static final String NAME = "arc_furnace_electrodes";
	public static DynamicModel ELECTRODES;
	public static final ResourceLocation HOT_METLA_STILL = new ResourceLocation(ImmersiveNuclear.MODID, "block/fluid/hot_metal_still");
	public static final ResourceLocation HOT_METLA_FLOW = new ResourceLocation(ImmersiveNuclear.MODID, "block/fluid/hot_metal_flow");

	@Override
	public void render(MultiblockBlockEntityMaster<State> te, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn,
					   int combinedLightIn, int combinedOverlayIn)
	{

	}
}