/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.data;

import blusunrize.immersiveengineering.common.register.IEItems;
import com.google.gson.JsonObject;
import igentuman.immersivenuclear.ImmersiveNuclear;
import igentuman.immersivenuclear.api.INEnumMetals;
import igentuman.immersivenuclear.api.INTags;
import igentuman.immersivenuclear.common.register.INBlocks;
import igentuman.immersivenuclear.common.register.INItems;
import igentuman.immersivenuclear.common.util.RecipeSerializers;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.function.Consumer;

import static blusunrize.immersiveengineering.api.utils.TagUtils.createItemWrapper;

public class INRecipes extends RecipeProvider
{
	private final HashMap<String, Integer> PATH_COUNT = new HashMap<>();

	private static final int standardSmeltingTime = 200;
	private static final int blastDivider = 2;

	public INRecipes(PackOutput output)
	{
		super(output);
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> out)
	{
		for(INEnumMetals metal : INEnumMetals.values()) {
			INTags.MetalTags tags = INTags.getTagsFor(metal);

			ItemLike rawOre = INItems.Metals.RAW_ORES.get(metal);
			ItemLike nugget = INItems.Metals.NUGGETS.get(metal);
			ItemLike ingot = INItems.Metals.INGOTS.get(metal);
			ItemLike plate = INItems.Metals.PLATES.get(metal);
			ItemLike dust = INItems.Metals.DUSTS.get(metal);
			INBlocks.BlockEntry<Block> block = INBlocks.Metals.STORAGE.get(metal);
			if(metal.shouldAddNugget())
				add3x3Conversion(ingot, nugget, tags.nugget, out);
			if(!metal.isVanillaMetal())
				add3x3Conversion(block, ingot, tags.ingot, out);
			if(metal.shouldAddOre())
			{
				INBlocks.BlockEntry<Block> ore = INBlocks.Metals.ORES.get(metal);
				addStandardSmeltingBlastingRecipe(ore, ingot, metal.smeltingXP, out);
				ore = INBlocks.Metals.DEEPSLATE_ORES.get(metal);
				addStandardSmeltingBlastingRecipe(ore, ingot, metal.smeltingXP, out);
				addStandardSmeltingBlastingRecipe(INItems.Metals.RAW_ORES.get(metal), ingot, metal.smeltingXP, out);
				INBlocks.BlockEntry<Block> rawBlock = INBlocks.Metals.RAW_ORES.get(metal);
				add3x3Conversion(rawBlock, rawOre, tags.rawOre, out);
			}
			if(!metal.isIsotope()) {
				addStandardSmeltingBlastingRecipe(dust, ingot, 0, out, "_from_dust");

				shapelessMisc(plate)
						.requires(INTags.getTagsFor(metal).ingot)
						.requires(IEItems.Tools.HAMMER)
						.unlockedBy("has_" + metal.tagName() + "_ingot", has(INTags.getTagsFor(metal).ingot))
						.save(out, toRL("plate_" + metal.tagName() + "_hammering"));
			}
		}
		recipesMetalDevices(out);
		recipesConnectors(out);
//		recipesMultiblockMachines(out);

	//	mineralMixes(out);

		}


