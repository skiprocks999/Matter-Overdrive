package matteroverdrive.core.datagen.utils;

import matteroverdrive.References;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelFile.ExistingModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ExistingLightableModel {

	public final ExistingModelFile off;
	public final ExistingModelFile on;
	
	public ExistingLightableModel(String name, ExistingFileHelper helper) {
		off = new ExistingModelFile(new ResourceLocation(References.ID + ":block/" + name), helper);
		on = new ExistingModelFile(new ResourceLocation(References.ID + ":block/" + name + "_on"), helper);
	}
	
}
