/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.common;

import blusunrize.immersiveengineering.api.tool.assembler.AssemblerHandler;
import blusunrize.immersiveengineering.api.tool.assembler.FluidStackRecipeQuery;
import blusunrize.immersiveengineering.api.tool.assembler.FluidTagRecipeQuery;
import blusunrize.immersiveengineering.common.crafting.fluidaware.IngredientFluidStack;
import igentuman.immersivenuclear.api.crafting.IERecipeTypes;
import igentuman.immersivenuclear.common.register.*;
import igentuman.immersivenuclear.common.util.IESounds;
import igentuman.immersivenuclear.common.util.commands.IEArgumentTypes;
import igentuman.immersivenuclear.common.wires.INWireTypes;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static igentuman.immersivenuclear.ImmersiveNuclear.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Bus.MOD)
public class INContent
{
	private static CompletableFuture<?> lastOnThreadFuture;

	public static void modConstruction()
	{
		final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		INFluids.REGISTER.register(modBus);
		INFluids.TYPE_REGISTER.register(modBus);
		INPotions.REGISTER.register(modBus);
		INParticles.REGISTER.register(modBus);
		INBlockEntities.REGISTER.register(modBus);
		INEntityTypes.REGISTER.register(modBus);
		INMenuTypes.REGISTER.register(modBus);
		INCreativeTabs.REGISTER.register(modBus);
		INEntityDataSerializers.REGISTER.register(modBus);
		INStats.modConstruction();
		INItems.init();
		IESounds.init();
		INBlocks.init();
		IERecipeTypes.init();
		//IELootFunctions.init();
		IEArgumentTypes.init();
		INBannerPatterns.init();


//		IEMultiblocks.init();
	//	INMultiblockLogic.init(modBus);
		populateAPI();
	}

	@SubscribeEvent
	public static void registerCaps(EntityAttributeCreationEvent ev)
	{
		//ev.put(INEntityTypes.FUSILIER.get(), Fusilier.createAttributes().build());
	}

	@SubscribeEvent
	public static void registerCaps(RegisterCapabilitiesEvent ev)
	{
		//ev.register(IExternalHeatable.class);
	}


	public static void commonSetup(ParallelDispatchEvent ev)
	{
		INWireTypes.setup();
		INStats.setup();


		/*ASSEMBLER RECIPE ADAPTERS*/
		//Fluid Ingredients
		AssemblerHandler.registerSpecialIngredientConverter((o, remain) -> {
			if(o instanceof IngredientFluidStack)
				return new FluidTagRecipeQuery(((IngredientFluidStack)o).getFluidTagInput());
			else
				return null;
		});
		// Buckets
		// TODO add "duplicates" of the fluid-aware recipes that only use buckets, so that other mods using similar
		//  code don't need explicit compat?
		AssemblerHandler.registerSpecialIngredientConverter((o, remain) -> {
			// Must be a vanilla ingredient, which returns an empty bucket
			if(!o.isVanilla()||remain.getItem()!=Items.BUCKET)
				return null;
			// Find bucket out of available items
			Optional<ItemStack> potentialBucket = Arrays.stream(o.getItems())
					.filter(stack -> stack.getItem() instanceof BucketItem)
					.findFirst();
			if(potentialBucket.isEmpty())
				return null;
			final Item bucketItem = potentialBucket.get().getItem();
			//Explicitly check for vanilla-style non-dynamic container items
			//noinspection deprecation
			if(!bucketItem.hasCraftingRemainingItem()||bucketItem.getCraftingRemainingItem()!=Items.BUCKET)
				return null;
			final Fluid contained = ((BucketItem)bucketItem).getFluid();
			return new FluidStackRecipeQuery(new FluidStack(contained, FluidType.BUCKET_VOLUME));
		});
		// Milk is a weird special case
		AssemblerHandler.registerSpecialIngredientConverter((o, remain) -> {
			// Only works when the milk fluid is enabled
			if(!ForgeMod.MILK.isPresent())
				return null;
			// Must be a vanilla ingredient, which returns an empty bucket
			if(!o.isVanilla()||remain.getItem()!=Items.BUCKET)
				return null;
			// Find milk bucket out of available items
			Optional<ItemStack> potentialBucket = Arrays.stream(o.getItems())
					.filter(stack -> stack.getItem()==Items.MILK_BUCKET)
					.findFirst();
			if(potentialBucket.isEmpty())
				return null;
			return new FluidStackRecipeQuery(new FluidStack(ForgeMod.MILK.get(), FluidType.BUCKET_VOLUME));
		});

		// TODO move to IEFluids/constructors?
		//INFluids.CREOSOTE.getBlock().setEffect(INPotions.FLAMMABLE.get(), 100, 0);

	}

	public static void populateAPI()
	{

	}

	public static void clearLastFuture()
	{
		if(lastOnThreadFuture==null)
			return;
		try
		{
			lastOnThreadFuture.get();
		} catch(InterruptedException|ExecutionException e)
		{
			throw new RuntimeException(e);
		}
		lastOnThreadFuture = null;
	}

	public static void setFuture(CompletableFuture<?> next)
	{
		clearLastFuture();
		lastOnThreadFuture = next;
	}
}
