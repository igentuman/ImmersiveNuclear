/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.client;

import blusunrize.immersiveengineering.api.client.TextUtils;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelper;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.client.BlockOverlayUtils;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.interfaces.MBOverlayText;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.common.config.IEClientConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import java.util.List;

public class ClientEventHandler implements ResourceManagerReloadListener
{
	private boolean shieldToggleButton = false;
	private int shieldToggleTimer = 0;

	@Override
	public void onResourceManagerReload(@Nonnull ResourceManager resourceManager)
	{
		ImmersiveNuclear.proxy.clearRenderCaches();
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event)
	{
		if(event.side==LogicalSide.CLIENT&&event.player!=null&&event.player==ClientUtils.mc().getCameraEntity())
		{
			if(event.phase==Phase.END)
			{

			}
		}
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event)
	{

	}

	@SubscribeEvent
	public void onItemTooltip(ItemTooltipEvent event)
	{

		Level clientLevel = ClientUtils.mc().level;

		if(IEClientConfig.tagTooltips.get()&&event.getFlags().isAdvanced())
			event.getItemStack().getItem().builtInRegistryHolder().tags()
					.map(TagKey::location)
					.forEach(oid ->
							event.getToolTip().add(TextUtils.applyFormat(
									Component.literal(oid.toString()),
									ChatFormatting.GRAY
							)));
	}

	@SubscribeEvent
	public void onRenderItemFrame(RenderItemInFrameEvent event)
	{

	}


	@SubscribeEvent()
	public void onRenderOverlayPost(RenderGuiOverlayEvent.Post event)
	{

	}

	private <S extends IMultiblockState> boolean renderMultiblockOverlay(
			IMultiblockBE<S> be, boolean hammer, PoseStack transform, int scaledWidth, int scaledHeight
	)
	{
		final IMultiblockBEHelper<S> helper = be.getHelper();
		if(!(helper.getMultiblock().logic() instanceof MBOverlayText<S> overlayHandler))
			return false;
		final List<Component> overlayText = overlayHandler.getOverlayText(
				helper.getState(), ClientUtils.mc().player, hammer
		);
		if(overlayText==null)
			return false;
		BlockOverlayUtils.drawBlockOverlayText(transform, overlayText, scaledWidth, scaledHeight);
		return true;
	}


	@SubscribeEvent()
	public void renderAdditionalBlockBounds(RenderHighlightEvent.Block event)
	{
		if(event.getTarget().getType()==Type.BLOCK&&event.getCamera().getEntity() instanceof LivingEntity living)
		{
			PoseStack transform = event.getPoseStack();
			MultiBufferSource buffer = event.getMultiBufferSource();
			BlockHitResult rtr = event.getTarget();
			BlockPos pos = rtr.getBlockPos();
			Vec3 renderView = event.getCamera().getPosition();
			transform.pushPose();
			transform.translate(-renderView.x, -renderView.y, -renderView.z);
			transform.translate(pos.getX(), pos.getY(), pos.getZ());
			float eps = 0.002F;
			BlockEntity tile = living.level().getBlockEntity(rtr.getBlockPos());
			ItemStack stack = living.getItemInHand(InteractionHand.MAIN_HAND);

		}
	}

	@SubscribeEvent()
	public void onRenderLivingPre(RenderLivingEvent.Pre event)
	{
		if(event.getEntity().getPersistentData().contains("headshot"))
			enableHead(event.getRenderer(), false);
	}

	@SubscribeEvent()
	public void onRenderLivingPost(RenderLivingEvent.Post event)
	{
		if(event.getEntity().getPersistentData().contains("headshot"))
			enableHead(event.getRenderer(), true);
	}

	private static void enableHead(LivingEntityRenderer renderer, boolean shouldEnable)
	{
		EntityModel m = renderer.getModel();
		if(m instanceof HeadedModel)
			((HeadedModel)m).getHead().visible = shouldEnable;
	}

	@SubscribeEvent
	public void onEntityJoiningWorld(EntityJoinLevelEvent event)
	{

	}
}
