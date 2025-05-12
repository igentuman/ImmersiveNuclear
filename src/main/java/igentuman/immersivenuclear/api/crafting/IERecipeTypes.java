/*
 * BluSunrize
 * Copyright (c) 2025
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.api.crafting;


import blusunrize.immersiveengineering.api.crafting.AlloyRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static igentuman.immersivenuclear.api.Lib.MODID;

@SuppressWarnings("unchecked")
public class IERecipeTypes {
	private static final DeferredRegister<RecipeType<?>> REGISTER;
	public static final blusunrize.immersiveengineering.api.crafting.IERecipeTypes.TypeWithClass<AlloyRecipe> ALLOY;

	public IERecipeTypes() {
	}

	private static <T extends Recipe<?>> blusunrize.immersiveengineering.api.crafting.IERecipeTypes.TypeWithClass<T> register(String name, Class<T> type) {
		RegistryObject<RecipeType<T>> regObj = REGISTER.register(name, () -> new RecipeType<T>() {
		});
		return new blusunrize.immersiveengineering.api.crafting.IERecipeTypes.TypeWithClass<T>(regObj, type);
	}

	public static void init() {
		REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	static {
		REGISTER = DeferredRegister.create(Registries.RECIPE_TYPE, MODID);
		ALLOY = register("alloy", AlloyRecipe.class);
	}

	public static record TypeWithClass<T extends Recipe<?>>(RegistryObject<RecipeType<T>> type, Class<T> recipeClass) implements Supplier<RecipeType<T>> {
		public TypeWithClass(RegistryObject<RecipeType<T>> type, Class<T> recipeClass) {
			this.type = type;
			this.recipeClass = recipeClass;
		}

		public RecipeType<T> get() {
			return (RecipeType)this.type.get();
		}

		public RegistryObject<RecipeType<T>> type() {
			return this.type;
		}

		public Class<T> recipeClass() {
			return this.recipeClass;
		}
	}
}