package matteroverdrive.common.block.states;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class OverdriveBlockStates {

	public static final List<EnumProperty<CableConnectionType>> CABLE_DIRECTIONS = new ArrayList<>();

	public static void init() {
		CABLE_DIRECTIONS.add(CABLE_DOWN);
		CABLE_DIRECTIONS.add(CABLE_UP);
		CABLE_DIRECTIONS.add(CABLE_NORTH);
		CABLE_DIRECTIONS.add(CABLE_SOUTH);
		CABLE_DIRECTIONS.add(CABLE_WEST);
		CABLE_DIRECTIONS.add(CABLE_EAST);
	}

	public static final EnumProperty<ChargerBlockPos> CHARGER_POS = EnumProperty.create("charger_pos",
			ChargerBlockPos.class);

	public static final EnumProperty<CableConnectionType> CABLE_DOWN = EnumProperty.create("cable_down",
			CableConnectionType.class);
	public static final EnumProperty<CableConnectionType> CABLE_UP = EnumProperty.create("cable_up",
			CableConnectionType.class);
	public static final EnumProperty<CableConnectionType> CABLE_NORTH = EnumProperty.create("cable_north",
			CableConnectionType.class);
	public static final EnumProperty<CableConnectionType> CABLE_SOUTH = EnumProperty.create("cable_south",
			CableConnectionType.class);
	public static final EnumProperty<CableConnectionType> CABLE_WEST = EnumProperty.create("cable_west",
			CableConnectionType.class);
	public static final EnumProperty<CableConnectionType> CABLE_EAST = EnumProperty.create("cable_east",
			CableConnectionType.class);

	public static final EnumProperty<VerticalFacing> VERTICAL_FACING = EnumProperty.create("vertical_facing",
			VerticalFacing.class);

	public enum CableConnectionType implements StringRepresentable {

		NONE, NONE_SEAMLESS, CABLE, INVENTORY, IGNORED;

		@Override
		public String getSerializedName() {
			return name().toLowerCase();
		}
	}

	public enum ChargerBlockPos implements StringRepresentable {
		BOTTOM, MIDDLE, TOP;

		@Override
		public String getSerializedName() {
			return name().toLowerCase();
		}
	}

	public enum VerticalFacing implements StringRepresentable {
		UP(Direction.UP), DOWN(Direction.DOWN), NONE(null);

		public final Direction mapped;

		private VerticalFacing(Direction dir) {
			mapped = dir;
		}

		@Override
		public String getSerializedName() {
			return name().toLowerCase();
		}

		public static VerticalFacing fromDirection(Direction direction) {
			for (VerticalFacing facing : values()) {
				if (facing.mapped == direction) {
					return facing;
				}
			}
			return NONE;
		}

	}

}
