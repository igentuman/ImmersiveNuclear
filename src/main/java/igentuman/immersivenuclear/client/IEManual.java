/*
 * BluSunrize
 * Copyright (c) 2020
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 *
 */

package igentuman.immersivenuclear.client;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.api.crafting.SqueezerRecipe;
import blusunrize.immersiveengineering.client.manual.IEManualInstance;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualInstance;
import blusunrize.lib.manual.ManualUtils;
import blusunrize.lib.manual.Tree.InnerNode;
import blusunrize.lib.manual.utils.ManualRecipeRef;
import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import igentuman.immersivenuclear.common.register.INFluids;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static igentuman.immersivenuclear.ImmersiveNuclear.MODID;

public class IEManual
{
	public static void initManual()
	{
		IEManualInstance ieMan = new IEManualInstance();
		ManualHelper.IE_MANUAL_INSTANCE.setValue(ieMan);
		ManualHelper.ADD_CONFIG_GETTER.setValue(e -> {
			synchronized(ieMan.configGetters)
			{
				ieMan.configGetters.add(e);
			}
		});

		/*ManualHelper.DYNAMIC_TABLES.put("squeezer", () -> formatToTable_ItemIntMap(
				SqueezerRecipe.getFluidValuesSorted(
						Minecraft.getInstance().level,
						INFluids.PLANTOIL.getStill(),
						true
				), "mB"
		));*/

	}

	public static void addIEManualEntries()
	{
		IEManualInstance ieMan = (IEManualInstance)ManualHelper.getManual();
		/*InnerNode<ResourceLocation, ManualEntry> generalCat = ieMan.getRoot().getOrCreateSubnode(new ResourceLocation(MODID,
				ManualHelper.CAT_GENERAL), 0);*/

		{
		//	ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(ManualHelper.getManual());
			//ieMan.addEntry(generalCat, builder.create(), ieMan.atOffsetFrom(generalCat, "graphite", -0.5));
		}

	}


	static Component[][] formatToTable_ItemIntMap(Map<Component, Integer> map, String valueType)
	{
		List<Entry<Component, Integer>> sortedMapArray = new ArrayList<>(map.entrySet());
		sortedMapArray.sort(Entry.comparingByValue());
		ArrayList<Component[]> list = new ArrayList<>();
		try
		{
			for(Entry<Component, Integer> entry : sortedMapArray)
			{
				Component item = entry.getKey();
				if(item==null)
					item = Component.nullToEmpty(entry.getKey().toString());

				int bt = entry.getValue();
				Component am = Component.nullToEmpty(bt+" "+valueType);
				list.add(new Component[]{item, am});
			}
		} catch(Exception e)
		{
		}
		return list.toArray(new Component[0][]);
	}

	static ManualRecipeRef[] collectRecipeStacksFromJSON(JsonObject json)
	{
		final ManualInstance manual = ManualHelper.getManual();
		ManualRecipeRef[] stacks;
		if(GsonHelper.isArrayNode(json, "recipes"))
		{
			JsonArray arr = json.get("recipes").getAsJsonArray();
			stacks = new ManualRecipeRef[arr.size()];
			for(int i = 0; i < stacks.length; ++i)
				stacks[i] = ManualUtils.getRecipeObjFromJson(manual, arr.get(i));
		}
		else
			stacks = new ManualRecipeRef[]{ManualUtils.getRecipeObjFromJson(manual, json.get("recipe"))};
		return stacks;
	}

	static Fluid[] collectRecipeFluidsFromJSON(JsonObject json)
	{
		Fluid[] stacks;
		if(GsonHelper.isArrayNode(json, "recipes"))
		{
			JsonArray arr = json.get("recipes").getAsJsonArray();
			stacks = new Fluid[arr.size()];
			for(int i = 0; i < stacks.length; ++i)
				stacks[i] = ForgeRegistries.FLUIDS.getValue(
						new ResourceLocation(GsonHelper.getAsString(arr.get(i).getAsJsonObject(), "fluid"))
				);
		}
		else
		{
			JsonElement recipe = json.get("recipe");
			Preconditions.checkArgument(recipe.isJsonObject());
			stacks = new Fluid[]{ForgeRegistries.FLUIDS.getValue(
					new ResourceLocation(GsonHelper.getAsString(recipe.getAsJsonObject(), "fluid"))
			)};
		}
		return stacks;
	}
}
