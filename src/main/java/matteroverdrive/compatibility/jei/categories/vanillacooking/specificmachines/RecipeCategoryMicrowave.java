package matteroverdrive.compatibility.jei.categories.vanillacooking.specificmachines;

import matteroverdrive.References;
import matteroverdrive.client.ClientReferences.Colors;
import matteroverdrive.common.tile.TileMicrowave;
import matteroverdrive.compatibility.jei.categories.vanillacooking.CookingRecipeCategory;
import matteroverdrive.compatibility.jei.utils.gui.ScreenObjectWrapper;
import matteroverdrive.compatibility.jei.utils.gui.ScreenObjectWrapper.JeiTexture;
import matteroverdrive.compatibility.jei.utils.gui.arrows.animated.ArrowRightAnimatedWrapper;
import matteroverdrive.compatibility.jei.utils.gui.backgroud.OverdriveBackgroundManager;
import matteroverdrive.compatibility.jei.utils.gui.item.DefaultItemSlotWrapper;
import matteroverdrive.compatibility.jei.utils.label.types.LabelWrapperPowerConsumed;
import matteroverdrive.compatibility.jei.utils.label.types.LabelWrapperPowerUsage;
import matteroverdrive.compatibility.jei.utils.label.types.LabelWrapperProcessTime;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import matteroverdrive.registry.BlockRegistry;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmokingRecipe;

public class RecipeCategoryMicrowave extends CookingRecipeCategory<SmokingRecipe> {

	// JEI Window Parameters
	private static final ScreenObjectWrapper BACK_WRAP = new ScreenObjectWrapper(JeiTexture.VANILLA_BACKGROUND, 0, 0, 0, 0, 132, 72);
	private static final OverdriveBackgroundManager MANAGER = new OverdriveBackgroundManager(JeiTexture.OVERDRIVE_BACKGROUND_BAR, 72, 0, 0);
	
	private static final DefaultItemSlotWrapper INPUT_SLOT = new DefaultItemSlotWrapper(SlotType.MAIN, 7, 12, false);
	private static final DefaultItemSlotWrapper OUTPUT_SLOT = new DefaultItemSlotWrapper(SlotType.BIG, 65, 12, false);

	private static final ArrowRightAnimatedWrapper ANIM_ARROW = new ArrowRightAnimatedWrapper(35, 15);
	
	private static final LabelWrapperPowerUsage POWER_USE = new LabelWrapperPowerUsage(Colors.HOLO_RED.getColor(), 50, 40).setConstUsage(TileMicrowave.USAGE_PER_TICK);
	private static final LabelWrapperPowerConsumed POWER_CONSUMED = new LabelWrapperPowerConsumed(Colors.HOLO_RED.getColor(), 50, 52).setConstUsage(TileMicrowave.USAGE_PER_TICK);
	private static final LabelWrapperProcessTime PROCESS_TIME = new LabelWrapperProcessTime(Colors.HOLO.getColor(), 89, 19);

	private static final int ANIM_TIME = 50;

	public static final ItemStack INPUT_MACHINE = new ItemStack(BlockRegistry.BLOCK_MICROWAVE.get());
	
	public static final RecipeType<SmokingRecipe> RECIPE_TYPE = new RecipeType<>(new ResourceLocation(References.ID, "microwave"), SmokingRecipe.class);
	
	public RecipeCategoryMicrowave(IGuiHelper guiHelper) {
		super(guiHelper, INPUT_MACHINE, BACK_WRAP, ANIM_TIME);
		
		setBackgroundExtra(guiHelper, MANAGER.getBackgroundExtras());
		setInputSlots(guiHelper, INPUT_SLOT);
		setOutputSlots(guiHelper, OUTPUT_SLOT);
		setAnimatedArrows(guiHelper, ANIM_ARROW);
		setLabels(POWER_USE, POWER_CONSUMED, PROCESS_TIME);
		
	}

	@Override
	public RecipeType<SmokingRecipe> getRecipeType() {
		return RECIPE_TYPE;
	}

}
