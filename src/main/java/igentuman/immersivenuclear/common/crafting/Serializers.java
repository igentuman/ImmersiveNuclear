package igentuman.immersivenuclear.common.crafting;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.api.crafting.CoolingTowerRecipe;
import igentuman.immersivenuclear.api.crafting.SulfurRecoveryRecipe;
import igentuman.immersivenuclear.common.crafting.serializers.CoolingTowerRecipeSerializer;
import igentuman.immersivenuclear.common.crafting.serializers.SulfurRecoveryRecipeSerializer;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Serializers{
	public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ImmersiveNuclear.MODID);
	
	public static final RegistryObject<IERecipeSerializer<CoolingTowerRecipe>> DISTILLATION_SERIALIZER = RECIPE_SERIALIZERS.register(
			"distillation", CoolingTowerRecipeSerializer::new
	);

	public static final RegistryObject<IERecipeSerializer<SulfurRecoveryRecipe>> HYDROTREATER_SERIALIZER = RECIPE_SERIALIZERS.register(
			"hydrotreater", SulfurRecoveryRecipeSerializer::new
	);

}
