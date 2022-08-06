package matteroverdrive.core.component.addons;

import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.api.client.IScreenAddonProvider;
import com.hrznstudio.titanium.component.IComponentHarness;
import com.hrznstudio.titanium.container.addon.IContainerAddon;
import com.hrznstudio.titanium.container.addon.IContainerAddonProvider;
import matteroverdrive.core.capability.types.matter.CapabilityMatterStorage;
import matteroverdrive.core.component.addons.matter.MatterContainerAddon;
import matteroverdrive.core.component.addons.matter.MatterScreenAddon;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MatterStorageComponent<T extends IComponentHarness> extends CapabilityMatterStorage
		implements IScreenAddonProvider, IContainerAddonProvider, INBTSerializable<CompoundTag> {

	private final int posX;
	private final int posY;
	private final String name;
	private T componentHarness;
	private Action action;

	public MatterStorageComponent(double maxStorage, Action defaultAction, int posX, int posY, String name) {
		super(maxStorage, defaultAction.canFill(), defaultAction.canDrain());
		this.posX = posX;
		this.posY = posY;
		this.name = name;
		this.action = defaultAction;
	}

	public MatterStorageComponent<T> setComponentHarness(T componentHarness) {
		this.componentHarness = componentHarness;
		return this;
	}

	public T getComponentHarness() {
		return componentHarness;
	}

	@Override
	public double receiveMatter(double maxReceive, boolean simulate) {
		double amount = super.receiveMatter(maxReceive, simulate);
		if (!simulate && amount > 0D) {
			this.update();
		}
		return amount;
	}

	@Override
	public double extractMatter(double maxExtract, boolean simulate) {
		double amount = super.extractMatter(maxExtract, simulate);
		if (!simulate && amount > 0) {
			this.update();
		}
		return amount;
	}

	@Override
	public void setMatterStored(double matterStored) {
		if (matterStored > this.getMaxMatterStored()) {
			this.currStorage = this.getMaxMatterStored();
		} else {
			this.currStorage = Math.max(matterStored, 0);
		}
		this.update();
	}

	@Override
	public boolean canExtract() {
		return this.action.canDrain();
	}

	@Override
	public boolean canReceive() {
		return this.action.canFill();
	}

	@NotNull
	@Override
	public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
		List<IFactory<? extends IScreenAddon>> addons = new ArrayList<>();
		addons.add(() -> new MatterScreenAddon(posX, posY, this));
		return addons;
	}

	@NotNull
	@Override
	public List<IFactory<? extends IContainerAddon>> getContainerAddons() {
		List<IFactory<? extends IContainerAddon>> addons = new ArrayList<>();
		addons.add(() -> new MatterContainerAddon(getComponentHarness()));
		return addons;
	}

	private void update() {
		if (this.componentHarness != null) {
			this.componentHarness.markComponentForUpdate(true);
		}
	}

	public String getName() {
		return name;
	}

	public int getX() {
		return posX;
	}

	public int getY() {
		return posY;
	}

	public enum Action {
		FILL(true, false), DRAIN(false, true), BOTH(true, true), NONE(false, false);

		private final boolean fill;
		private final boolean drain;

		Action(boolean fill, boolean drain) {
			this.fill = fill;
			this.drain = drain;
		}

		public boolean canFill() {
			return this.fill;
		}

		public boolean canDrain() {
			return this.drain;
		}
	}

}
