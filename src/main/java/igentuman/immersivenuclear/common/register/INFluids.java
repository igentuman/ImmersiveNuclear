/*
 *  BluSunrize
 *  Copyright (c) 2021
 *
 *  This code is licensed under "Blu's License of Common Sense"
 *  Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.common.register;

import blusunrize.immersiveengineering.common.register.IEBlocks.BlockEntry;
import igentuman.immersivenuclear.api.Lib;
import igentuman.immersivenuclear.common.fluids.IEFluid;
import igentuman.immersivenuclear.common.fluids.IEFluidBlock;
import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static igentuman.immersivenuclear.ImmersiveNuclear.rl;

public class INFluids
{
	public static final DeferredRegister<Fluid> REGISTER = DeferredRegister.create(ForgeRegistries.FLUIDS, Lib.MODID);
	public static final DeferredRegister<FluidType> TYPE_REGISTER = DeferredRegister.create(
			ForgeRegistries.Keys.FLUID_TYPES, Lib.MODID
	);
	public static final List<FluidEntry> ALL_ENTRIES = new ArrayList<>();
	public static final Set<BlockEntry<? extends LiquidBlock>> ALL_FLUID_BLOCKS = new HashSet<>();

	public static final FluidEntry STEAM = FluidEntry.make(
			"steam", 800, rl("block/fluid/steam_still"), rl("block/fluid/steam_flow")
	);


	public record FluidEntry(
			RegistryObject<IEFluid> flowing,
			RegistryObject<IEFluid> still,
			BlockEntry<IEFluidBlock> block,
			RegistryObject<BucketItem> bucket,
			RegistryObject<FluidType> type,
			List<Property<?>> properties
	)
	{
		private static FluidEntry make(String name, ResourceLocation stillTex, ResourceLocation flowingTex)
		{
			return make(name, 0, stillTex, flowingTex);
		}

		private static FluidEntry make(
				String name, ResourceLocation stillTex, ResourceLocation flowingTex, Consumer<FluidType.Properties> buildAttributes
		)
		{
			return make(name, 0, stillTex, flowingTex, buildAttributes);
		}

		private static FluidEntry make(String name, int burnTime, ResourceLocation stillTex, ResourceLocation flowingTex)
		{
			return make(name, burnTime, stillTex, flowingTex, null);
		}

		private static FluidEntry make(
				String name, int burnTime,
				ResourceLocation stillTex, ResourceLocation flowingTex,
				@Nullable Consumer<FluidType.Properties> buildAttributes
		)
		{
			return make(
					name, burnTime, stillTex, flowingTex, IEFluid::new, IEFluid.Flowing::new, buildAttributes,
					ImmutableList.of()
			);
		}

		private static FluidEntry make(
				String name, ResourceLocation stillTex, ResourceLocation flowingTex,
				Function<FluidEntry, ? extends IEFluid> makeStill, Function<FluidEntry, ? extends IEFluid> makeFlowing,
				@Nullable Consumer<FluidType.Properties> buildAttributes, ImmutableList<Property<?>> properties
		)
		{
			return make(name, 0, stillTex, flowingTex, makeStill, makeFlowing, buildAttributes, properties);
		}

		private static FluidEntry make(
				String name, int burnTime,
				ResourceLocation stillTex, ResourceLocation flowingTex,
				Function<FluidEntry, ? extends IEFluid> makeStill, Function<FluidEntry, ? extends IEFluid> makeFlowing,
				@Nullable Consumer<FluidType.Properties> buildAttributes, List<Property<?>> properties)
		{
			FluidType.Properties builder = FluidType.Properties.create()
					.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
					.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY);
			if(buildAttributes!=null)
				buildAttributes.accept(builder);
			RegistryObject<FluidType> type = TYPE_REGISTER.register(
					name, () -> makeTypeWithTextures(builder, stillTex, flowingTex)
			);
			Mutable<FluidEntry> thisMutable = new MutableObject<>();
			RegistryObject<IEFluid> still = REGISTER.register(name, () -> IEFluid.makeFluid(
					makeStill, thisMutable.getValue()
			));
			RegistryObject<IEFluid> flowing = REGISTER.register(name+"_flowing", () -> IEFluid.makeFluid(
					makeFlowing, thisMutable.getValue()
			));
			BlockEntry<IEFluidBlock> block = new BlockEntry<>(
					name+"_fluid_block",
					() -> Properties.copy(Blocks.WATER),
					p -> new IEFluidBlock(thisMutable.getValue(), p)
			);
			RegistryObject<BucketItem> bucket = INItems.REGISTER.register(name+"_bucket", () -> makeBucket(still, burnTime));
			FluidEntry entry = new FluidEntry(flowing, still, block, bucket, type, properties);
			thisMutable.setValue(entry);
			ALL_FLUID_BLOCKS.add(block);
			ALL_ENTRIES.add(entry);
			return entry;
		}

		private static FluidType makeTypeWithTextures(
				FluidType.Properties builder, ResourceLocation stillTex, ResourceLocation flowingTex
		)
		{
			return new FluidType(builder)
			{
				@Override
				public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
				{
					consumer.accept(new IClientFluidTypeExtensions()
					{
						@Override
						public ResourceLocation getStillTexture()
						{
							return stillTex;
						}

						@Override
						public ResourceLocation getFlowingTexture()
						{
							return flowingTex;
						}
					});
				}
			};
		}

		public IEFluid getFlowing()
		{
			return flowing.get();
		}

		public IEFluid getStill()
		{
			return still.get();
		}

		public IEFluidBlock getBlock()
		{
			return block.get();
		}

		public BucketItem getBucket()
		{
			return bucket.get();
		}

		private static BucketItem makeBucket(RegistryObject<IEFluid> still, int burnTime)
		{
			return new BucketItem(
					still, new Item.Properties()
					.stacksTo(1)
					.craftRemainder(Items.BUCKET))
			{
				@Override
				public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
				{
					return new FluidBucketWrapper(stack);
				}

				@Override
				public int getBurnTime(ItemStack itemStack, RecipeType<?> type)
				{
					return burnTime;
				}
			};
		}

		public RegistryObject<IEFluid> getStillGetter()
		{
			return still;
		}
	}
}
