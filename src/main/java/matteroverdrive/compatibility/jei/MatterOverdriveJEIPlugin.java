package matteroverdrive.compatibility.jei;

import java.util.Objects;

import matteroverdrive.References;
import matteroverdrive.client.screen.ScreenCharger;
import matteroverdrive.client.screen.ScreenInscriber;
import matteroverdrive.client.screen.ScreenMatterAnalyzer;
import matteroverdrive.client.screen.ScreenMatterDecomposer;
import matteroverdrive.client.screen.ScreenMatterRecycler;
import matteroverdrive.client.screen.ScreenMatterReplicator;
import matteroverdrive.client.screen.ScreenMicrowave;
import matteroverdrive.client.screen.ScreenPatternMonitor;
import matteroverdrive.client.screen.ScreenPatternStorage;
import matteroverdrive.client.screen.ScreenSolarPanel;
import matteroverdrive.client.screen.ScreenSpacetimeAccelerator;
import matteroverdrive.client.screen.ScreenTransporter;
import matteroverdrive.common.recipe.RecipeInit;
import matteroverdrive.compatibility.jei.categories.item2item.specificmachines.RecipeCategoryInscriber;
import matteroverdrive.compatibility.jei.screenhandlers.types.ScreenHandlerCharger;
import matteroverdrive.compatibility.jei.screenhandlers.types.ScreenHandlerInscriber;
import matteroverdrive.compatibility.jei.screenhandlers.types.ScreenHandlerMatterAnalyzer;
import matteroverdrive.compatibility.jei.screenhandlers.types.ScreenHandlerMatterDecomposer;
import matteroverdrive.compatibility.jei.screenhandlers.types.ScreenHandlerMatterRecycler;
import matteroverdrive.compatibility.jei.screenhandlers.types.ScreenHandlerMatterReplicator;
import matteroverdrive.compatibility.jei.screenhandlers.types.ScreenHandlerMicrowave;
import matteroverdrive.compatibility.jei.screenhandlers.types.ScreenHandlerPatternMonitor;
import matteroverdrive.compatibility.jei.screenhandlers.types.ScreenHandlerPatternStorage;
import matteroverdrive.compatibility.jei.screenhandlers.types.ScreenHandlerSolarPanel;
import matteroverdrive.compatibility.jei.screenhandlers.types.ScreenHandlerSpacetimeAccelerator;
import matteroverdrive.compatibility.jei.screenhandlers.types.ScreenHandlerTransporter;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

@JeiPlugin
public class MatterOverdriveJEIPlugin implements IModPlugin {

	private static final ResourceLocation ID = new ResourceLocation(References.ID, "jei");

	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}
	
	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {

		registration.addRecipeCatalyst(RecipeCategoryInscriber.INPUT_MACHINE, RecipeCategoryInscriber.RECIPE_TYPE);
		
	}

	@Override
	//if you can think of a way to automate this go for it
	public void registerRecipes(IRecipeRegistration registration) {
		Minecraft mc = Minecraft.getInstance();
		ClientLevel world = Objects.requireNonNull(mc.level);
		RecipeManager recipeManager = world.getRecipeManager();

		
		// Electric Furnace
		//List<SmeltingRecipe> electricFurnaceRecipes = recipeManager.getAllRecipesFor(RecipeType.SMELTING);
		//registration.addRecipes(ElectricFurnaceRecipeCategory.RECIPE_TYPE, electricFurnaceRecipes);

		registration.addRecipes(RecipeCategoryInscriber.RECIPE_TYPE, recipeManager.getAllRecipesFor(RecipeInit.INSCRIBER_TYPE.get()));

		
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {

		IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

		registration.addRecipeCategories(new RecipeCategoryInscriber(guiHelper));
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registry) {
		
		registry.addGuiContainerHandler(ScreenCharger.class, new ScreenHandlerCharger());
		registry.addGuiContainerHandler(ScreenInscriber.class, new ScreenHandlerInscriber());
		registry.addGuiContainerHandler(ScreenMatterDecomposer.class, new ScreenHandlerMatterDecomposer());
		registry.addGuiContainerHandler(ScreenMatterRecycler.class, new ScreenHandlerMatterRecycler());
		registry.addGuiContainerHandler(ScreenMicrowave.class, new ScreenHandlerMicrowave());
		registry.addGuiContainerHandler(ScreenSolarPanel.class, new ScreenHandlerSolarPanel());
		registry.addGuiContainerHandler(ScreenTransporter.class, new ScreenHandlerTransporter());
		registry.addGuiContainerHandler(ScreenSpacetimeAccelerator.class, new ScreenHandlerSpacetimeAccelerator());
		registry.addGuiContainerHandler(ScreenPatternStorage.class, new ScreenHandlerPatternStorage());
		registry.addGuiContainerHandler(ScreenPatternMonitor.class, new ScreenHandlerPatternMonitor());
		registry.addGuiContainerHandler(ScreenMatterReplicator.class, new ScreenHandlerMatterReplicator());
		registry.addGuiContainerHandler(ScreenMatterAnalyzer.class, new ScreenHandlerMatterAnalyzer());
		
		registry.addRecipeClickArea(ScreenInscriber.class, 33, 48, 22, 15, RecipeCategoryInscriber.RECIPE_TYPE);
	
	}

}
