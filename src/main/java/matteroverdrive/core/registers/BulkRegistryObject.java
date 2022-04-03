package matteroverdrive.core.registers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import matteroverdrive.common.block.utils.BlockColors;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;

public class BulkRegistryObject<T extends IForgeRegistryEntry<T>> {

	private final HashMap<IBulkRegistryObject, RegistryObject<T>> objects = new HashMap<>();

	public BulkRegistryObject(Function<IBulkRegistryObject, RegistryObject<T>> factory, IBulkRegistryObject[] bulkValues) {
		for (IBulkRegistryObject val : bulkValues) {
			objects.put(val, factory.apply(val));
		}
	}

	public RegistryObject<T> get(IBulkRegistryObject val) {
		return objects.get(val);
	}
	
	public List<RegistryObject<T>> getAll(){
		return new ArrayList<>(objects.values());
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