	private void recipesMultiblockMachines(@Nonnull Consumer<FinishedRecipe> out)
	{
		/*ArcFurnaceRecipeBuilder arcBuilder;
		MetalPressRecipeBuilder pressBuilder;
		AlloyRecipeBuilder alloyBuilder;
		SawmillRecipeBuilder sawmillBuilder;

		*//* Common Metals *//*
		for(RecipeMetals metal : RecipeMetals.values())
		{
			if(metal.getOre()!=null)
			{
				SecondaryOutput[] secondaryOutputs = metal.getSecondaryOutputs();

				// Hammer crushing
				HammerCrushingRecipeBuilder hammerBuilder = HammerCrushingRecipeBuilder.builder(metal.getDust());
				if(!metal.isNative())
					hammerBuilder.addCondition(getTagCondition(metal.getDust())).addCondition(getTagCondition(metal.getOre()));
				hammerBuilder.addInput(metal.getOre())
						.build(out, toRL("crafting/hammercrushing_"+metal.getName()));
				HammerCrushingRecipeBuilder rawHammerBuilder = HammerCrushingRecipeBuilder.builder(metal.getDust());
				rawHammerBuilder.addCondition(getTagCondition(metal.getDust())).addCondition(getTagCondition(metal.getRawOre()))
						.addInput(metal.getRawOre())
						.build(out, toRL("crafting/raw_hammercrushing_"+metal.getName()));

				// Crush ore
				CrusherRecipeBuilder oreCrushing = CrusherRecipeBuilder.builder(metal.getDust(), 2);
				if(!metal.isNative())
					oreCrushing.addCondition(getTagCondition(metal.getDust())).addCondition(getTagCondition(metal.getOre()));
				if(secondaryOutputs!=null)
					for(SecondaryOutput secondaryOutput : secondaryOutputs)
						oreCrushing.addSecondary(secondaryOutput.getItem(), secondaryOutput.getChance(), secondaryOutput.getConditions());
				oreCrushing.addInput(metal.getOre())
						.setEnergy(6000)
						.build(out, toRL("crusher/ore_"+metal.getName()));

				CrusherRecipeBuilder rawOreCrushing = CrusherRecipeBuilder.builder(metal.getDust(), 1);
				if(!metal.isNative())
					rawOreCrushing.addCondition(getTagCondition(metal.getDust())).addCondition(getTagCondition(metal.getRawOre()));
				rawOreCrushing.addSecondary(metal.getDust(), 1/3f)
						.addInput(metal.getRawOre())
						.setEnergy(6000)
						.build(out, toRL("crusher/raw_ore_"+metal.getName()));

				TagKey<Item> rawBlock = createItemWrapper(IETags.getRawBlock(metal.getName()));
				rawOreCrushing = CrusherRecipeBuilder.builder(metal.getDust(), 12);
				if(!metal.isNative())
					rawOreCrushing.addCondition(getTagCondition(metal.getDust())).addCondition(getTagCondition(rawBlock));
				rawOreCrushing.addInput(rawBlock)
						.setEnergy(9*6000)
						.build(out, toRL("crusher/raw_block_"+metal.getName()));


				// Arcfurnace ore
				arcBuilder = ArcFurnaceRecipeBuilder.builder(metal.getIngot(), 2);
				if(!metal.isNative())
					arcBuilder.addCondition(getTagCondition(metal.getIngot())).addCondition(getTagCondition(metal.getOre()));
				arcBuilder.addIngredient("input", metal.getOre())
						.addSlag(IETags.slag, 1)
						.setTime(200)
						.setEnergy(102400)
						.build(out, toRL("arcfurnace/ore_"+metal.getName()));

				// Arcfurnace raw ore
				arcBuilder = ArcFurnaceRecipeBuilder.builder(metal.getIngot(), 1);
				arcBuilder.addSecondary(metal.getIngot(), 0.5F);
				if(!metal.isNative())
					arcBuilder.addCondition(getTagCondition(metal.getIngot())).addCondition(getTagCondition(metal.getRawOre()));
				arcBuilder.addIngredient("input", metal.getRawOre())
						.setTime(100)
						.setEnergy(25600)
						.build(out, toRL("arcfurnace/raw_ore_"+metal.getName()));

				// Arcfurnace raw ore block
				arcBuilder = ArcFurnaceRecipeBuilder.builder(metal.getIngot(), 13);
				arcBuilder.addSecondary(metal.getIngot(), 0.5F);
				if(!metal.isNative())
					arcBuilder.addCondition(getTagCondition(metal.getIngot())).addCondition(getTagCondition(metal.getRawBlock()));
				arcBuilder.addIngredient("input", metal.getRawBlock())
						.setTime(9*100)
						.setEnergy(9*25600)
						.build(out, toRL("arcfurnace/raw_block_"+metal.getName()));
			}

			// Crush ingot
			CrusherRecipeBuilder ingotCrushing = CrusherRecipeBuilder.builder(metal.getDust(), 1);
			if(!metal.isNative())
				ingotCrushing.addCondition(getTagCondition(metal.getDust())).addCondition(getTagCondition(metal.getIngot()));
			ingotCrushing.addInput(metal.getIngot())
					.setEnergy(3000)
					.build(out, toRL("crusher/ingot_"+metal.getName()));

			// Arcfurnace dust
			arcBuilder = ArcFurnaceRecipeBuilder.builder(metal.getIngot(), 1);
			if(!metal.isNative())
				arcBuilder.addCondition(getTagCondition(metal.getIngot())).addCondition(getTagCondition(metal.getDust()));
			arcBuilder.addIngredient("input", metal.getDust())
					.setTime(100)
					.setEnergy(51200)
					.build(out, toRL("arcfurnace/dust_"+metal.getName()));

			// Plate
			TagKey<Item> plate = createItemWrapper(IETags.getPlate(metal.getName()));
			pressBuilder = MetalPressRecipeBuilder.builder(Molds.MOLD_PLATE, plate, 1);
			if(!metal.isNative())
				pressBuilder.addCondition(getTagCondition(metal.getIngot())).addCondition(getTagCondition(plate));
			pressBuilder.addInput(metal.getIngot())
					.setEnergy(2400)
					.build(out, toRL("metalpress/plate_"+metal.getName()));

			// Gear
			TagKey<Item> gear = createItemWrapper(IETags.getGear(metal.getName()));
			pressBuilder = MetalPressRecipeBuilder.builder(Molds.MOLD_GEAR, gear, 1);
			if(!metal.isNative())
				pressBuilder.addCondition(getTagCondition(metal.getIngot()));
			pressBuilder.addCondition(getTagCondition(gear))
					.addInput(new IngredientWithSize(metal.getIngot(), 4))
					.setEnergy(2400)
					.build(out, toRL("metalpress/gear_"+metal.getName()));

			// Rod
			TagKey<Item> rods = createItemWrapper(IETags.getRod(metal.getName()));
			pressBuilder = MetalPressRecipeBuilder.builder(Molds.MOLD_ROD, rods, 2);
			if(!metal.isNative())
				pressBuilder.addCondition(getTagCondition(metal.getIngot()));
			pressBuilder.addCondition(getTagCondition(rods))
					.addInput(metal.getIngot())
					.setEnergy(2400)
					.build(out, toRL("metalpress/rod_"+metal.getName()));

			// Wire
			TagKey<Item> wire = createItemWrapper(IETags.getWire(metal.getName()));
			pressBuilder = MetalPressRecipeBuilder.builder(Molds.MOLD_WIRE, wire, 2);
			if(!metal.isNative())
				pressBuilder.addCondition(getTagCondition(metal.getIngot()));
			pressBuilder.addCondition(getTagCondition(wire))
					.addInput(metal.getIngot())
					.setEnergy(2400)
					.build(out, toRL("metalpress/wire_"+metal.getName()));

			AlloyProperties alloy = metal.getAlloyProperties();
			if(alloy!=null)
			{
				IngredientWithSize[] ingredients = alloy.getAlloyIngredients();
				if(alloy.isSimple())
				{
					alloyBuilder = AlloyRecipeBuilder.builder(metal.getIngot(), alloy.getOutputSize());
					if(!metal.isNative())
						alloyBuilder.addCondition(getTagCondition(metal.getIngot()));
					for(ICondition condition : alloy.getConditions())
						alloyBuilder.addCondition(condition);
					for(IngredientWithSize ingr : ingredients)
						alloyBuilder.addInput(ingr);
					alloyBuilder.build(out, toRL("alloysmelter/"+metal.getName()));
				}

				arcBuilder = ArcFurnaceRecipeBuilder.builder(metal.getIngot(), alloy.getOutputSize());
				if(!metal.isNative())
					arcBuilder.addCondition(getTagCondition(metal.getIngot()));
				for(ICondition condition : alloy.getConditions())
					arcBuilder.addCondition(condition);
				arcBuilder.addIngredient("input", ingredients[0]);
				for(int i = 1; i < ingredients.length; i++)
					arcBuilder.addInput(ingredients[i]);
				arcBuilder.setTime(100)
						.setEnergy(51200)
						.build(out, toRL("arcfurnace/alloy_"+metal.getName()));
			}
		}

		// Non-metal ores
		for(RecipeOres ore : RecipeOres.values())
		{
			SecondaryOutput[] secondaryOutputs = ore.getSecondaryOutputs();
			CrusherRecipeBuilder oreCrushing = CrusherRecipeBuilder.builder(ore.getOutput());
			if(!ore.isNative())
				oreCrushing.addCondition(getTagCondition(ore.getOre()));
			if(secondaryOutputs!=null)
				for(SecondaryOutput secondaryOutput : secondaryOutputs)
					oreCrushing.addSecondary(secondaryOutput.getItem(), secondaryOutput.getChance(), secondaryOutput.getConditions());
			oreCrushing.addInput(ore.getOre())
					.setEnergy(6000)
					.build(out, toRL("crusher/ore_"+ore.getName()));
		}

		*//* ALLOY SMELTER *//*
		AlloyRecipeBuilder.builder(new ItemStack(StoneDecoration.INSULATING_GLASS.asItem(), 2))
				.addInput(new IngredientWithSize(Tags.Items.GLASS, 2))
				.addInput(IETags.getTagsFor(EnumMetals.IRON).dust)
				.build(out, toRL("alloysmelter/"+toPath(StoneDecoration.INSULATING_GLASS)));

		*//* METAL PRESS *//*
		MetalPressRecipeBuilder.builder(Molds.MOLD_BULLET_CASING, new ItemStack(Ingredients.EMPTY_CASING, 2))
				.addInput(IETags.getTagsFor(EnumMetals.COPPER).ingot)
				.setEnergy(2400)
				.build(out, toRL("metalpress/bullet_casing"));

		ItemStack electrode = new ItemStack(Misc.GRAPHITE_ELECTRODE);
		electrode.setDamageValue(IEServerConfig.MACHINES.arcfurnace_electrodeDamage.getDefault()/2);
		MetalPressRecipeBuilder.builder(Molds.MOLD_ROD, electrode)
				.addInput(new IngredientWithSize(IETags.hopGraphiteIngot, 4))
				.setEnergy(4800)
				.build(out, toRL("metalpress/electrode"));

		MetalPressRecipeBuilder.builder(Molds.MOLD_UNPACKING, new ItemStack(Items.MELON_SLICE, 9))
				.addInput(Items.MELON)
				.setEnergy(3200)
				.build(out, toRL("metalpress/melon"));

		MetalPressRecipeBuilder.builder(Molds.MOLD_ROD, Items.BLAZE_ROD)
				.addInput(new IngredientWithSize(Ingredient.of(Items.BLAZE_POWDER), 5))
				.setEnergy(3200)
				.build(out, toRL("metalpress/blaze_rod"));

		*//* ARC FURNACE *//*
		ArcFurnaceRecipeBuilder.builder(IETags.getTagsFor(EnumMetals.STEEL).ingot, 1)
				.addIngredient("input", Tags.Items.INGOTS_IRON)
				.addInput(IETags.coalCokeDust)
				.addSlag(IETags.slag, 1)
				.setTime(400)
				.setEnergy(204800)
				.build(out, toRL("arcfurnace/steel"));

		ArcFurnaceRecipeBuilder.builder(new ItemStack(Items.NETHERITE_SCRAP, 2))
				.addIngredient("input", Items.ANCIENT_DEBRIS)
				.addSlag(IETags.slag, 1)
				.setTime(100)
				.setEnergy(512000)
				.build(out, toRL("arcfurnace/netherite_scrap"));

		ArcFurnaceRecipeBuilder.builder(new ItemStack(StoneDecoration.INSULATING_GLASS.asItem(), 2))
				.addIngredient("input", new IngredientWithSize(Tags.Items.GLASS, 2))
				.addInput(IETags.getTagsFor(EnumMetals.IRON).dust)
				.setTime(100)
				.setEnergy(51200)
				.build(out, toRL("arcfurnace/"+toPath(StoneDecoration.INSULATING_GLASS)));

		// partial bucket values for bottling & mixing
		int half_bucket = FluidType.BUCKET_VOLUME/2;
		int quarter_bucket = FluidType.BUCKET_VOLUME/4;
		int eighth_bucket = FluidType.BUCKET_VOLUME/8;

		*//* BOTTLING *//*
		BottlingMachineRecipeBuilder.builder(Items.WET_SPONGE)
				.addInput(Items.SPONGE)
				.addFluidTag(FluidTags.WATER, FluidType.BUCKET_VOLUME)
				.build(out, toRL("bottling/sponge"));
		BottlingMachineRecipeBuilder.builder(Items.MUD)
				.addInput(Items.DIRT)
				.addFluidTag(FluidTags.WATER, quarter_bucket)
				.build(out, toRL("bottling/mud"));
		BottlingMachineRecipeBuilder.builder(Items.EXPOSED_COPPER)
				.addInput(Items.COPPER_BLOCK)
				.addFluidTag(IETags.fluidRedstoneAcid, eighth_bucket)
				.build(out, toRL("bottling/copper_aging"));
		BottlingMachineRecipeBuilder.builder(Items.WEATHERED_COPPER)
				.addInput(Items.EXPOSED_COPPER)
				.addFluidTag(IETags.fluidRedstoneAcid, eighth_bucket)
				.build(out, toRL("bottling/copper_aging"));
		BottlingMachineRecipeBuilder.builder(Items.OXIDIZED_COPPER)
				.addInput(Items.WEATHERED_COPPER)
				.addFluidTag(IETags.fluidRedstoneAcid, eighth_bucket)
				.build(out, toRL("bottling/copper_aging"));
		BottlingMachineRecipeBuilder.builder(Ingredients.ERSATZ_LEATHER.get())
				.addResult(Molds.MOLD_PLATE)
				.setUseInputArray(2)
				.addInput(Molds.MOLD_PLATE)
				.addInput(IETags.fabricHemp)
				.addFluidTag(IETags.fluidPlantoil, eighth_bucket)
				.build(out, toRL("bottling/"+toPath(Ingredients.ERSATZ_LEATHER)));
		BottlingMachineRecipeBuilder.builder(Ingredients.DUROPLAST_PLATE.get())
				.addResult(Molds.MOLD_PLATE)
				.addInput(Molds.MOLD_PLATE)
				.addFluidTag(IETags.fluidResin, quarter_bucket)
				.build(out, toRL("bottling/duroplast_plate"));
		BottlingMachineRecipeBuilder.builder(new ItemStack(StoneDecoration.DUROPLAST, 4))
				.addResult(Molds.MOLD_PACKING_4)
				.addInput(Molds.MOLD_PACKING_4)
				.addFluidTag(IETags.fluidResin, FluidType.BUCKET_VOLUME*4)
				.build(out, toRL("bottling/duroplast_block"));
		BottlingMachineRecipeBuilder.builder(new ItemStack(Ingredients.EMPTY_SHELL, 2))
				.addResult(Molds.MOLD_BULLET_CASING)
				.setUseInputArray(2)
				.addInput(Molds.MOLD_BULLET_CASING)
				.addInput(new IngredientWithSize(IETags.getTagsFor(EnumMetals.COPPER).nugget, 3))
				.addFluidTag(IETags.fluidResin, quarter_bucket)
				.build(out, toRL("bottling/"+toPath(BulletHandler.emptyShell)));
		BottlingMachineRecipeBuilder.builder(Tools.GRINDINGDISK.get())
				.addResult(Molds.MOLD_GEAR)
				.setUseInputArray(3)
				.addInput(Molds.MOLD_GEAR)
				.addInput(new IngredientWithSize(IETags.getTagsFor(EnumMetals.ALUMINUM).dust, 6))
				.addInput(new IngredientWithSize(IETags.fiberHemp, 8))
				.addFluidTag(IETags.fluidResin, half_bucket)
				.build(out, toRL("bottling/"+toPath(Tools.GRINDINGDISK)));

		*//* CRUSHER *//*
		CrusherRecipeBuilder.builder(Items.GRAVEL)
				.addInput(Tags.Items.COBBLESTONE)
				.setEnergy(1600)
				.build(out, toRL("crusher/cobblestone"));
		CrusherRecipeBuilder.builder(Items.SAND)
				.addSecondary(Items.FLINT, .1f)
				.addInput(Tags.Items.GRAVEL)
				.setEnergy(1600)
				.build(out, toRL("crusher/gravel"));
		CrusherRecipeBuilder.builder(StoneDecoration.SLAG_GRAVEL.asItem())
				.addInput(IETags.slag)
				.setEnergy(1600)
				.build(out, toRL("crusher/slag"));
		CrusherRecipeBuilder.builder(Items.SAND)
				.addInput(Tags.Items.GLASS)
				.setEnergy(3200)
				.build(out, toRL("crusher/glass"));
		CrusherRecipeBuilder.builder(new ItemStack(Items.SAND, 2))
				.addSecondary(IETags.saltpeterDust, .5f)
				.addInput(IETags.getItemTag(IETags.colorlessSandstoneBlocks))
				.setEnergy(3200)
				.build(out, toRL("crusher/sandstone"));
		CrusherRecipeBuilder.builder(new ItemStack(Items.RED_SAND, 2))
				.addSecondary(IETags.saltpeterDust, .5f)
				.addInput(IETags.getItemTag(IETags.redSandstoneBlocks))
				.setEnergy(3200)
				.build(out, toRL("crusher/red_sandstone"));
		CrusherRecipeBuilder.builder(new ItemStack(Items.CLAY_BALL, 4))
				.addInput(IETags.getItemTag(IETags.clayBlock))
				.setEnergy(1600)
				.build(out, toRL("crusher/clay"));
		CrusherRecipeBuilder.builder(new ItemStack(Items.AMETHYST_SHARD, 4))
				.addInput(Tags.Items.STORAGE_BLOCKS_AMETHYST)
				.setEnergy(3200)
				.build(out, toRL("crusher/amethyst"));
		CrusherRecipeBuilder.builder(new ItemStack(Items.QUARTZ, 4))
				.addInput(Tags.Items.STORAGE_BLOCKS_QUARTZ)
				.setEnergy(3200)
				.build(out, toRL("crusher/quartz"));
		CrusherRecipeBuilder.builder(new ItemStack(Items.GLOWSTONE_DUST, 4))
				.addInput(Blocks.GLOWSTONE)
				.setEnergy(3200)
				.build(out, toRL("crusher/glowstone"));
		CrusherRecipeBuilder.builder(new ItemStack(Items.PRISMARINE_SHARD, 4))
				.addInput(Blocks.PRISMARINE)
				.setEnergy(3200)
				.build(out, toRL("crusher/prismarine"));
		CrusherRecipeBuilder.builder(new ItemStack(Items.PRISMARINE_SHARD, 8))
				.addInput(Blocks.DARK_PRISMARINE)
				.setEnergy(3200)
				.build(out, toRL("crusher/dark_prismarine"));
		CrusherRecipeBuilder.builder(new ItemStack(Items.PRISMARINE_SHARD, 9))
				.addInput(Blocks.PRISMARINE_BRICKS)
				.setEnergy(3200)
				.build(out, toRL("crusher/prismarine_brick"));
		CrusherRecipeBuilder.builder(new ItemStack(Items.BLAZE_POWDER, 4))
				.addSecondary(IETags.sulfurDust, .5f)
				.addInput(Tags.Items.RODS_BLAZE)
				.setEnergy(1600)
				.build(out, toRL("crusher/blaze_powder"));
		CrusherRecipeBuilder.builder(new ItemStack(Items.BONE_MEAL, 6))
				.addInput(Items.BONE)
				.setEnergy(1600)
				.build(out, toRL("crusher/bone_meal"));
		CrusherRecipeBuilder.builder(IETags.coalCokeDust, 1)
				.addInput(IETags.coalCoke)
				.setEnergy(2400)
				.build(out, toRL("crusher/coke"));
		CrusherRecipeBuilder.builder(IETags.coalCokeDust, 9)
				.addInput(IETags.getItemTag(IETags.coalCokeBlock))
				.setEnergy(4800)
				.build(out, toRL("crusher/coke_block"));

		TagKey<Item> coal_dust = createItemWrapper(IETags.getDust("coal"));
		CrusherRecipeBuilder.builder(coal_dust, 1)
				.addCondition(getTagCondition(coal_dust))
				.addInput(Items.COAL)
				.setEnergy(2400)
				.build(out, toRL("crusher/coal"));
		CrusherRecipeBuilder.builder(coal_dust, 9)
				.addCondition(getTagCondition(coal_dust))
				.addInput(Items.COAL_BLOCK)
				.setEnergy(4800)
				.build(out, toRL("crusher/coal_block"));

		CrusherRecipeBuilder.builder(new ItemStack(Items.STRING, 4))
				.addInput(ItemTags.WOOL)
				.setEnergy(3200)
				.build(out, toRL("crusher/wool"));

		CrusherRecipeBuilder.builder(IETags.getTagsFor(EnumMetals.GOLD).dust, 2)
				.addInput(Items.NETHER_GOLD_ORE)
				.setEnergy(4200)
				.build(out, toRL("crusher/nether_gold"));

		CrusherRecipeBuilder.builder(new ItemStack(Items.NETHER_WART, 9))
				.addInput(Items.NETHER_WART_BLOCK)
				.setEnergy(1600)
				.build(out, toRL("crusher/nether_wart"));

		CrusherRecipeBuilder.builder(new ItemStack(Items.BLACK_DYE, 1))
				.addSecondary(Items.GRAY_DYE, .2f)
				.addInput(Items.CHARCOAL)
				.setEnergy(1600)
				.build(out, toRL("crusher/black_dye"));

		CrusherRecipeBuilder.builder(new ItemStack(Items.BLUE_DYE, 2))
				.addSecondary(Items.LIGHT_GRAY_DYE, .1f)
				.addInput(Tags.Items.GEMS_LAPIS)
				.setEnergy(1600)
				.build(out, toRL("crusher/blue_dye"));

		CrusherRecipeBuilder.builder(new ItemStack(Items.WHITE_DYE, 2))
				.addSecondary(Items.LIGHT_GRAY_DYE, .1f)
				.addInput(Items.BONE_MEAL)
				.setEnergy(1600)
				.build(out, toRL("crusher/white_dye"));

		*//* SAWMILL *//*
		for(RecipeWoods wood : RecipeWoods.values())
		{
			// Basic log
			if(wood.getLog()!=null)
			{
				sawmillBuilder = SawmillRecipeBuilder.builder(new ItemStack(wood.getPlank(), wood.plankCount()))
						.addInput(wood.getLog())
						.setEnergy(1600);
				if(wood.getStripped()!=null)
				{
					sawmillBuilder.addStripped(wood.getStripped());
					if(wood.produceSawdust())
						sawmillBuilder.addSecondary(IETags.sawdust, true);
				}
				if(wood.produceSawdust())
					sawmillBuilder.addSecondary(IETags.sawdust, false);
				sawmillBuilder.build(out, toRL("sawmill/"+wood.getName()+"_log"));
			}
			// All-bark block
			if(wood.getWood()!=null)
			{
				sawmillBuilder = SawmillRecipeBuilder.builder(new ItemStack(wood.getPlank(), wood.plankCount()))
						.addInput(wood.getWood())
						.setEnergy(1600);
				if(wood.getStrippedWood()!=null)
				{
					sawmillBuilder.addStripped(wood.getStrippedWood());
					if(wood.produceSawdust())
						sawmillBuilder.addSecondary(IETags.sawdust, true);
				}
				if(wood.produceSawdust())
					sawmillBuilder.addSecondary(IETags.sawdust, false);
				sawmillBuilder.build(out, toRL("sawmill/"+wood.getName()+"_wood"));
			}
			// Already stripped log
			if(wood.getStripped()!=null)
			{
				sawmillBuilder = SawmillRecipeBuilder.builder(new ItemStack(wood.getPlank(), wood.plankCount()))
						.setEnergy(800);
				if(wood.getWood()!=null)
					sawmillBuilder.addInput(wood.getStripped(), wood.getStrippedWood());
				else
					sawmillBuilder.addInput(wood.getStripped());
				if(wood.produceSawdust())
					sawmillBuilder.addSecondary(IETags.sawdust, false);
				sawmillBuilder.build(out, toRL("sawmill/stripped_"+wood.getName()+"_log"));
			}
			// Door
			if(wood.getDoor()!=null)
			{
				sawmillBuilder = SawmillRecipeBuilder.builder(new ItemStack(wood.getPlank(), 1))
						.addInput(wood.getDoor())
						.setEnergy(800);
				if(wood.produceSawdust())
					sawmillBuilder.addSecondary(IETags.sawdust, false);
				sawmillBuilder.build(out, toRL("sawmill/"+wood.getName()+"_door"));
			}
			// Stairs
			if(wood.getStairs()!=null)
			{
				sawmillBuilder = SawmillRecipeBuilder.builder(new ItemStack(wood.getPlank(), 1))
						.addInput(wood.getStairs())
						.setEnergy(1600);
				if(wood.produceSawdust())
					sawmillBuilder.addSecondary(IETags.sawdust, false);
				sawmillBuilder.build(out, toRL("sawmill/"+wood.getName()+"_stairs"));
			}
			// Slabs
			if(wood.getSlab()!=null)
			{
				sawmillBuilder = SawmillRecipeBuilder.builder(new ItemStack(wood.getSlab(), 2))
						.addInput(wood.getPlank())
						.setEnergy(800);
				if(wood.produceSawdust())
					sawmillBuilder.addSecondary(IETags.sawdust, false);
				sawmillBuilder.build(out, toRL("sawmill/"+wood.getName()+"_slab"));
			}
		}
		for(TreatedWoodStyles style : TreatedWoodStyles.values())
		{
			BlockEntry<IEBaseBlock> plank = WoodenDecoration.TREATED_WOOD.get(style);
			SawmillRecipeBuilder.builder(new ItemStack(INBlocks.TO_SLAB.get(plank.getId()), 2))
					.addInput(plank.get())
					.setEnergy(800)
					.addSecondary(IETags.sawdust, false)
					.build(out, toRL("sawmill/treated_wood_"+style.name().toLowerCase(Locale.ROOT)+"_slab"));

			SawmillRecipeBuilder.builder(new ItemStack(plank.get(), 1))
					.addInput(INBlocks.TO_STAIRS.get(plank.getId()))
					.setEnergy(1600)
					.addSecondary(IETags.sawdust, false)
					.build(out, toRL("sawmill/treated_wood_"+style.name().toLowerCase(Locale.ROOT)+"_stairs"));
		}
		SawmillRecipeBuilder.builder(new ItemStack(Items.OAK_PLANKS, 4))
				.addInput(Items.BOOKSHELF)
				.addSecondary(IETags.sawdust, false)
				.addSecondary(new ItemStack(Items.BOOK, 3), false)
				.setEnergy(1600)
				.build(out, toRL("sawmill/bookshelf"));

		*//* SQUEEZER *//*
		Fluid plantOil = INFluids.PLANTOIL.getStill();
		SqueezerRecipeBuilder.builder(plantOil, 80)
				.addInput(Items.WHEAT_SEEDS)
				.setEnergy(6400)
				.build(out, toRL("squeezer/wheat_seeds"));
		SqueezerRecipeBuilder.builder(plantOil, 60)
				.addInput(Items.BEETROOT_SEEDS)
				.setEnergy(6400)
				.build(out, toRL("squeezer/beetroot_seeds"));
		SqueezerRecipeBuilder.builder(plantOil, 40)
				.addInput(Items.PUMPKIN_SEEDS)
				.setEnergy(6400)
				.build(out, toRL("squeezer/pumpkin_seeds"));
		SqueezerRecipeBuilder.builder(plantOil, 20)
				.addInput(Items.MELON_SEEDS)
				.setEnergy(6400)
				.build(out, toRL("squeezer/melon_seeds"));
		SqueezerRecipeBuilder.builder(plantOil, 120)
				.addInput(Misc.HEMP_SEEDS)
				.setEnergy(6400)
				.build(out, toRL("squeezer/hemp_seeds"));
		SqueezerRecipeBuilder.builder()
				.addResult(new IngredientWithSize(IETags.hopGraphiteDust))
				.addInput(new IngredientWithSize(IETags.coalCokeDust, 8))
				.setEnergy(19200)
				.build(out, toRL("squeezer/graphite_dust"));
		*//* FERMENTER *//*
		Fluid ethanol = INFluids.ETHANOL.getStill();
		FermenterRecipeBuilder.builder(ethanol, 80)
				.addInput(Items.SUGAR_CANE)
				.setEnergy(6400)
				.build(out, toRL("fermenter/sugar_cane"));
		FermenterRecipeBuilder.builder(ethanol, 20)
				.addInput(Items.MELON_SLICE)
				.setEnergy(6400)
				.build(out, toRL("fermenter/melon_slice"));
		FermenterRecipeBuilder.builder(ethanol, 80)
				.addInput(Items.APPLE)
				.setEnergy(6400)
				.build(out, toRL("fermenter/apple"));
		FermenterRecipeBuilder.builder(ethanol, 80)
				.addInput(Tags.Items.CROPS_POTATO)
				.setEnergy(6400)
				.build(out, toRL("fermenter/potato"));
		FermenterRecipeBuilder.builder(ethanol, 40)
				.addInput(Tags.Items.CROPS_BEETROOT)
				.setEnergy(6400)
				.build(out, toRL("fermenter/beetroot"));
		FermenterRecipeBuilder.builder(ethanol, 50)
				.addInput(Items.SWEET_BERRIES)
				.setEnergy(6400)
				.build(out, toRL("fermenter/sweet_berries"));
		FermenterRecipeBuilder.builder(ethanol, 100)
				.addInput(Items.GLOW_BERRIES)
				.setEnergy(6400)
				.build(out, toRL("fermenter/glow_berries"));
		FermenterRecipeBuilder.builder(ethanol, 250)
				.addResult(Items.GLASS_BOTTLE)
				.addInput(Items.HONEY_BOTTLE)
				.setEnergy(6400)
				.build(out, toRL("fermenter/honey"));
		*//* REFINERY *//*
		RefineryRecipeBuilder.builder(INFluids.BIODIESEL.getStill(), 16)
				.addCatalyst(IETags.saltpeterDust)
				.addInput(IETags.fluidPlantoil, 8)
				.addInput(IETags.fluidEthanol, 8)
				.setEnergy(80)
				.build(out, toRL("refinery/biodiesel"));
		RefineryRecipeBuilder.builder(INFluids.ACETALDEHYDE.getStill(), 8)
				.addCatalyst(IETags.getTagsFor(EnumMetals.SILVER).plate)
				.addInput(IETags.fluidEthanol, 8)
				.setEnergy(120)
				.build(out, toRL("refinery/acetaldehyde"));
		RefineryRecipeBuilder.builder(INFluids.PHENOLIC_RESIN.getStill(), 8)
				.addInput(IETags.fluidAcetaldehyde, 12)
				.addInput(IETags.fluidCreosote, 8)
				.setEnergy(240)
				.build(out, toRL("refinery/resin"));
		*//* MIXER *//*
		Fluid concrete = INFluids.CONCRETE.getStill();
		MixerRecipeBuilder.builder(concrete, half_bucket)
				.addFluidTag(FluidTags.WATER, half_bucket)
				.addInput(new IngredientWithSize(Tags.Items.SAND, 2))
				.addInput(Tags.Items.GRAVEL)
				.addInput(IETags.clay)
				.setEnergy(3200)
				.build(out, toRL("mixer/concrete"));
		MixerRecipeBuilder.builder(INFluids.HERBICIDE.getStill(), half_bucket)
				.addFluidTag(IETags.fluidEthanol, half_bucket)
				.addInput(IETags.sulfurDust)
				.addInput(IETags.getTagsFor(EnumMetals.COPPER).dust)
				.setEnergy(3200)
				.build(out, toRL("mixer/herbicide"));
		MixerRecipeBuilder.builder(INFluids.REDSTONE_ACID.getStill(), quarter_bucket)
				.addFluidTag(FluidTags.WATER, quarter_bucket)
				.addInput(Tags.Items.DUSTS_REDSTONE)
				.setEnergy(1600)
				.build(out, toRL("mixer/redstone_acid"));*/
	}

