package matteroverdrive.compatibility.jei.categories.pseudo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import matteroverdrive.References;
import matteroverdrive.client.ClientReferences.Colors;
import matteroverdrive.common.tile.TileMatterDecomposer;
import matteroverdrive.compatibility.jei.categories.base.AbstractOverdriveRecipeCategory;
import matteroverdrive.compatibility.jei.utils.IPseudoRecipe;
import matteroverdrive.compatibility.jei.utils.gui.ScreenObjectWrapper;
import matteroverdrive.compatibility.jei.utils.gui.ScreenObjectWrapper.JeiTexture;
import matteroverdrive.compatibility.jei.utils.gui.arrows.animated.ArrowRightAnimatedWrapper;
import matteroverdrive.compatibility.jei.utils.gui.backgroud.OverdriveBackgroundManager;
import matteroverdrive.compatibility.jei.utils.gui.item.DefaultItemSlotWrapper;
import matteroverdrive.compatibility.jei.utils.label.types.LabelWrapperFailureChance;
import matteroverdrive.compatibility.jei.utils.label.types.LabelWrapperMatterDecomposer;
import matteroverdrive.compatibility.jei.utils.label.types.LabelWrapperPowerConsumed;
import matteroverdrive.compatibility.jei.utils.label.types.LabelWrapperPowerUsage;
import matteroverdrive.compatibility.jei.utils.label.types.LabelWrapperProcessTime;
import matteroverdrive.core.matter.MatterRegister;
import matteroverdrive.core.screen.component.ScreenComponentSlot.SlotType;
import matteroverdrive.core.utils.UtilsNbt;
import matteroverdrive.registry.BlockRegistry;
import matteroverdrive.registry.ItemRegistry;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class RecipeCategoryMatterDecomposer extends AbstractOverdriveRecipeCategory<matteroverdrive.compatibility.jei.categories.pseudo.RecipeCategoryMatterDecomposer.PseudoRecipeMatterDecomposer> {
	
	// JEI Window Parameters
	private static final ScreenObjectWrapper BACK_WRAP = new ScreenObjectWrapper(JeiTexture.VANILLA_BACKGROUND, 0, 0, 0, 0, 132, 72);
	private static final OverdriveBackgroundManager MANAGER = new OverdriveBackgroundManager(JeiTexture.OVERDRIVE_BACKGROUND_BAR, 72, 0, 0);
	
	private static final DefaultItemSlotWrapper INPUT_SLOT = new DefaultItemSlotWrapper(SlotType.MAIN, 7, 12, false);
	private static final DefaultItemSlotWrapper OUTPUT_SLOT = new DefaultItemSlotWrapper(SlotType.SMALL, 65, 14, false);

	private static final ArrowRightAnimatedWrapper ANIM_ARROW = new ArrowRightAnimatedWrapper(35, 15);
	
	private static final LabelWrapperPowerUsage POWER_USE = new LabelWrapperPowerUsage(Colors.HOLO_RED.getColor(), 50, 45);
	private static final LabelWrapperPowerConsumed POWER_CONSUMED = new LabelWrapperPowerConsumed(Colors.HOLO_RED.getColor(), 50, 56);
	private static final LabelWrapperMatterDecomposer COMPOSER = new LabelWrapperMatterDecomposer(50, 34);
	private static final LabelWrapperProcessTime PROCESS_TIME = new LabelWrapperProcessTime(Colors.HOLO.getColor(), 89, 24);
	private static final LabelWrapperFailureChance FAILURE = new LabelWrapperFailureChance(Colors.MATTER.getColor(), 89, 12, TileMatterDecomposer.FAILURE_CHANCE);
	
	private static final int ANIM_TIME = 50;

	public static final ItemStack INPUT_MACHINE = new ItemStack(BlockRegistry.BLOCK_MATTER_DECOMPOSER.get());
	
	public static final RecipeType<PseudoRecipeMatterDecomposer> RECIPE_TYPE = new RecipeType<>(new ResourceLocation(References.ID, "matter_decomposer"), PseudoRecipeMatterDecomposer.class);
	
	public RecipeCategoryMatterDecomposer(IGuiHelper guiHelper) {
		super(guiHelper, INPUT_MACHINE, BACK_WRAP, ANIM_TIME);
		
		setBackgroundExtra(guiHelper, MANAGER.getBackgroundExtras());
		setInputSlots(guiHelper, INPUT_SLOT);
		setOutputSlots(guiHelper, OUTPUT_SLOT);
		setAnimatedArrows(guiHelper, ANIM_ARROW);
		setLabels(POWER_USE, POWER_CONSUMED, PROCESS_TIME, COMPOSER, FAILURE);
		
	}
	
	@Override
	public List<List<ItemStack>> getItemInputs(PseudoRecipeMatterDecomposer recipe) {
		return Arrays.asList(Arrays.asList(recipe.input()));
	}
	
	@Override
	public List<ItemStack> getItemOutputs(PseudoRecipeMatterDecomposer recipe){
		ItemStack stack = recipe.output();
		UtilsNbt.writeMatterVal(stack, recipe.value());
		return Arrays.asList(stack);
	}
	
	@Override
	public RecipeType<PseudoRecipeMatterDecomposer> getRecipeType() {
		return RECIPE_TYPE;
	}

	public static List<PseudoRecipeMatterDecomposer> getPseudoReipes() {
		
		List<PseudoRecipeMatterDecomposer> recipes = new ArrayList<>();
		
		MatterRegister.INSTANCE.CLIENT_VALUES.forEach((item, value) -> {
			recipes.add(new PseudoRecipeMatterDecomposer(new ItemStack(item), new ItemStack(ItemRegistry.ITEM_RAW_MATTER_DUST.get()), value));
		});
		
		return recipes;
		
	}
	
	public static record PseudoRecipeMatterDecomposer(ItemStack input, ItemStack output, double value) implements IPseudoRecipe {

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
