package matteroverdrive.core.block;

import java.util.function.Function;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class OverdriveBlockProperties extends BlockBehaviour.Properties {

	private boolean canBeWaterlogged = false;
	private boolean canBeLit = false;
	private boolean alwaysLit = false;
	//best solution to work with current system :L
	private boolean hasFacing = false;
	//if true, can face up and down
	private boolean omniDir = false;
	private boolean canConnectToRedstone = false;
	
	protected OverdriveBlockProperties(Material material, MaterialColor color) {
		super(material, color);
	}
	
	protected OverdriveBlockProperties(Material material, Function<BlockState, MaterialColor> function) {
		super(material, function);
	}
	
	private OverdriveBlockProperties(@Nonnull OverdriveBlockProperties properties) {
		this((BlockBehaviour.Properties)properties);

		canBeLit = properties.canBeLit;
		canBeWaterlogged = properties.canBeWaterlogged;
		hasFacing = properties.hasFacing;
		omniDir = properties.omniDir;
		alwaysLit = properties.alwaysLit;
		canConnectToRedstone = properties.canConnectToRedstone;
	}
	
	private OverdriveBlockProperties(@Nonnull BlockBehaviour.Properties properties) {
		super(properties.material, properties.materialColor);
		destroyTime = properties.destroyTime;
		explosionResistance = properties.explosionResistance;
		hasCollision = properties.hasCollision;
		isRandomlyTicking = properties.isRandomlyTicking;
		lightEmission = properties.lightEmission;
		materialColor = properties.materialColor;
		soundType = properties.soundType;
		friction = properties.friction;
		speedFactor = properties.speedFactor;
		dynamicShape = properties.dynamicShape;
		canOcclude = properties.canOcclude;
		isAir = properties.isAir;
		requiresCorrectToolForDrops = properties.requiresCorrectToolForDrops;
		offsetType = properties.offsetType;
	}
	
	public OverdriveBlockProperties setAlwaysLit() {
		alwaysLit = true;
		return this;
	}
	
	public OverdriveBlockProperties setCanBeLit() {
		canBeLit = true;
		return this;
	}

	public OverdriveBlockProperties setCanBeWaterlogged() {
		canBeWaterlogged = true;
		return this;
	}
	
	public OverdriveBlockProperties setHasFacing(boolean omniDir) {
		hasFacing = true;
		this.omniDir = omniDir;
		return this;
	}
	
	public OverdriveBlockProperties redstoneConnectivity() {
		canConnectToRedstone = true;
		return this;
	}

	public boolean canBeWaterlogged() {
		return canBeWaterlogged;
	}

	public boolean canBeLit() {
		return canBeLit;
	}
	
	public boolean hasFacing() {
		return hasFacing;
	}
	
	public boolean isOmniDirectional() {
		return omniDir;
	}
	
	public boolean isAlwaysLit() {
		return alwaysLit;
	}
	
	public boolean canConnectToRedstone() {
		return canConnectToRedstone;
	}
	
	public static OverdriveBlockProperties from(@Nonnull OverdriveBlockProperties properties) {
		return new OverdriveBlockProperties(properties);
	}
	
	public static OverdriveBlockProperties from(@Nonnull BlockBehaviour.Properties properties) {
		return new OverdriveBlockProperties(properties);
	}
	
	public static class Defaults {

		public static void init() {
		}

		/*
		// No Rotation

		public static final OverdriveBlockProperties defaultMachine(BlockBehaviour.Properties properties) {
			return from(properties);
		}

		public static final OverdriveBlockProperties litMachine(BlockBehaviour.Properties properties) {
			return from(properties).setCanBeLit();
		}

		public static final OverdriveBlockProperties waterloggableMachine(BlockBehaviour.Properties properties) {
			return from(properties).setCanBeWaterlogged();
		}

		public static final OverdriveBlockProperties waterloggableLit(BlockBehaviour.Properties properties) {
			return from(properties).setCanBeWaterlogged().setCanBeLit();
		}

		// Defaults with Four-Way & Six-Way Rotations
		
		public static final OverdriveBlockProperties defaultFourWay(BlockBehaviour.Properties properties) {
			return OverdriveBlockProperties.from(defaultMachine(properties)).setHasFacing(false);
		}

		public static final OverdriveBlockProperties defaultSixWay(BlockBehaviour.Properties properties) {
			return OverdriveBlockProperties.from(defaultMachine(properties)).setHasFacing(true);
		}

		public static final OverdriveBlockProperties litFourWay(BlockBehaviour.Properties properties) {
			return OverdriveBlockProperties.from(litMachine(properties)).setHasFacing(false);
		}

		public static final OverdriveBlockProperties litSixWay(BlockBehaviour.Properties properties) {
			return OverdriveBlockProperties.from(litMachine(properties)).setHasFacing(true);
		}

		public static final OverdriveBlockProperties waterloggableFourway(BlockBehaviour.Properties properties) {
			return OverdriveBlockProperties.from(waterloggableMachine(properties)).setHasFacing(false);
		}

		public static final OverdriveBlockProperties waterloggableSixway(BlockBehaviour.Properties properties) {
			return OverdriveBlockProperties.from(waterloggableMachine(properties)).setHasFacing(true);
		}

		public static final OverdriveBlockProperties waterloggableLitFourway(BlockBehaviour.Properties properties) {
			return OverdriveBlockProperties.from(waterloggableLit(properties)).setHasFacing(false);
		}

		public static final OverdriveBlockProperties waterloggableLitSixway(BlockBehaviour.Properties properties) {
			return OverdriveBlockProperties.from(waterloggableLit(properties)).setHasFacing(true);
		}
		*/
	}

}