	/*private void mineralMixes(@Nonnull Consumer<FinishedRecipe> out)
	{
		// Metals
		TagKey<Item> iron = Tags.Items.ORES_IRON;
		TagKey<Item> gold = Tags.Items.ORES_GOLD;
		TagKey<Item> copper = Tags.Items.ORES_COPPER;
		TagKey<Item> aluminum = IETags.getItemTag(IETags.getTagsFor(EnumMetals.ALUMINUM).ore);
		TagKey<Item> lead = IETags.getItemTag(IETags.getTagsFor(EnumMetals.LEAD).ore);
		TagKey<Item> silver = IETags.getItemTag(IETags.getTagsFor(EnumMetals.SILVER).ore);
		TagKey<Item> nickel = IETags.getItemTag(IETags.getTagsFor(EnumMetals.NICKEL).ore);
		TagKey<Item> uranium = IETags.getItemTag(IETags.getTagsFor(EnumMetals.URANIUM).ore);
		TagKey<Item> tin = createItemWrapper(IETags.getOre("tin"));
		TagKey<Item> titanium = createItemWrapper(IETags.getOre("titanium"));
		TagKey<Item> thorium = createItemWrapper(IETags.getOre("thorium"));
		TagKey<Item> tungsten = createItemWrapper(IETags.getOre("tungsten"));
		TagKey<Item> manganese = createItemWrapper(IETags.getOre("manganese"));
		TagKey<Item> platinum = createItemWrapper(IETags.getOre("platinum"));
		TagKey<Item> paladium = createItemWrapper(IETags.getOre("paladium"));
		TagKey<Item> mercury = createItemWrapper(IETags.getOre("mercury"));
		// Gems & Dusts
		TagKey<Item> sulfur = IETags.sulfurDust;
		TagKey<Item> phosphorus = createItemWrapper(IETags.getDust("phosphorus"));
		TagKey<Item> redstone = Tags.Items.ORES_REDSTONE;
		TagKey<Item> emerald = Tags.Items.ORES_EMERALD;
		Block prismarine = Blocks.PRISMARINE;
		TagKey<Item> aquamarine = createItemWrapper(IETags.getGem("aquamarine"));
		//Dimensions
		ResourceKey<DimensionType> overworld = BuiltinDimensionTypes.OVERWORLD;
		ResourceKey<DimensionType> nether = BuiltinDimensionTypes.NETHER;

		//Decorative Blocks
		MineralMixBuilder.builder(overworld)
				.addSoilSpoils()
				.addOre(Items.CLAY, .5f)
				.addOre(Items.SAND, .3f)
				.addOre(Items.GRAVEL, .2f)
				.setWeight(25)
				.setFailchance(.05f)
				.build(out, toRL("mineral/silt"));
		MineralMixBuilder.builder(overworld)
				.addOverworldSpoils()
				.addOre(Blocks.GRANITE, .3f)
				.addOre(Blocks.DIORITE, .3f)
				.addOre(Blocks.ANDESITE, .3f)
				.addOre(Blocks.OBSIDIAN, .1f)
				.setWeight(25)
				.setFailchance(.05f)
				.build(out, toRL("mineral/igneous_rock"));
		MineralMixBuilder.builder(overworld)
				.addSoilSpoils()
				.addOre(Items.TERRACOTTA, .6f)
				.addOre(Items.RED_SANDSTONE, .3f)
				.addOre(Items.RED_SAND, .1f)
				.setWeight(15)
				.setFailchance(.05f)
				.build(out, toRL("mineral/hardened_clay_pan"));
		MineralMixBuilder.builder(overworld)
				.addSeabedSpoils()
				.addOre(Blocks.CALCITE, .65f)
				.addOre(Blocks.DRIPSTONE_BLOCK, .3f)
				.addOre(Blocks.BONE_BLOCK, .05f)
				.setWeight(15)
				.setFailchance(.05f)
				.build(out, toRL("mineral/ancient_seabed"));
		MineralMixBuilder.builder(overworld)
				.addOverworldSpoils()
				.addOre(Blocks.AMETHYST_BLOCK, .4f)
				.addOre(Blocks.CALCITE, .3f)
				.addOre(Blocks.SMOOTH_BASALT, .3f)
				.setWeight(10)
				.setFailchance(.1f)
				.build(out, toRL("mineral/amethyst_crevasse"));
		// Common things
		MineralMixBuilder.builder(overworld)
				.addOverworldSpoils()
				.addOre(Tags.Items.ORES_COAL, .8f)
				.addOre(sulfur, .2f)
				.addOre(phosphorus, .2f, getTagCondition(phosphorus))
				.setWeight(25)
				.setFailchance(.05f)
				.build(out, toRL("mineral/bituminous_coal"));
		// Metals
		MineralMixBuilder.builder(overworld)
				.addOverworldSpoils()
				.addOre(iron, .35f)
				.addOre(nickel, .35f)
				.addOre(sulfur, .3f)
				.setWeight(25)
				.setFailchance(.05f)
				.build(out, toRL("mineral/pentlandite"));
		MineralMixBuilder.builder(overworld)
				.addOverworldSpoils()
				.addOre(iron, .35f)
				.addOre(copper, .35f)
				.addOre(sulfur, .3f)
				.setWeight(20)
				.setFailchance(.05f)
				.build(out, toRL("mineral/chalcopyrite"));
		MineralMixBuilder.builder(overworld)
				.addOverworldSpoils()
				.addOre(aluminum, .7f)
				.addOre(iron, .2f)
				.addOre(titanium, .1f, getTagCondition(titanium))
				.setWeight(20)
				.setFailchance(.05f)
				.build(out, toRL("mineral/laterite"));
		MineralMixBuilder.builder(overworld)
				.addOverworldSpoils()
				.addOre(copper, .75f)
				.addOre(gold, .25f)
				.setWeight(30)
				.setFailchance(.1f)
				.build(out, toRL("mineral/auricupride"));
		MineralMixBuilder.builder(overworld)
				.addOverworldSpoils()
				.addOre(lead, .4f)
				.addOre(sulfur, .4f)
				.addOre(silver, .2f)
				.setWeight(15)
				.setFailchance(.05f)
				.build(out, toRL("mineral/galena"));
		MineralMixBuilder.builder(overworld)
				.addOverworldSpoils()
				.addOre(redstone, .6f)
				.addOre(sulfur, .4f)
				.addOre(mercury, .3f, getTagCondition(mercury))
				.setWeight(15)
				.setFailchance(.1f)
				.build(out, toRL("mineral/cinnabar"));
		// Rare
		MineralMixBuilder.builder(overworld)
				.addOverworldSpoils()
				.addOre(uranium, .7f)
				.addOre(lead, .3f)
				.addOre(thorium, .1f, getTagCondition(thorium))
				.setWeight(10)
				.setFailchance(.15f)
				.build(out, toRL("mineral/uraninite"));
		MineralMixBuilder.builder(overworld)
				.addOverworldSpoils()
				.addOre(emerald, .3f)
				.addOre(prismarine, .7f)
				.addOre(aquamarine, .3f, getTagCondition(aquamarine))
				.setWeight(5)
				.setFailchance(.2f)
				.build(out, toRL("mineral/beryl"));
		// Nether
		MineralMixBuilder.builder(nether)
				.addNetherSpoils()
				.addOre(Blocks.NETHER_QUARTZ_ORE, .6f)
				.addOre(Blocks.NETHER_GOLD_ORE, .2f)
				.addOre(sulfur, .2f)
				.setWeight(20)
				.setFailchance(.15f)
				.setBackground(ForgeRegistries.BLOCKS.getKey(Blocks.NETHERRACK))
				.build(out, toRL("mineral/mephitic_quarzite"));
		MineralMixBuilder.builder(nether)
				.addNetherSpoils()
				.addOre(Blocks.POLISHED_BLACKSTONE_BRICKS, .4f)
				.addOre(Blocks.POLISHED_BLACKSTONE, .3f)
				.addOre(Blocks.ANCIENT_DEBRIS, .2f)
				.addOre(Blocks.GILDED_BLACKSTONE, .1f)
				.setWeight(8)
				.setFailchance(.5f)
				.setBackground(ForgeRegistries.BLOCKS.getKey(Blocks.POLISHED_BLACKSTONE))
				.build(out, toRL("mineral/ancient_debris"));
		MineralMixBuilder.builder(nether)
				.addNetherSpoils()
				.addOre(Items.SOUL_SOIL, .5f)
				.addOre(Items.SOUL_SAND, .3f)
				.addOre(Items.GRAVEL, .2f)
				.setWeight(15)
				.setFailchance(.05f)
				.setBackground(ForgeRegistries.BLOCKS.getKey(Blocks.SOUL_SOIL))
				.build(out, toRL("mineral/nether_silt"));
		MineralMixBuilder.builder(nether)
				.addNetherSpoils()
				.addOre(Items.MAGMA_BLOCK, .5f)
				.addOre(Items.SMOOTH_BASALT, .3f)
				.addOre(Items.OBSIDIAN, .2f)
				.setWeight(15)
				.setFailchance(.05f)
				.setBackground(ForgeRegistries.BLOCKS.getKey(Blocks.NETHERRACK))
				.build(out, toRL("mineral/cooled_lava_tube"));

		// Compat
		MineralMixBuilder.builder(overworld)
				.addOverworldSpoils()
				.addCondition(getTagCondition(tin))
				.addOre(tin, 1)
				.setWeight(20)
				.setFailchance(.05f)
				.build(out, toRL("mineral/cassiterite"));
		MineralMixBuilder.builder(overworld)
				.addOverworldSpoils()
				.addCondition(getTagCondition(platinum))
				.addOre(platinum, .5f)
				.addOre(paladium, .5f, getTagCondition(paladium))
				.addOre(nickel, .5f)
				.setWeight(5)
				.setFailchance(.1f)
				.build(out, toRL("mineral/cooperite"));
		MineralMixBuilder.builder(overworld)
				.addOverworldSpoils()
				.addCondition(getTagCondition(tungsten))
				.addOre(tungsten, .5f)
				.addOre(iron, .5f)
				.addOre(manganese, .5f, getTagCondition(manganese))
				.setWeight(5)
				.setFailchance(.1f)
				.build(out, toRL("mineral/wolframite"));
		//todo
		//	Lapis
		//	Cinnabar
	}*/

