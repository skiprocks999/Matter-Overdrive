package matteroverdrive.compatibility.jei.categories.pseudo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import matteroverdrive.References;
import matteroverdrive.common.tile.TileMatterDecomposer;
import matteroverdrive.compatibility.jei.categories.base.AbstractOverdriveRecipeCategory;
import matteroverdrive.compatibility.jei.utils.IPseudoRecipe;
import matteroverdrive.compatibility.jei.utils.gui.ScreenObjectWrapper;
import matteroverdrive.compatibility.jei.utils.gui.ScreenObjectWrapper.JeiTexture;
import matteroverdrive.compatibility.jei.utils.gui.arrows.animated.ArrowRightAnimatedWrapper;
import matteroverdrive.compatibility.jei.utils.gui.backgroud.OverdriveBackgroundManager;
import matteroverdrive.compatibility.jei.utils.gui.item.DefaultItemSlotWrapper;
import matteroverdrive.compatibility.jei.utils.label.types.PowerConsumedLabelWrapper;
import matteroverdrive.compatibility.jei.utils.label.types.PowerUsageLabelWrapper;
import matteroverdrive.compatibility.jei.utils.label.types.ProcessTimeLabelWrapper;
import matteroverdrive.core.matter.MatterRegister;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import matteroverdrive.core.utils.UtilsRendering;
import matteroverdrive.registry.BlockRegistry;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class RecipeCategoryMatterDecomposer extends AbstractOverdriveRecipeCategory<matteroverdrive.compatibility.jei.categories.pseudo.RecipeCategoryMatterDecomposer.PseudoRecipeMatterDecomposer> {
	
	// JEI Window Parameters
	private static final ScreenObjectWrapper BACK_WRAP = new ScreenObjectWrapper(JeiTexture.VANILLA_BACKGROUND, 0, 0, 0, 0, 132, 72);
	private static final OverdriveBackgroundManager MANAGER = new OverdriveBackgroundManager(JeiTexture.OVERDRIVE_BACKGROUND_BAR, 72, 0, 0);
	
	private static final DefaultItemSlotWrapper INPUT_SLOT = new DefaultItemSlotWrapper(SlotType.MAIN, 7, 12, false);

	private static final ArrowRightAnimatedWrapper ANIM_ARROW = new ArrowRightAnimatedWrapper(35, 15);
	
	private static final PowerUsageLabelWrapper POWER_USE = new PowerUsageLabelWrapper(UtilsRendering.HOLO_RED, 50, 40);
	private static final PowerConsumedLabelWrapper POWER_CONSUMED = new PowerConsumedLabelWrapper(UtilsRendering.HOLO_RED, 50, 52);
	private static final ProcessTimeLabelWrapper PROCESS_TIME = new ProcessTimeLabelWrapper(UtilsRendering.TEXT_BLUE, 89, 19);

	private static final int ANIM_TIME = 50;

	public static final ItemStack INPUT_MACHINE = new ItemStack(BlockRegistry.BLOCK_MATTER_DECOMPOSER.get());
	
	public static final RecipeType<PseudoRecipeMatterDecomposer> RECIPE_TYPE = new RecipeType<>(new ResourceLocation(References.ID, "matter_decomposer"), PseudoRecipeMatterDecomposer.class);
	
	public RecipeCategoryMatterDecomposer(IGuiHelper guiHelper) {
		super(guiHelper, INPUT_MACHINE, BACK_WRAP, ANIM_TIME);
		// TODO Auto-generated constructor stub
	}
	
	public List<List<ItemStack>> getItemInputs(PseudoRecipeMatterDecomposer recipe) {
		return Arrays.asList(Arrays.asList(recipe.input));
	}
	
	@Override
	public RecipeType<PseudoRecipeMatterDecomposer> getRecipeType() {
		return RECIPE_TYPE;
	}

	public static List<PseudoRecipeMatterDecomposer> getPseudoReipes() {
		
		List<PseudoRecipeMatterDecomposer> recipes = new ArrayList<>();
		
		MatterRegister.INSTANCE.CLIENT_VALUES.forEach((item, value) -> {
			recipes.add(new PseudoRecipeMatterDecomposer(new ItemStack(item), value));
		});
		
		return recipes;
		
	}
	
	public static record PseudoRecipeMatterDecomposer(ItemStack input, double value) implements IPseudoRecipe {

		@Override
		public double getUsagePerTick() {
			return TileMatterDecomposer.USAGE_PER_TICK;
		}

		@Override
		public double getProcessTime() {
			return TileMatterDecomposer.OPERATING_TIME;
		}
		
	}

}
