/*
 * BluSunrize
 * Copyright (c) 2020
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.data;

import blusunrize.immersiveengineering.api.EnumMetals;
import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockAdvancementTrigger;
import blusunrize.immersiveengineering.api.tool.BulletHandler;
import blusunrize.immersiveengineering.api.wires.WireType;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IEMultiblocks;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import blusunrize.immersiveengineering.common.register.IEItems;
import igentuman.immersivenuclear.common.register.INBlocks;
import igentuman.immersivenuclear.common.register.INBlocks.MetalDevices;
import igentuman.immersivenuclear.common.register.INEntityTypes;
import igentuman.immersivenuclear.common.register.INFluids;
import igentuman.immersivenuclear.common.register.INItems.*;
import igentuman.immersivenuclear.common.register.INPotions;
import igentuman.immersivenuclear.common.world.Villages;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger.TriggerInstance;
import net.minecraft.commands.CommandFunction;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Advancements extends ForgeAdvancementProvider
{
	public Advancements(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper exFileHelper)
	{
		super(output, provider, exFileHelper, List.of(Advancements::registerAdvancements));
	}

	private static void registerAdvancements(
			HolderLookup.Provider lookup, Consumer<Advancement> consumer, ExistingFileHelper existingFileHelper
	)
	{
		/* MAIN */
		AdvBuilder.setPage("main");

	}

	private static Path createPath(Path pathIn, Advancement advancementIn)
	{
		return pathIn.resolve("data/"+advancementIn.getId().getNamespace()+"/advancements/"+advancementIn.getId().getPath()+".json");
	}

	static class AdvBuilder
	{
		public static String page = null;

		// recipeAdvancement is badly named, it just means that telemetry is disabled for this advancement. Since
		// telemetry only applies to vanilla events (minecraft namespace) it does not matter much.
		private final Advancement.Builder builder = Advancement.Builder.recipeAdvancement();

		private final String name;
		private ItemStack icon;
		private ResourceLocation background = null;
		private FrameType frame = FrameType.TASK;
		private boolean hidden = false;

		private boolean quiet = false;

		private AdvBuilder(String name)
		{
			assert page!=null;
			this.name = name;
		}

		public static void setPage(String page)
		{
			AdvBuilder.page = page;
		}

		public static AdvBuilder root(String bg)
		{
			return new AdvBuilder(page+"_root").background(new ResourceLocation(Lib.MODID, "textures/"+bg+".png"));
		}

		public static AdvBuilder child(String name, Advancement parent)
		{
			return new AdvBuilder(name).parent(parent);
		}

		public AdvBuilder getItem(ItemLike item)
		{
			return this.icon(item).hasItems(item);
		}

		public AdvBuilder placeBlock(IEBlocks.BlockEntry<?> block)
		{
			return this.icon(block).addCriterion("place_block", TriggerInstance.placedBlock(block.get()));
		}

		public AdvBuilder multiblock(IETemplateMultiblock multiblock)
		{
			return this.icon(multiblock.getBlock()).addCriterion("form_multiblock", MultiblockAdvancementTrigger.create(
					multiblock.getUniqueName(),
					ItemPredicate.Builder.item().of(IEItems.Tools.HAMMER).build())
			);
		}

		private AdvBuilder parent(Advancement parent)
		{
			this.builder.parent(parent);
			return this;
		}

		private AdvBuilder background(ResourceLocation background)
		{
			this.background = background;
			return this;
		}


		public AdvBuilder icon(ItemStack icon)
		{
			this.icon = icon;
			return this;
		}

		public AdvBuilder icon(ItemLike icon)
		{
			return this.icon(new ItemStack(icon));
		}

		public AdvBuilder goal()
		{
			this.frame = FrameType.GOAL;
			return this;
		}

		public AdvBuilder challenge()
		{
			this.frame = FrameType.CHALLENGE;
			return this;
		}

		public AdvBuilder hidden()
		{
			this.hidden = true;
			return this;
		}

		public AdvBuilder quiet()
		{
			this.quiet = true;
			return this;
		}

		public AdvBuilder loot(String lootPath)
		{
			this.builder.rewards(new AdvancementRewards(
					0,
					new ResourceLocation[]{new ResourceLocation(Lib.MODID, "advancements/"+lootPath)},
					new ResourceLocation[0],
					CommandFunction.CacheableFunction.NONE)
			);
			return this;
		}

		public AdvBuilder addCriterion(String key, CriterionTriggerInstance criterion)
		{
			this.builder.addCriterion(key, criterion);
			return this;
		}

		public AdvBuilder orRequirements()
		{
			this.builder.requirements(RequirementsStrategy.OR);
			return this;
		}

		public AdvBuilder hasItems(ItemLike... items)
		{
			return this.addCriterion("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(
					ItemPredicate.Builder.item().of(items).build())
			);
		}

		public AdvBuilder placeBlocks(Collection<? extends IEBlocks.BlockEntry<?>> blocks)
		{
			blocks.stream().sorted(Comparator.comparing(IEBlocks.BlockEntry::getId))
					.forEachOrdered(block -> addCriterion(block.getId().getPath(), TriggerInstance.placedBlock(block.get())));
			return this;
		}

		public AdvBuilder talkToVillagers(ResourceLocation... professions)
		{
			Arrays.stream(professions).sorted(Comparator.comparing(ResourceLocation::getPath))
					.forEachOrdered(prof -> {
						CompoundTag villagerData = new CompoundTag();
						villagerData.putString("profession", prof.toString());
						CompoundTag entityNBT = new CompoundTag();
						entityNBT.put("VillagerData", villagerData);
						addCriterion("meet_"+prof.getPath(), PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
								ContextAwarePredicate.ANY,
								ItemPredicate.Builder.item(),
								EntityPredicate.wrap(EntityPredicate.Builder.entity().of(EntityType.VILLAGER).nbt(new NbtPredicate(entityNBT)).build())
						));
					});
			return this;
		}

		public AdvBuilder codeTriggered()
		{
			return this.addCriterion("code_trigger", new ImpossibleTrigger.TriggerInstance());
		}

		private Advancement.Builder withDisplay()
		{
			return this.builder.display(new DisplayInfo(
					this.icon,
					Component.translatable("advancement.immersiveengineering."+this.name),
					Component.translatable("advancement.immersiveengineering."+this.name+".desc"),
					this.background,
					this.frame,
					!this.quiet,
					!this.quiet,
					this.hidden
			));
		}

		public Advancement save(Consumer<Advancement> consumer)
		{
			return this.withDisplay().save(consumer, Lib.MODID+":"+AdvBuilder.page+"/"+this.name);
		}

	}
}