	private void recipesMetalDevices(@Nonnull Consumer<FinishedRecipe> out)
	{

		/*shapedMisc(MetalDevices.CAPACITOR_EV)
				.pattern("waw")
				.pattern("fef")
				.pattern("wcw")
				.define('w', IETags.getItemTag(IETags.treatedWood))
				.define('f', IETags.getTagsFor(EnumMetals.STEEL).ingot)
				.define('a', IETags.getTagsFor(EnumMetals.ALUMINUM).plate)
				.define('c', IETags.hopGraphiteIngot)
				.define('e', new IngredientFluidStack(IETags.fluidRedstoneAcid, FluidType.BUCKET_VOLUME))
				.unlockedBy("has_nickel_ingot", has(IETags.getTagsFor(EnumMetals.NICKEL).ingot))
				.unlockedBy("has_steel_ingot", has(IETags.getTagsFor(EnumMetals.STEEL).ingot))
				.unlockedBy("has_treated_planks", has(IETags.getItemTag(IETags.treatedWood)))
				.save(out, toRL(toPath(MetalDevices.CAPACITOR_EV)));*/

	}

	private void recipesConnectors(@Nonnull Consumer<FinishedRecipe> out)
	{
/*
		shapedMisc(INBlocks.Connectors.TRANSFORMER_EV)
				.pattern("mh")
				.pattern("eb")
				.pattern("ii")
				.define('m', INBlocks.Connectors.getEnergyConnector(WireType.MV_CATEGORY, false))
				.define('h', INBlocks.Connectors.getEnergyConnector(WireType.HV_CATEGORY, false))
				.define('e', IEItems.Ingredients.COMPONENT_ELECTRONIC)
				.define('b', MetalDecoration.HV_COIL)
				.define('i', IETags.getTagsFor(EnumMetals.IRON).ingot)
				.unlockedBy("has_hv_connector", has(INBlocks.Connectors.getEnergyConnector(WireType.HV_CATEGORY, false)))
				.save(out, toRL(toPath(Connectors.TRANSFORMER_EV)));*/

	}

