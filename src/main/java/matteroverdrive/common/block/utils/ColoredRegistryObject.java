package matteroverdrive.common.block.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	
	public List<RegistryObject<T>> getAll(){
		return new ArrayList<>(colorMap.values());
	}
	
	public <A> List<A> getAllObjects(){
		List<A> entries = new ArrayList<>();
		getAll().forEach(h -> entries.add((A) h.get()));
		return entries;
	}
	
	public <A> A[] getObjectsAsArray(A[] arr) {
		return this.<A>getAllObjects().toArray(arr);
	}

}
