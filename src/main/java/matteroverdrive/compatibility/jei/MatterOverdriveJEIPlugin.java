package matteroverdrive.compatibility.jei;

import java.util.Objects;

import matteroverdrive.References;
import matteroverdrive.client.screen.ScreenCharger;
import matteroverdrive.client.screen.ScreenDiscManipulator;
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
import matteroverdrive.compatibility.jei.categories.pseudo.RecipeCategoryMatterDecomposer;
import matteroverdrive.compatibility.jei.categories.pseudo.RecipeCategoryMatterRecycler;
import matteroverdrive.compatibility.jei.categories.vanillacooking.specificmachines.RecipeCategoryMicrowave;
import matteroverdrive.compatibility.jei.screenhandlers.types.ScreenHandlerCharger;
import matteroverdrive.compatibility.jei.screenhandlers.types.ScreenHandlerDiscManipulator;
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
import net.minecraft.world.item.crafting.RecipeType;

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
		registration.addRecipeCatalyst(RecipeCategoryMicrowave.INPUT_MACHINE, RecipeCategoryMicrowave.RECIPE_TYPE);
		registration.addRecipeCatalyst(RecipeCategoryMatterRecycler.INPUT_MACHINE, RecipeCategoryMatterRecycler.RECIPE_TYPE);
		registration.addRecipeCatalyst(RecipeCategoryMatterDecomposer.INPUT_MACHINE, RecipeCategoryMatterDecomposer.RECIPE_TYPE);
		
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		Minecraft mc = Minecraft.getInstance();
		ClientLevel world = Objects.requireNonNull(mc.level);
		RecipeManager recipeManager = world.getRecipeManager();

		registration.addRecipes(RecipeCategoryInscriber.RECIPE_TYPE, recipeManager.getAllRecipesFor(RecipeInit.INSCRIBER_TYPE.get()));
		registration.addRecipes(RecipeCategoryMicrowave.RECIPE_TYPE, recipeManager.getAllRecipesFor(RecipeType.SMOKING));
		registration.addRecipes(RecipeCategoryMatterRecycler.RECIPE_TYPE, RecipeCategoryMatterRecycler.getPseudoReipes());
		registration.addRecipes(RecipeCategoryMatterDecomposer.RECIPE_TYPE, RecipeCategoryMatterDecomposer.getPseudoReipes());
		
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {

		IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

		registration.addRecipeCategories(new RecipeCategoryInscriber(guiHelper));
		registration.addRecipeCategories(new RecipeCategoryMicrowave(guiHelper));
		registration.addRecipeCategories(new RecipeCategoryMatterRecycler(guiHelper));
		registration.addRecipeCategories(new RecipeCategoryMatterDecomposer(guiHelper));
		
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
		registry.addGuiContainerHandler(ScreenDiscManipulator.class, new ScreenHandlerDiscManipulator());
		
		registry.addRecipeClickArea(ScreenInscriber.class, 33, 48, 22, 15, RecipeCategoryInscriber.RECIPE_TYPE);
		registry.addRecipeClickArea(ScreenMicrowave.class, 33, 48, 22, 15, RecipeCategoryMicrowave.RECIPE_TYPE);
		registry.addRecipeClickArea(ScreenMatterRecycler.class, 33, 48, 22, 15, RecipeCategoryMatterRecycler.RECIPE_TYPE);
		registry.addRecipeClickArea(ScreenMatterDecomposer.class, 33, 48, 22, 15, RecipeCategoryMatterDecomposer.RECIPE_TYPE);
	
	}

}