	private void add3x3Conversion(ItemLike bigItem, ItemLike smallItem, TagKey<Item> smallTag, Consumer<FinishedRecipe> out)
	{
		shapedMisc(bigItem)
				.define('s', smallTag)
				.define('i', smallItem)
				.pattern("sss")
				.pattern("sis")
				.pattern("sss")
				.unlockedBy("has_"+toPath(smallItem), has(smallItem))
				.save(out, toRL(toPath(smallItem)+"_to_")+toPath(bigItem));
		shapelessMisc(smallItem, 9)
				.requires(bigItem)
				.unlockedBy("has_"+toPath(bigItem), has(smallItem))
				.save(out, toRL(toPath(bigItem)+"_to_"+toPath(smallItem)));
	}

	private void addSlab(ItemLike block, ItemLike slab, Consumer<FinishedRecipe> out)
	{
		shapedMisc(slab, 6)
				.define('s', block)
				.pattern("sss")
				.unlockedBy("has_"+toPath(block), has(block))
				.save(out, toRL(toPath(block)+"_to_slab"));
		shapedMisc(block)
				.define('s', slab)
				.pattern("s")
				.pattern("s")
				.unlockedBy("has_"+toPath(block), has(block))
				.save(out, toRL(toPath(block)+"_from_slab"));
	}

