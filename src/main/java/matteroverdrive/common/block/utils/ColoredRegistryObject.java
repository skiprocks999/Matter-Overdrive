package matteroverdrive.common.block.utils;

import java.util.HashMap;
import java.util.function.Function;

import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;

public class ColoredRegistryObject<T extends IForgeRegistryEntry<T>> {

	private final HashMap<BlockColors, RegistryObject<T>> colorMap = new HashMap<>();

	public ColoredRegistryObject(Function<BlockColors, RegistryObject<T>> factory) {
		for (BlockColors color : BlockColors.values()) {
			colorMap.put(color, factory.apply(color));
		}
	}

	public RegistryObject<T> get(BlockColors color) {
		return colorMap.get(color);
	}

}
