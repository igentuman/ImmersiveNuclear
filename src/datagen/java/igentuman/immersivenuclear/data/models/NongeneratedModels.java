/*
 * BluSunrize
 * Copyright (c) 2022
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package igentuman.immersivenuclear.data.models;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import static igentuman.immersivenuclear.api.Lib.MODID;

public class NongeneratedModels extends ModelProvider<NongeneratedModels.NongeneratedModel> {

	public NongeneratedModels(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, MODID, "block", NongeneratedModel::new, existingFileHelper);
	}

	protected void registerModels() {
	}

	public String getName() {
		return "Non-generated models";
	}

	public static class NongeneratedModel extends ModelBuilder<NongeneratedModel> {
		protected NongeneratedModel(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
			super(outputLocation, existingFileHelper);
		}
	}
}