	private void addStonecuttingRecipe(ItemLike input, ItemLike output, Consumer<FinishedRecipe> out)
	{
		addStonecuttingRecipe(input, output, 1, out);
	}

	private void addStonecuttingRecipe(ItemLike input, ItemLike output, int amount, Consumer<FinishedRecipe> out)
	{
		SingleItemRecipeBuilder.stonecutting(Ingredient.of(input), RecipeCategory.MISC, output, amount)
				.unlockedBy("has_"+toPath(input), has(input))
				.save(out, toRL("stonecutting/"+toPath(output)));
	}

	/**
	 * For Recipes like Coke or Blast Bricks, which use the same item in all corners, one for the sides and one for the middle
	 * Also work for shapes like TNT
	 *
	 * @param output the recipe's output
	 * @param corner the item in the corners
	 * @param side   the item on the sides
	 * @param middle the item in the middle
	 */
	@ParametersAreNonnullByDefault
	private void addCornerStraightMiddle(ItemLike output, int count, Ingredient corner, Ingredient side, Ingredient middle,
										 CriterionTriggerInstance condition, Consumer<FinishedRecipe> out)
	{
		shapedMisc(output, count)
				.define('c', corner)
				.define('s', side)
				.define('m', middle)
				.pattern("csc")
				.pattern("sms")
				.pattern("csc")
				.unlockedBy("has_item", condition)
				.save(out, toRL(toPath(output)));
	}

