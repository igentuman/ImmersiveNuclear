/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.common.register;

import blusunrize.immersiveengineering.common.items.IEBaseItem;
import igentuman.immersivenuclear.api.Lib;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class INItems
{
	public static final int COKE_BURN_TIME = 3200;
	public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, Lib.MODID);

	private INItems()
	{
	}


	public static void init()
	{
		REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
		// Load all classes to make sure the static variables are initialized
	}

	private static <T> Consumer<T> nothing()
	{
		return $ -> {
		};
	}

	private static ItemRegObject<IEBaseItem> simpleWithStackSize(String name, int maxSize)
	{
		return simple(name, p -> p.stacksTo(maxSize), i -> {
		});
	}

	private static ItemRegObject<IEBaseItem> simple(String name)
	{
		return simple(name, $ -> {
		}, $ -> {
		});
	}

	private static ItemRegObject<IEBaseItem> simple(
			String name, Consumer<Properties> makeProps, Consumer<IEBaseItem> processItem
	)
	{
		return register(
				name, () -> Util.make(new IEBaseItem(Util.make(new Properties(), makeProps)), processItem)
		);
	}

	static <T extends Item> ItemRegObject<T> register(String name, Supplier<? extends T> make)
	{
		return new ItemRegObject<>(REGISTER.register(name, make));
	}

	private static <T extends Item> ItemRegObject<T> of(T existing)
	{
		return new ItemRegObject<>(RegistryObject.create(BuiltInRegistries.ITEM.getKey(existing), ForgeRegistries.ITEMS));
	}

	public record ItemRegObject<T extends Item>(RegistryObject<T> regObject) implements Supplier<T>, ItemLike
	{
		@Override
		@Nonnull
		public T get()
		{
			return regObject.get();
		}

		@Nonnull
		@Override
		public Item asItem()
		{
			return regObject.get();
		}

		public ResourceLocation getId()
		{
			return regObject.getId();
		}
	}
}
