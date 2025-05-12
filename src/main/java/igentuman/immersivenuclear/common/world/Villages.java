/*
 * BluSunrize
 * Copyright (c) 2019
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.common.world;

import blusunrize.immersiveengineering.api.IEApi;
import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.common.register.IEBlocks.WoodenDevices;
import blusunrize.immersiveengineering.mixin.accessors.TemplatePoolAccess;
import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.api.*;
import igentuman.immersivenuclear.common.register.INBannerPatterns;
import igentuman.immersivenuclear.common.register.INBlocks.*;
import igentuman.immersivenuclear.common.register.INItems;
import igentuman.immersivenuclear.common.util.IESounds;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.*;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection;
import net.minecraft.world.level.saveddata.maps.MapDecoration.Type;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.TagsUpdatedEvent.UpdateCause;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static blusunrize.immersiveengineering.common.register.IEBlocks.WoodenDevices.*;
import static igentuman.immersivenuclear.ImmersiveNuclear.MODID;
import static igentuman.immersivenuclear.ImmersiveNuclear.rl;

@EventBusSubscriber(modid = Lib.MODID, bus = Bus.FORGE)
public class Villages
{
	public static final ResourceLocation ENGINEER = rl("engineer");
	public static final ResourceLocation MACHINIST = rl("machinist");
	public static final ResourceLocation ELECTRICIAN = rl("electrician");
	public static final ResourceLocation OUTFITTER = rl("outfitter");
	public static final ResourceLocation GUNSMITH = rl("gunsmith");

	@SubscribeEvent
	public static void onTagsUpdated(TagsUpdatedEvent ev)
	{
		if(ev.getUpdateCause()!=UpdateCause.SERVER_DATA_LOAD)
			return;
		// Register engineer's houses for each biome
		for(String biome : new String[]{"plains", "snowy", "savanna", "desert", "taiga"})
			for(String type : new String[]{"engineer", "machinist", "electrician", "gunsmith", "outfitter"})
				addToPool(
						new ResourceLocation("village/"+biome+"/houses"),
						rl("village/houses/"+biome+"_"+type),
						ev.getRegistryAccess()
				);
	}

	public static void init()
	{
	}

	private static void addToPool(ResourceLocation poolId, ResourceLocation toAdd, RegistryAccess regAccess)
	{
		Registry<StructureTemplatePool> registry = regAccess.registryOrThrow(Registries.TEMPLATE_POOL);
		StructureTemplatePool pool = Objects.requireNonNull(registry.get(poolId), poolId.getPath());
		TemplatePoolAccess poolAccess = (TemplatePoolAccess)pool;
		if(!(poolAccess.getRawTemplates() instanceof ArrayList))
			poolAccess.setRawTemplates(new ArrayList<>(poolAccess.getRawTemplates()));

		SinglePoolElement addedElement = SinglePoolElement.single(toAdd.toString()).apply(Projection.RIGID);
		poolAccess.getRawTemplates().add(Pair.of(addedElement, 1));
		poolAccess.getTemplates().add(addedElement);
	}

	@Mod.EventBusSubscriber(modid = MODID, bus = Bus.MOD)
	public static class Registers
	{
		public static final DeferredRegister<PoiType> POINTS_OF_INTEREST = DeferredRegister.create(ForgeRegistries.POI_TYPES, ImmersiveNuclear.MODID);
		public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(
				ForgeRegistries.VILLAGER_PROFESSIONS, ImmersiveNuclear.MODID
		);

		/*public static final RegistryObject<PoiType> POI_CIRCUITTABLE = POINTS_OF_INTEREST.register(
				"circuit_table", () -> createPOI(assembleStates(CIRCUIT_TABLE.get()))
		);*/