	/**
	 * For Recipes consisting of layers
	 *
	 * @param output the recipe's output
	 * @param top    the item on the top
	 * @param middle the item in the middle
	 * @param bottom the item on the bottom
	 */
	@ParametersAreNonnullByDefault
	private void addSandwich(ItemLike output, int count, Ingredient top, Ingredient middle, Ingredient bottom,
							 CriterionTriggerInstance condition, Consumer<FinishedRecipe> out)
	{
		shapedMisc(output, count)
				.define('t', top)
				.define('m', middle)
				.define('b', bottom)
				.pattern("ttt")
				.pattern("mmm")
				.pattern("bbb")
				.unlockedBy("has_item", condition)
				.save(out, toRL(toPath(output)));
	}

	private String toPath(ItemLike src)
	{
		return BuiltInRegistries.ITEM.getKey(src.asItem()).getPath();
	}

	private ResourceLocation toRL(String s)
	{
		if(!s.contains("/"))
			s = "crafting/"+s;
		if(PATH_COUNT.containsKey(s))
		{
			int count = PATH_COUNT.get(s)+1;
			PATH_COUNT.put(s, count);
			return new ResourceLocation(ImmersiveNuclear.MODID, s+count);
		}
		PATH_COUNT.put(s, 1);
		return new ResourceLocation(ImmersiveNuclear.MODID, s);
	}

