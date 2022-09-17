package matteroverdrive.compatibility.jei.categories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.vertex.PoseStack;

import matteroverdrive.common.recipe.AbstractOverdriveRecipe;
import matteroverdrive.compatibility.jei.utils.gui.ScreenObjectWrapper;
import matteroverdrive.compatibility.jei.utils.gui.arrows.animated.ArrowAnimatedWrapper;
import matteroverdrive.compatibility.jei.utils.gui.fluid.GenericFluidGaugeWrapper;
import matteroverdrive.compatibility.jei.utils.gui.item.GenericItemSlotWrapper;
import matteroverdrive.compatibility.jei.utils.label.BiproductPercentWrapper;
import matteroverdrive.compatibility.jei.utils.label.GenericLabelWrapper;
import matteroverdrive.core.utils.UtilsText;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public abstract class OverdriveRecipeCategory<T extends AbstractOverdriveRecipe> implements IRecipeCategory<T> {

	private int ANIMATION_LENGTH;

	private IDrawable BACKGROUND;
	private IDrawable ICON;

	private ItemStack ICON_STACK;

	public GenericLabelWrapper[] LABELS;

	public int itemBiLabelFirstIndex;
	public int fluidBiLabelFirstIndex;

	private LoadingCache<Integer, List<IDrawableStatic>> BACKGROUND_EXTRA;
	private LoadingCache<Integer, List<IDrawableAnimated>> ANIMATED_ARROWS;
	private LoadingCache<Integer, List<IDrawableStatic>> STATIC_ARROWS;
	private LoadingCache<Integer, List<IDrawableStatic>> INPUT_SLOTS;
	private LoadingCache<Integer, List<IDrawableStatic>> OUTPUT_SLOTS;
	private LoadingCache<Integer, List<IDrawableStatic>> FLUID_INPUTS;
	private LoadingCache<Integer, List<IDrawableStatic>> FLUID_OUTPUTS;

	private GenericItemSlotWrapper[] inSlots = new GenericItemSlotWrapper[0];
	private GenericItemSlotWrapper[] outSlots = new GenericItemSlotWrapper[0];
	private GenericFluidGaugeWrapper[] fluidInputs = new GenericFluidGaugeWrapper[0];
	private GenericFluidGaugeWrapper[] fluidOutputs = new GenericFluidGaugeWrapper[0];
	private ArrowAnimatedWrapper[] animArrows = new ArrowAnimatedWrapper[0];
	private ScreenObjectWrapper[] staticArrows = new ScreenObjectWrapper[0];
	private ScreenObjectWrapper[] backgroundExtra = new ScreenObjectWrapper[0];

	public OverdriveRecipeCategory(IGuiHelper guiHelper, ResourceLocation loc, ItemStack inputMachine,
			ScreenObjectWrapper wrapper, int animationTime) {

		ICON = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, inputMachine);
		BACKGROUND = guiHelper.createDrawable(wrapper.getTexture(), wrapper.getTextX(), wrapper.getTextY(),
				wrapper.getLength(), wrapper.getWidth());

		ANIMATION_LENGTH = animationTime;
	}

	@Override
	public Component getTitle() {
		return UtilsText.jeiTranslated(getRecipeType().getUid().getPath());
	}

	@Override
	public IDrawable getBackground() {
		return BACKGROUND;
	}

	@Override
	public IDrawable getIcon() {
		return ICON;
	}

	@Override
	public void draw(T recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
		drawBackgroundExtras(matrixStack);
		drawInputSlots(matrixStack);
		drawOutputSlots(matrixStack);
		drawFluidInputs(matrixStack);
		drawFluidOutputs(matrixStack);
		drawStaticArrows(matrixStack);
		drawAnimatedArrows(matrixStack);

		addDescriptions(matrixStack, recipe);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
		setItemInputs(getItemInputs(recipe), builder);
		setFluidInputs(getFluidInputs(recipe), builder);
		setItemOutputs(getItemOutputs(recipe), builder);
		setFluidOutputs(getFluidOutputs(recipe), builder);
	}

	public int getAnimationTime() {
		return ANIMATION_LENGTH;
	}

	public ItemStack getIconMachine() {
		return ICON_STACK;
	}

	public void addDescriptions(PoseStack stack, T recipe) {
		if (LABELS != null) {
			Font fontRenderer = Minecraft.getInstance().font;
			for (GenericLabelWrapper wrap : LABELS) {
				fontRenderer.draw(stack, wrap.getComponent(this, recipe), wrap.getXPos(), wrap.getYPos(),
						wrap.getColor());
			}
		}
	}

	public void setLabels(GenericLabelWrapper... labels) {
		LABELS = labels;
		GenericLabelWrapper wrap;
		boolean firstItemBi = false;
		for (int i = 0; i < labels.length; i++) {
			wrap = labels[i];
			if (!firstItemBi && wrap instanceof BiproductPercentWrapper) {
				this.itemBiLabelFirstIndex = i;
				firstItemBi = true;
			}
		}

	}

	public void setInputSlots(IGuiHelper guiHelper, GenericItemSlotWrapper... inputSlots) {
		inSlots = inputSlots;
		INPUT_SLOTS = CacheBuilder.newBuilder().maximumSize(inputSlots.length)
				.build(new CacheLoader<Integer, List<IDrawableStatic>>() {
					@Override
					public List<IDrawableStatic> load(Integer time) {
						List<IDrawableStatic> slots = new ArrayList<>();
						for (ScreenObjectWrapper slot : inputSlots) {
							slots.add(guiHelper
									.drawableBuilder(slot.getTexture(), slot.getTextX(), slot.getTextY(),
											slot.getLength(), slot.getWidth())
									.setTextureSize(slot.getTextureWidth(), slot.getTextureHeight()).build());
						}
						return slots;
					}
				});
	}

	public void setOutputSlots(IGuiHelper guiHelper, GenericItemSlotWrapper... outputSlots) {
		outSlots = outputSlots;
		OUTPUT_SLOTS = CacheBuilder.newBuilder().maximumSize(outputSlots.length)
				.build(new CacheLoader<Integer, List<IDrawableStatic>>() {
					@Override
					public List<IDrawableStatic> load(Integer time) {
						List<IDrawableStatic> slots = new ArrayList<>();
						for (ScreenObjectWrapper slot : outputSlots) {
							slots.add(guiHelper
									.drawableBuilder(slot.getTexture(), slot.getTextX(), slot.getTextY(),
											slot.getLength(), slot.getWidth())
									.setTextureSize(slot.getTextureWidth(), slot.getTextureHeight()).build());
						}
						return slots;
					}
				});
	}

	public void setFluidInputs(IGuiHelper guiHelper, GenericFluidGaugeWrapper... gauges) {
		fluidInputs = gauges;
		FLUID_INPUTS = CacheBuilder.newBuilder().maximumSize(fluidInputs.length)
				.build(new CacheLoader<Integer, List<IDrawableStatic>>() {
					@Override
					public List<IDrawableStatic> load(Integer time) {
						List<IDrawableStatic> gauges = new ArrayList<>();
						for (ScreenObjectWrapper gauge : fluidInputs) {
							gauges.add(guiHelper
									.drawableBuilder(gauge.getTexture(), gauge.getTextX(), gauge.getTextY(),
											gauge.getLength(), gauge.getWidth())
									.setTextureSize(gauge.getTextureWidth(), gauge.getTextureHeight()).build());
						}
						return gauges;
					}
				});
	}

	public void setFluidOutputs(IGuiHelper guiHelper, GenericFluidGaugeWrapper... gauges) {
		fluidOutputs = gauges;
		FLUID_OUTPUTS = CacheBuilder.newBuilder().maximumSize(fluidOutputs.length)
				.build(new CacheLoader<Integer, List<IDrawableStatic>>() {
					@Override
					public List<IDrawableStatic> load(Integer time) {
						List<IDrawableStatic> gauges = new ArrayList<>();
						for (ScreenObjectWrapper gauge : fluidOutputs) {
							gauges.add(guiHelper
									.drawableBuilder(gauge.getTexture(), gauge.getTextX(), gauge.getTextY(),
											gauge.getLength(), gauge.getWidth())
									.setTextureSize(gauge.getTextureWidth(), gauge.getTextureHeight()).build());
						}
						return gauges;
					}
				});
	}

	public void setStaticArrows(IGuiHelper guiHelper, ScreenObjectWrapper... arrows) {
		if (staticArrows.length == 0) {
			staticArrows = arrows;
		} else {
			staticArrows = ArrayUtils.addAll(staticArrows, arrows);
		}
		STATIC_ARROWS = CacheBuilder.newBuilder().maximumSize(staticArrows.length)
				.build(new CacheLoader<Integer, List<IDrawableStatic>>() {
					@Override
					public List<IDrawableStatic> load(Integer time) {
						List<IDrawableStatic> arrows = new ArrayList<>();
						for (ScreenObjectWrapper arrow : staticArrows) {
							arrows.add(guiHelper
									.drawableBuilder(arrow.getTexture(), arrow.getTextX(), arrow.getTextY(),
											arrow.getLength(), arrow.getWidth())
									.setTextureSize(arrow.getTextureWidth(), arrow.getTextureHeight()).build());
						}
						return arrows;
					}
				});
	}

	public void setBackgroundExtra(IGuiHelper guiHelper, ScreenObjectWrapper... extra) {
		backgroundExtra = extra;
		BACKGROUND_EXTRA = CacheBuilder.newBuilder().maximumSize(backgroundExtra.length)
				.build(new CacheLoader<Integer, List<IDrawableStatic>>() {
					@Override
					public List<IDrawableStatic> load(Integer time) {
						List<IDrawableStatic> extras = new ArrayList<>();
						for (ScreenObjectWrapper extra : backgroundExtra) {
							extras.add(guiHelper
									.drawableBuilder(extra.getTexture(), extra.getTextX(), extra.getTextY(),
											extra.getLength(), extra.getWidth())
									.setTextureSize(extra.getTextureWidth(), extra.getTextureHeight()).build());
						}
						return extras;
					}
				});
	}

	public void setAnimatedArrows(IGuiHelper guiHelper, ArrowAnimatedWrapper... arrows) {
		ScreenObjectWrapper[] temp = new ScreenObjectWrapper[arrows.length];
		for (int i = 0; i < arrows.length; i++) {
			temp[i] = arrows[i].getStaticArrow();
		}
		setStaticArrows(guiHelper, temp);
		animArrows = arrows;
		ANIMATED_ARROWS = CacheBuilder.newBuilder().maximumSize(animArrows.length)
				.build(new CacheLoader<Integer, List<IDrawableAnimated>>() {
					@Override
					public List<IDrawableAnimated> load(Integer time) {
						List<IDrawableAnimated> arrows = new ArrayList<>();
						for (ArrowAnimatedWrapper arrow : animArrows) {
							arrows.add(guiHelper
									.drawableBuilder(arrow.getTexture(), arrow.getTextX(), arrow.getTextY(),
											arrow.getLength(), arrow.getWidth())
									.setTextureSize(arrow.getTextureWidth(), arrow.getTextureHeight())
									.buildAnimated(time, arrow.getStartDirection(), false));
						}

						return arrows;
					}
				});
	}

	public void setItemInputs(List<List<ItemStack>> inputs, IRecipeLayoutBuilder builder) {
		GenericItemSlotWrapper wrapper;
		RecipeIngredientRole role;
		for (int i = 0; i < inSlots.length; i++) {
			wrapper = inSlots[i];
			role = wrapper.isVisibleOnly() ? RecipeIngredientRole.RENDER_ONLY : RecipeIngredientRole.INPUT;
			builder.addSlot(role, wrapper.itemXStart(), wrapper.itemYStart()).addItemStacks(inputs.get(i));
		}
	}

	public void setItemOutputs(List<ItemStack> outputs, IRecipeLayoutBuilder builder) {
		GenericItemSlotWrapper wrapper;
		RecipeIngredientRole role;
		for (int i = 0; i < outSlots.length; i++) {
			wrapper = outSlots[i];
			role = wrapper.isVisibleOnly() ? RecipeIngredientRole.RENDER_ONLY : RecipeIngredientRole.OUTPUT;
			if (i < outputs.size()) {
				builder.addSlot(role, wrapper.itemXStart(), wrapper.itemYStart()).addItemStack(outputs.get(i));
			}
		}
	}

	public void setFluidInputs(List<List<FluidStack>> inputs, IRecipeLayoutBuilder builder) {
		GenericFluidGaugeWrapper wrapper;
		RecipeIngredientRole role = RecipeIngredientRole.INPUT;
		FluidStack stack;
		for (int i = 0; i < fluidInputs.length; i++) {
			wrapper = fluidInputs[i];
			stack = inputs.get(i).get(0);
			int height = (int) Math
					.ceil(stack.getAmount() / (float) wrapper.getAmount() * wrapper.getFluidTextHeight());
			builder.addSlot(role, wrapper.getFluidXPos(), wrapper.getFluidYPos() - height)
					.setFluidRenderer(stack.getAmount(), false, wrapper.getFluidTextWidth(), height)
					.addIngredients(ForgeTypes.FLUID_STACK, inputs.get(i));
		}
	}

	public void setFluidOutputs(List<FluidStack> outputs, IRecipeLayoutBuilder builder) {
		GenericFluidGaugeWrapper wrapper;
		RecipeIngredientRole role = RecipeIngredientRole.OUTPUT;
		FluidStack stack;
		for (int i = 0; i < fluidOutputs.length; i++) {
			wrapper = fluidOutputs[i];
			stack = outputs.get(i);
			int height = (int) Math
					.ceil(stack.getAmount() / (float) wrapper.getAmount() * wrapper.getFluidTextHeight());
			builder.addSlot(role, wrapper.getFluidXPos(), wrapper.getFluidYPos() - height)
					.setFluidRenderer(stack.getAmount(), false, wrapper.getFluidTextWidth(), height)
					.addIngredient(ForgeTypes.FLUID_STACK, stack);
		}
	}

	public void drawInputSlots(PoseStack matrixStack) {
		if (INPUT_SLOTS != null) {
			List<IDrawableStatic> inputSlots = INPUT_SLOTS.getUnchecked(getAnimationTime());
			IDrawableStatic image;
			ScreenObjectWrapper wrapper;
			for (int i = 0; i < inputSlots.size(); i++) {
				image = inputSlots.get(i);
				wrapper = inSlots[i];
				image.draw(matrixStack, wrapper.getXPos(), wrapper.getYPos());
			}
		}
	}

	public void drawOutputSlots(PoseStack matrixStack) {
		if (OUTPUT_SLOTS != null) {
			List<IDrawableStatic> outputSlots = OUTPUT_SLOTS.getUnchecked(getAnimationTime());
			IDrawableStatic image;
			ScreenObjectWrapper wrapper;
			for (int i = 0; i < outputSlots.size(); i++) {
				image = outputSlots.get(i);
				wrapper = outSlots[i];
				image.draw(matrixStack, wrapper.getXPos(), wrapper.getYPos());
			}
		}
	}

	public void drawFluidInputs(PoseStack matrixStack) {
		if (FLUID_INPUTS != null) {
			List<IDrawableStatic> inFluidGauges = FLUID_INPUTS.getUnchecked(getAnimationTime());
			IDrawableStatic image;
			ScreenObjectWrapper wrapper;
			for (int i = 0; i < inFluidGauges.size(); i++) {
				image = inFluidGauges.get(i);
				wrapper = fluidInputs[i];
				image.draw(matrixStack, wrapper.getXPos(), wrapper.getYPos());
			}
		}
	}

	public void drawFluidOutputs(PoseStack matrixStack) {
		if (FLUID_OUTPUTS != null) {
			List<IDrawableStatic> fluidGauges = FLUID_OUTPUTS.getUnchecked(getAnimationTime());
			IDrawableStatic image;
			ScreenObjectWrapper wrapper;
			for (int i = 0; i < fluidGauges.size(); i++) {
				image = fluidGauges.get(i);
				wrapper = fluidOutputs[i];
				image.draw(matrixStack, wrapper.getXPos(), wrapper.getYPos());
			}
		}
	}

	public void drawStaticArrows(PoseStack matrixStack) {
		if (STATIC_ARROWS != null) {
			List<IDrawableStatic> arrows = STATIC_ARROWS.getUnchecked(getAnimationTime());
			IDrawableStatic image;
			ScreenObjectWrapper wrapper;
			for (int i = 0; i < arrows.size(); i++) {
				image = arrows.get(i);
				wrapper = staticArrows[i];
				image.draw(matrixStack, wrapper.getXPos(), wrapper.getYPos());
			}
		}
	}

	public void drawBackgroundExtras(PoseStack matrixStack) {
		if (BACKGROUND_EXTRA != null) {
			List<IDrawableStatic> extras = BACKGROUND_EXTRA.getUnchecked(getAnimationTime());
			IDrawableStatic image;
			ScreenObjectWrapper wrapper;
			for (int i = 0; i < extras.size(); i++) {
				image = extras.get(i);
				wrapper = backgroundExtra[i];
				image.draw(matrixStack, wrapper.getXPos(), wrapper.getYPos());
			}
		}
	}

	public void drawAnimatedArrows(PoseStack matrixStack) {
		if (ANIMATED_ARROWS != null) {
			List<IDrawableAnimated> arrows = ANIMATED_ARROWS.getUnchecked(getAnimationTime());
			IDrawableAnimated arrow;
			ScreenObjectWrapper wrapper;
			for (int i = 0; i < arrows.size(); i++) {
				arrow = arrows.get(i);
				wrapper = animArrows[i];
				arrow.draw(matrixStack, wrapper.getXPos(), wrapper.getYPos());
			}
		}
	}

	public List<List<ItemStack>> getItemInputs(T recipe) {
		return Collections.emptyList();
	}

	public List<List<FluidStack>> getFluidInputs(T recipe) {
		return Collections.emptyList();
	}

	public List<ItemStack> getItemOutputs(T recipe) {
		return Collections.emptyList();
	}

	public List<FluidStack> getFluidOutputs(T recipe) {
		return Collections.emptyList();
	}

}