/*		public static final RegistryObject<VillagerProfession> PROF_ELECTRICIAN = PROFESSIONS.register(
				ELECTRICIAN.getPath(), () -> createProf(ELECTRICIAN, POI_CIRCUITTABLE, IESounds.spark.get())
		);*/


		private static PoiType createPOI(Collection<BlockState> block)
		{
			return new PoiType(ImmutableSet.copyOf(block), 1, 1);
		}

		private static VillagerProfession createProf(
				ResourceLocation name, RegistryObject<PoiType> poi, SoundEvent sound
		)
		{
			ResourceKey<PoiType> poiName = Objects.requireNonNull(poi.getKey());
			return new VillagerProfession(
					name.toString(),
					holder -> holder.is(poiName),
					holder -> holder.is(poiName),
					ImmutableSet.of(),
					ImmutableSet.of(),
					sound
			);
		}

		private static Collection<BlockState> assembleStates(Block block)
		{
			return block.getStateDefinition().getPossibleStates().stream().filter(blockState -> {
				if(blockState.hasProperty(IEProperties.MULTIBLOCKSLAVE))
					return !blockState.getValue(IEProperties.MULTIBLOCKSLAVE);
				return true;
			}).collect(Collectors.toList());
		}
	}

	@Mod.EventBusSubscriber(modid = MODID, bus = Bus.FORGE)
	public static class Events
	{
		@SubscribeEvent
		public static void registerTrades(VillagerTradesEvent ev)
		{
			Int2ObjectMap<List<ItemListing>> trades = ev.getTrades();
			final ResourceLocation typeName = new ResourceLocation(ev.getType().name());

		}
	}

	private static class TradeListing implements ItemListing
	{
		private final TradeOutline outline;
		private final LazyItemStack lazyItem;
		private final PriceInterval priceInfo;
		private final int maxUses;
		private final int xp;
		private float priceMultiplier = 0.05f;

		public TradeListing(@Nonnull TradeOutline outline, @Nonnull Function<Level, ItemStack> item, @Nonnull PriceInterval priceInfo, int maxUses, int xp)
		{
			this.outline = outline;
			this.lazyItem = new LazyItemStack(item);
			this.priceInfo = priceInfo;
			this.maxUses = maxUses;
			this.xp = xp;
		}

		public TradeListing(@Nonnull TradeOutline outline, @Nonnull ItemStack itemStack, @Nonnull PriceInterval buyAmounts, int maxUses, int xp)
		{
			this(outline, l -> itemStack, buyAmounts, maxUses, xp);
		}

		public TradeListing(@Nonnull TradeOutline outline, @Nonnull ItemLike item, @Nonnull PriceInterval buyAmounts, int maxUses, int xp)
		{
			this(outline, new ItemStack(item), buyAmounts, maxUses, xp);
		}

		public TradeListing(@Nonnull TradeOutline outline, @Nonnull TagKey<Item> tag, @Nonnull PriceInterval buyAmounts, int maxUses, int xp)
		{
			this(outline, l -> l!=null?IEApi.getPreferredTagStack(l.registryAccess(), tag): ItemStack.EMPTY, buyAmounts, maxUses, xp);
		}

		public TradeListing setMultiplier(float priceMultiplier)
		{
			this.priceMultiplier = priceMultiplier;
			return this;
		}

		@Nullable
		@Override
		public MerchantOffer getOffer(@Nullable Entity trader, @Nonnull RandomSource rand)
		{
			ItemStack buying = this.lazyItem.apply(trader!=null?trader.level(): null);
			return this.outline.generateOffer(buying, priceInfo, rand, maxUses, xp, priceMultiplier);
		}
	}


	/**
	 * Lazy-loaded ItemStack to support tag-based trades
	 */
	private static class LazyItemStack implements Function<Level, ItemStack>
	{
		private final Function<Level, ItemStack> function;
		private ItemStack instance;

		private LazyItemStack(Function<Level, ItemStack> function)
		{
			this.function = function;
		}

		@Override
		public ItemStack apply(Level level)
		{
			if(instance==null)
				instance = function.apply(level);
			return instance;
		}
	}

	/**
	 * Functional interface to create constant implementations from
	 */
	@FunctionalInterface
	private interface TradeOutline
	{
		MerchantOffer generateOffer(ItemStack item, PriceInterval priceInfo, RandomSource random, int maxUses, int xp, float priceMultiplier);
	}

	private static final TradeOutline EMERALD_FOR_ITEM = (buying, priceInfo, random, maxUses, xp, priceMultiplier) -> new MerchantOffer(
			ItemHandlerHelper.copyStackWithSize(buying, priceInfo.getPrice(random)),
			new ItemStack(Items.EMERALD),
			maxUses, xp, priceMultiplier
	);
	private static final TradeOutline ONE_ITEM_FOR_EMERALDS = (selling, priceInfo, random, maxUses, xp, priceMultiplier) -> new MerchantOffer(
			new ItemStack(Items.EMERALD, priceInfo.getPrice(random)),
			selling,
			maxUses, xp, priceMultiplier
	);
	private static final TradeOutline ITEMS_FOR_ONE_EMERALD = (selling, priceInfo, random, maxUses, xp, priceMultiplier) -> new MerchantOffer(
			new ItemStack(Items.EMERALD),
			ItemHandlerHelper.copyStackWithSize(selling, priceInfo.getPrice(random)),
			maxUses, xp, priceMultiplier
	);

	private record PriceInterval(int min, int max)
	{
		int getPrice(RandomSource rand)
		{
			return min >= max?min: min+rand.nextInt(max-min+1);
		}
	}
}