	@Nonnull
	private Ingredient makeIngredient(ItemLike in)
	{
		return Ingredient.of(in);
	}

	@Nonnull
	private Ingredient makeIngredient(TagKey<Item> in)
	{
		return Ingredient.of(in);
	}

	@Nonnull
	private Ingredient makeIngredientFromBlock(TagKey<Block> in)
	{
		TagKey<Item> itemTag = INTags.getItemTag(in);
		return makeIngredient(itemTag);
	}

	public static ICondition getTagCondition(TagKey<?> tag)
	{
		// TODO I'm not convinved TagEmptyCondition is in a remotely working condition
		return new NotCondition(new TagEmptyCondition(tag.location()));
	}

	public static ICondition getTagCondition(ResourceLocation tag)
	{
		return getTagCondition(createItemWrapper(tag));
	}

	/**
	 * For smelting recipes that also have a blasting recipe, like ores
	 * keep the smelting postfix in mind when using this for non-ores or weird cases where the primary recipe for the ingot is not occupied by the smelting recipe
	 * has an overloaded method for regular use
	 *
	 * @param input        the recipe's input
	 * @param output       the recipe's output
	 * @param xp           experience awarded per smelted item
	 * @param smeltingTime smelting time in ticks
	 * @param extraPostfix adds an additional postfix before the smelting/blasting postfix when needed (for example used by dusts)
	 */
	private void addStandardSmeltingBlastingRecipe(ItemLike input, ItemLike output, float xp, int smeltingTime, Consumer<FinishedRecipe> out, String extraPostfix)
	{
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.MISC, output, xp, smeltingTime)
				.unlockedBy("has_"+toPath(input), has(input))
				.save(out, toRL("smelting/"+toPath(output)+extraPostfix));
		SimpleCookingRecipeBuilder.blasting(Ingredient.of(input), RecipeCategory.MISC, output, xp, smeltingTime/blastDivider)
				.unlockedBy("has_"+toPath(input), has(input))
				.save(out, toRL("smelting/"+toPath(output)+extraPostfix+"_from_blasting"));
	}

	private void addStandardSmeltingBlastingRecipe(ItemLike input, ItemLike output, float xp, Consumer<FinishedRecipe> out)
	{
		addStandardSmeltingBlastingRecipe(input, output, xp, out, "");
	}

	private void addStandardSmeltingBlastingRecipe(ItemLike input, ItemLike output, float xp, Consumer<FinishedRecipe> out, String extraPostfix)
	{
		addStandardSmeltingBlastingRecipe(input, output, xp, standardSmeltingTime, out, extraPostfix);
	}

	private void addRGBRecipe(Consumer<FinishedRecipe> out, ResourceLocation recipeName, Ingredient target, String nbtKey)
	{
		out.accept(new FinishedRecipe()
		{

			@Override
			public void serializeRecipeData(JsonObject json)
			{
				json.add("target", target.toJson());
				json.addProperty("key", nbtKey);
			}

			@Override
			public ResourceLocation getId()
			{
				return recipeName;
			}

			@Override
			public RecipeSerializer<?> getType()
			{
				return RecipeSerializers.RGB_SERIALIZER.get();
			}

			@Nullable
			@Override
			public JsonObject serializeAdvancement()
			{
				return null;
			}

			@Nullable
			@Override
			public ResourceLocation getAdvancementId()
			{
				return null;
			}
		});
	}

	private ShapedRecipeBuilder shapedMisc(ItemLike output) {
		return ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output);
	}

	private ShapedRecipeBuilder shapedMisc(ItemLike output, int count) {
		return ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output, count);
	}

	private ShapelessRecipeBuilder shapelessMisc(ItemLike output) {
		return ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, output);
	}

	private ShapelessRecipeBuilder shapelessMisc(ItemLike output, int count) {
		return ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, output, count);
	}
}
