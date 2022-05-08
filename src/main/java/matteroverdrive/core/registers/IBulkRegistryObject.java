package matteroverdrive.core.registers;

public interface IBulkRegistryObject {

	public String id();

	default String id(String id) {
		return id + id();
	}

}
