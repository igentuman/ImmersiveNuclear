package igentuman.immersivenuclear.common.crafting;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.api.crafting.CokerUnitRecipe;
import igentuman.immersivenuclear.api.crafting.DistillationRecipe;
import igentuman.immersivenuclear.api.crafting.SulfurRecoveryRecipe;
import igentuman.immersivenuclear.common.crafting.serializers.CokerUnitRecipeSerializer;
import igentuman.immersivenuclear.common.crafting.serializers.DistillationRecipeSerializer;
import igentuman.immersivenuclear.common.crafting.serializers.ReservoirTypeSerializer;
import igentuman.immersivenuclear.common.crafting.serializers.SulfurRecoveryRecipeSerializer;
import igentuman.immersivenuclear.api.crafting.pumpjack.PumpjackHandler;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Serializers{
	public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ImmersiveNuclear.MODID);
	
	public static final RegistryObject<IERecipeSerializer<DistillationRecipe>> DISTILLATION_SERIALIZER = RECIPE_SERIALIZERS.register(
			"distillation", DistillationRecipeSerializer::new
	);
	
	public static final RegistryObject<IERecipeSerializer<CokerUnitRecipe>> COKER_SERIALIZER = RECIPE_SERIALIZERS.register(
			"coker", CokerUnitRecipeSerializer::new
	);
	
	public static final RegistryObject<IERecipeSerializer<SulfurRecoveryRecipe>> HYDROTREATER_SERIALIZER = RECIPE_SERIALIZERS.register(
			"hydrotreater", SulfurRecoveryRecipeSerializer::new
	);
	
	public static final RegistryObject<IERecipeSerializer<PumpjackHandler.ReservoirType>> RESERVOIR_SERIALIZER = RECIPE_SERIALIZERS.register(
			"reservoirs", ReservoirTypeSerializer::new
	);
}
